package fr.traqueur.crates.managers;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.models.wrappers.CrateWrapper;
import fr.traqueur.crates.models.wrappers.InventoryWrapper;
import fr.traqueur.crates.models.wrappers.PlayerWrapper;
import fr.traqueur.crates.views.CrateMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZCratesManager implements CratesManager {

    private final InventoryManager inventoryManager;
    private final AnimationExecutor animationExecutor;
    private final Map<UUID, OpenedCrate> openingCrates;

    private static class OpenedCrate {
        protected final Crate crate;
        protected final Animation animation;
        protected UUID animationId;
        public OpenedCrate(Crate crate, Animation animation) {
            this.crate = crate;
            this.animation = animation;
        }
    }

    public ZCratesManager(InventoryManager inventoryManager) {
        this.animationExecutor = new AnimationExecutor(this.getPlugin());
        this.inventoryManager = inventoryManager;
        this.openingCrates = new HashMap<>();
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

}
