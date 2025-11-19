package fr.traqueur.crates.managers;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.User;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.listeners.CratesListener;
import fr.traqueur.crates.models.placedcrates.EntityCrateDisplay;
import fr.traqueur.crates.models.wrappers.CrateWrapper;
import fr.traqueur.crates.models.wrappers.InventoryWrapper;
import fr.traqueur.crates.models.wrappers.PlayerWrapper;
import fr.traqueur.crates.api.serialization.Keys;
import fr.traqueur.crates.views.CrateMenu;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ZCratesManager implements CratesManager {

    private final InventoryManager inventoryManager;
    private final AnimationExecutor animationExecutor;
    private final Map<UUID, OpenedCrate> openingCrates;

    // Placed crates management - single cache combining data and display
    private final Map<Location, ActivePlacedCrate> placedCratesCache;

    @Override
    public void init() {
        this.getPlugin().registerListener(new CratesListener(this));
        this.ensureInventoriesExist();
        this.loadAllPlacedCrates();
    }

    private static class OpenedCrate {
        protected final Crate crate;
        protected final Animation animation;
        protected UUID animationId;
        public OpenedCrate(Crate crate, Animation animation) {
            this.crate = crate;
            this.animation = animation;
        }
    }

    private record ActivePlacedCrate(PlacedCrate data, CrateDisplay<?> display) {}

    public ZCratesManager(InventoryManager inventoryManager) {
        this.animationExecutor = new AnimationExecutor(this.getPlugin());
        this.inventoryManager = inventoryManager;
        this.openingCrates = new HashMap<>();
        this.placedCratesCache = new ConcurrentHashMap<>();
    }

    @Override
    public void openCrate(Player player, Crate crate, Animation animation) {
        if(this.openingCrates.containsKey(player.getUniqueId())) {
            return;
        }
        this.openingCrates.put(player.getUniqueId(), new OpenedCrate(crate, animation));
        this.inventoryManager.openInventory(player, crate.relatedMenu());
    }

    @Override
    public void startAnimation(Player player, Inventory inventory, List<Integer> slots) {
        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        Crate crate = openedCrate.crate;
        Reward reward = crate.generateReward();

        // Log the crate opening
        UsersManager usersManager = this.getPlugin().getManager(UsersManager.class);
        User user = usersManager.getUser(player.getUniqueId());
        user.addCrateOpening(crate.id(), reward.id());

        InventoryWrapper inventoryWrapper = new InventoryWrapper(this.getPlugin(), player, crate, inventory, slots);
        CrateWrapper crateWrapper = new CrateWrapper(crate, player, reward);
        openedCrate.animationId = this.animationExecutor.startAnimation(openedCrate.animation, new AnimationContext(playerWrapper, inventoryWrapper, crateWrapper), () -> reward.give(player));
        this.openingCrates.put(player.getUniqueId(), openedCrate);
    }

    @Override
    public void stopAllOpening() {
        this.animationExecutor.cancelAll();
        this.openingCrates.forEach((uuid, openedCrate) -> {
            Player player = this.getPlugin().getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.closeInventory();
            }
        });
    }

    @Override
    public void closeCrate(Player player) {
        OpenedCrate instance = this.openingCrates.remove(player.getUniqueId());
        if (instance != null) {
            UUID animationId = instance.animationId;
            if (animationId != null && this.animationExecutor.isRunning(animationId)) {
                this.animationExecutor.cancelAnimation(animationId);
            }
        }
    }

    @Override
    public void ensureInventoriesExist() {
        this.inventoryManager.deleteInventories(this.getPlugin());
        CratesRegistry registry = Registry.get(CratesRegistry.class);
        for (Crate crate : registry.getAll()) {
            if (this.inventoryManager.getInventory(this.getPlugin(), crate.relatedMenu()).isEmpty()) {
                try {
                    this.inventoryManager.loadInventoryOrSaveResource(this.getPlugin(), "inventories/" + crate.relatedMenu() + ".yml", CrateMenu.class);
                } catch (InventoryException e) {
                    Logger.warning("Failed to load or create inventory for crate '{}': {}", crate.id(), e.getMessage());
                }
            }
        }
    }

    @Override
    public PlacedCrate placeCrate(String crateId, Location location, DisplayType displayType, String displayValue, float yaw) {
        PlacedCrate placedCrate = new PlacedCrate(
                UUID.randomUUID(),
                crateId,
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                displayType,
                displayValue,
                yaw
        );

        // Spawn the display
        CrateDisplayFactory<?> factory = Registry.get(CrateDisplayFactoriesRegistry.class).getById(displayType);
        if (factory == null) {
            throw new IllegalArgumentException("No display factory registered for type: " + displayType);
        }

        CrateDisplay<?> display = factory.create(location, displayValue, yaw);
        display.spawn();

        // Cache
        Location blockLocation = location.getBlock().getLocation();
        this.placedCratesCache.put(blockLocation, new ActivePlacedCrate(placedCrate, display));

        // Save to chunk PDC
        this.saveToChunk(location.getChunk());

        Logger.info("Placed crate '{}' at {} with display type {}", crateId, blockLocation, displayType);
        return placedCrate;
    }

    @Override
    public void removePlacedCrate(PlacedCrate placedCrate) {
        Location location = placedCrate.getLocation();
        if (location == null) {
            Logger.warning("Cannot remove placed crate: world '{}' not loaded", placedCrate.worldName());
            return;
        }

        Location blockLocation = location.getBlock().getLocation();

        // Remove from cache and display
        ActivePlacedCrate active = this.placedCratesCache.remove(blockLocation);
        if (active != null && active.display() != null) {
            active.display().remove();
        }

        // Update chunk PDC
        this.saveToChunk(location.getChunk());

        Logger.info("Removed placed crate '{}' at {}", placedCrate.crateId(), blockLocation);
    }

    @Override
    public Optional<PlacedCrate> findPlacedCrateByBlock(Block block) {
        Location blockLocation = block.getLocation();
        ActivePlacedCrate cached = this.placedCratesCache.get(blockLocation);
        if (cached != null) {
            return Optional.of(cached.data());
        }

        // Check if any block display matches
        return this.placedCratesCache.values().stream()
                .filter(activePlacedCrate -> activePlacedCrate.display() != null
                        && activePlacedCrate.display().getLocation().equals(blockLocation))
                .map(ActivePlacedCrate::data)
                .findFirst();
    }

    @Override
    public Optional<PlacedCrate> findPlacedCrateByEntity(Entity entity) {
        return this.placedCratesCache.values().stream()
                .filter(active -> {
                    CrateDisplay<?> display = active.display();
                    if (display instanceof EntityCrateDisplay entityDisplay) {
                        return entityDisplay.matches(entity);
                    }
                    return false;
                })
                .map(ActivePlacedCrate::data)
                .findFirst();
    }

    @Override
    public void loadPlacedCratesFromChunk(Chunk chunk) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        List<PlacedCrate> placedCrates = Keys.PLACED_CRATES.get(pdc, new ArrayList<>());

        for (PlacedCrate placedCrate : placedCrates) {
            Location location = placedCrate.getLocation();
            if (location == null) {
                continue;
            }

            Location blockLocation = location.getBlock().getLocation();

            // Skip if already loaded
            if (this.placedCratesCache.containsKey(blockLocation)) {
                continue;
            }

            // Spawn display
            CrateDisplayFactory<?> factory = Registry.get(CrateDisplayFactoriesRegistry.class).getById(placedCrate.displayType());
            CrateDisplay<?> display = null;
            if (factory != null) {
                display = factory.create(location, placedCrate.displayValue(), placedCrate.yaw());
                display.spawn();
            }

            this.placedCratesCache.put(blockLocation, new ActivePlacedCrate(placedCrate, display));
        }

        if (!placedCrates.isEmpty()) {
            Logger.debug("Loaded {} placed crates from chunk ({}, {})", placedCrates.size(), chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void unloadPlacedCratesFromChunk(Chunk chunk) {
        List<Location> toRemove = this.placedCratesCache.keySet().stream()
                .filter(loc -> loc.getWorld().equals(chunk.getWorld())
                        && loc.getBlockX() >> 4 == chunk.getX()
                        && loc.getBlockZ() >> 4 == chunk.getZ())
                .toList();

        for (Location location : toRemove) {
            ActivePlacedCrate active = this.placedCratesCache.remove(location);
            if (active != null && active.display() != null) {
                active.display().remove();
            }
        }

        if (!toRemove.isEmpty()) {
            Logger.debug("Unloaded {} placed crates from chunk ({}, {})", toRemove.size(), chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public void loadAllPlacedCrates() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                this.loadPlacedCratesFromChunk(chunk);
            }
        }
        Logger.info("Loaded all placed crates from {} worlds", Bukkit.getWorlds().size());
    }

    @Override
    public void unloadAllPlacedCrates() {
        for (ActivePlacedCrate active : this.placedCratesCache.values()) {
            if (active.display() != null) {
                active.display().remove();
            }
        }
        this.placedCratesCache.clear();
        Logger.info("Unloaded all placed crates");
    }

    @Override
    public List<PlacedCrate> getPlacedCratesInWorld(World world) {
        return this.placedCratesCache.values().stream()
                .map(ActivePlacedCrate::data)
                .filter(crate -> crate.worldName().equals(world.getName()))
                .collect(Collectors.toList());
    }

    private void saveToChunk(Chunk chunk) {
        List<PlacedCrate> cratesInChunk = this.placedCratesCache.entrySet().stream()
                .filter(entry -> {
                    Location loc = entry.getKey();
                    return loc.getWorld().equals(chunk.getWorld())
                            && loc.getBlockX() >> 4 == chunk.getX()
                            && loc.getBlockZ() >> 4 == chunk.getZ();
                })
                .map(entry -> entry.getValue().data())
                .toList();

        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        Keys.PLACED_CRATES.set(pdc, cratesInChunk);
    }
}

