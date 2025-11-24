package fr.traqueur.crates.managers;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.events.*;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.OpenCondition;
import fr.traqueur.crates.api.managers.UsersManager;
import fr.traqueur.crates.api.models.CrateOpening;
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
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.models.conditions.CooldownCondition;
import fr.traqueur.crates.models.placedcrates.EntityCrateDisplay;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import fr.traqueur.crates.models.wrappers.CrateWrapper;
import fr.traqueur.crates.models.wrappers.InventoryWrapper;
import fr.traqueur.crates.models.wrappers.PlayerWrapper;
import fr.traqueur.crates.api.serialization.Keys;
import fr.traqueur.crates.views.CrateMenu;
import org.apache.commons.lang3.time.DurationUtils;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ZCratesManager implements CratesManager {

    private final InventoryManager inventoryManager;
    private final AnimationExecutor animationExecutor;
    private final Map<UUID, OpenedCrate> openingCrates;
    private final Map<UUID, Crate> previewingCrates;

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
        protected Reward currentReward;
        protected int rerollsRemaining;
        protected boolean animationCompleted;
        // Stored for reroll to restart animation
        protected Inventory inventory;
        protected List<Integer> slots;

        public OpenedCrate(Crate crate, Animation animation) {
            this.crate = crate;
            this.animation = animation;
            this.rerollsRemaining = crate.maxRerolls();
            this.animationCompleted = false;
        }
    }

    private record ActivePlacedCrate(PlacedCrate data, CrateDisplay<?> display) {}

    public ZCratesManager(InventoryManager inventoryManager) {
        this.animationExecutor = new AnimationExecutor(this.getPlugin());
        this.inventoryManager = inventoryManager;
        this.openingCrates = new ConcurrentHashMap<>();
        this.previewingCrates = new ConcurrentHashMap<>();
        this.placedCratesCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean tryOpenCrate(Player player, Crate crate) {
        if (!crate.key().has(player)) {
            return false;
        }

        // Check all conditions
        for (OpenCondition condition : crate.conditions()) {
            if (!condition.check(player, crate)) {
                this.sendConditionError(player, crate, condition);
                return false;
            }
        }

        // Fire pre-open event (cancellable)
        CratePreOpenEvent preOpenEvent = new CratePreOpenEvent(player, crate);
        Bukkit.getPluginManager().callEvent(preOpenEvent);
        if (preOpenEvent.isCancelled()) {
            return false;
        }

        crate.key().remove(player);

        // Call onOpen for all conditions (e.g., set cooldown)
        for (OpenCondition condition : crate.conditions()) {
            condition.onOpen(player, crate);
        }

        this.openCrate(player, crate, crate.animation());
        return true;
    }

    private void sendConditionError(Player player, Crate crate, OpenCondition condition) {
        String errorKey = condition.errorMessageKey();
        switch (errorKey) {
            case "no-permission" -> Messages.CONDITION_NO_PERMISSION.send(player);
            case "cooldown" -> {
                if (condition instanceof CooldownCondition cooldownCondition) {
                    long remaining = cooldownCondition.getRemainingCooldown(player, crate);
                    String formattedTime = formatDuration(remaining);
                    Messages.CONDITION_COOLDOWN.send(player, Placeholder.parsed("time", formattedTime));
                } else {
                    Messages.CONDITION_COOLDOWN.send(player, Placeholder.parsed("time", "unknown"));
                }
            }
            default -> Logger.warning("Unknown condition error key: {}", errorKey);
        }
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) {
            return seconds + "s";
        }
        long minutes = seconds / 60;
        seconds = seconds % 60;
        if (minutes < 60) {
            return minutes + "m " + seconds + "s";
        }
        long hours = minutes / 60;
        minutes = minutes % 60;
        return hours + "h " + minutes + "m";
    }

    @Override
    public void openCrate(Player player, Crate crate, Animation animation) {
        if(this.openingCrates.containsKey(player.getUniqueId())) {
            return;
        }
        this.openingCrates.put(player.getUniqueId(), new OpenedCrate(crate, animation));
        this.inventoryManager.openInventory(player, crate.relatedMenu());

        // Fire open event (after menu is opened)
        CrateOpenEvent openEvent = new CrateOpenEvent(player, crate, animation);
        Bukkit.getPluginManager().callEvent(openEvent);
    }

    @Override
    public void openPreview(Player player, Crate crate) {
        this.previewingCrates.put(player.getUniqueId(), crate);
        String previewMenu = crate.relatedMenu() + "-preview";
        if (this.inventoryManager.getInventory(this.getPlugin(), previewMenu).isEmpty()) {
            previewMenu = "crate-preview";
        }
        this.inventoryManager.openInventory(player, previewMenu);
    }

    @Override
    public Optional<Crate> getPreviewingCrate(Player player) {
        return Optional.ofNullable(this.previewingCrates.get(player.getUniqueId()));
    }

    @Override
    public void closePreview(Player player) {
        this.previewingCrates.remove(player.getUniqueId());
    }

    @Override
    public void startAnimation(Player player, Inventory inventory, List<Integer> slots) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());

        // Store inventory and slots for potential reroll
        openedCrate.inventory = inventory;
        openedCrate.slots = slots;
        openedCrate.animationCompleted = false;

        this.runAnimation(player, openedCrate, inventory, slots);
    }

    private void runAnimation(Player player, OpenedCrate openedCrate, Inventory inventory, List<Integer> slots) {
        // Determine if this is a reroll (animation already started once before)
        boolean isReroll = openedCrate.animationId != null;

        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        Crate crate = openedCrate.crate;

        // Get user before generating reward (needed for algorithm context)
        UsersManager usersManager = this.getPlugin().getManager(UsersManager.class);
        User user = usersManager.getUser(player.getUniqueId());

        // Generate reward using the algorithm with user context
        Reward reward = crate.generateReward(user);
        openedCrate.currentReward = reward;

        // Fire reward generated event
        RewardGeneratedEvent rewardEvent = new RewardGeneratedEvent(player, crate, reward, isReroll);
        Bukkit.getPluginManager().callEvent(rewardEvent);

        // Log the crate opening (dual-write: memory + immediate async DB persist)
        CrateOpening crateOpening = user.addCrateOpening(crate.id(), reward.id());
        usersManager.persistCrateOpening(crateOpening);

        InventoryWrapper inventoryWrapper = new InventoryWrapper(this.getPlugin(), player, crate, inventory, slots);
        // Pass supplier to get live rerolls remaining count
        CrateWrapper crateWrapper = new CrateWrapper(crate, player, reward, () -> openedCrate.rerollsRemaining);

        openedCrate.animationId = this.animationExecutor.startAnimation(openedCrate.animation, new AnimationContext(playerWrapper, inventoryWrapper, crateWrapper), () -> {
            openedCrate.animationCompleted = true;
        });
    }

    @Override
    public boolean canReroll(Player player) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        if (openedCrate == null) {
            return false;
        }
        return openedCrate.animationCompleted && openedCrate.rerollsRemaining > 0;
    }

    @Override
    public int getRerollsRemaining(Player player) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        if (openedCrate == null) {
            return 0;
        }
        return openedCrate.rerollsRemaining;
    }

    @Override
    public Optional<Reward> getCurrentReward(Player player) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        if (openedCrate == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(openedCrate.currentReward);
    }

    @Override
    public boolean reroll(Player player) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        if (openedCrate == null || !openedCrate.animationCompleted || openedCrate.rerollsRemaining <= 0) {
            return false;
        }

        // Fire reroll event (cancellable)
        CrateRerollEvent rerollEvent = new CrateRerollEvent(player, openedCrate.crate, openedCrate.currentReward, openedCrate.rerollsRemaining - 1);
        Bukkit.getPluginManager().callEvent(rerollEvent);
        if (rerollEvent.isCancelled()) {
            return false;
        }

        // Decrement rerolls
        openedCrate.rerollsRemaining--;

        // Cancel current animation if still running
        if (openedCrate.animationId != null && this.animationExecutor.isRunning(openedCrate.animationId)) {
            this.animationExecutor.cancelAnimation(openedCrate.animationId);
        }

        Logger.debug("Player {} rerolling ({} rerolls remaining)", player.getName(), openedCrate.rerollsRemaining);

        // Restart the full animation with a new reward
        this.runAnimation(player, openedCrate, openedCrate.inventory, openedCrate.slots);

        return true;
    }


    @Override
    public boolean isAnimationCompleted(Player player) {
        OpenedCrate openedCrate = this.openingCrates.get(player.getUniqueId());
        return openedCrate != null && openedCrate.animationCompleted;
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
            // If animation was completed, give the current reward on close
            if (instance.currentReward != null) {
                instance.currentReward.give(player);
                Logger.debug("Player {} received reward {} on inventory close", player.getName(), instance.currentReward.id());

                // Fire reward given event
                RewardGivenEvent givenEvent = new RewardGivenEvent(player, instance.crate, instance.currentReward);
                Bukkit.getPluginManager().callEvent(givenEvent);
            }
        }
    }

    @Override
    public void ensureInventoriesExist() {
        this.inventoryManager.deleteInventories(this.getPlugin());
        CratesRegistry registry = Registry.get(CratesRegistry.class);

        for (Crate crate : registry.getAll()) {
            try {
                if (this.inventoryManager.getInventory(this.getPlugin(), crate.relatedMenu()).isEmpty()) {
                    this.inventoryManager.loadInventoryOrSaveResource(this.getPlugin(), "inventories/" + crate.relatedMenu() + ".yml", CrateMenu.class);
                }
                if (this.inventoryManager.getInventory(this.getPlugin(), crate.relatedMenu() + "-preview").isEmpty()) {
                    this.inventoryManager.loadInventoryOrSaveResource(this.getPlugin(), "inventories/preview/" + crate.relatedMenu() + "-preview.yml");
                }
            } catch (InventoryException e) {
                Logger.warning("Failed to load or create inventory for crate '{}': {}", crate.id(), e.getMessage());
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

