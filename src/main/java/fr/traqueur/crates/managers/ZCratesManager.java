package fr.traqueur.crates.managers;

import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.models.wrappers.CrateWrapper;
import fr.traqueur.crates.models.wrappers.InventoryWrapper;
import fr.traqueur.crates.models.wrappers.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ZCratesManager implements CratesManager {

    private final AnimationExecutor animationExecutor;

    public ZCratesManager() {
        this.animationExecutor = new AnimationExecutor(this.getPlugin());
    }

    @Override
    public void openCrate(Player player, Crate crate, Reward reward, Animation animation, Inventory inventory) {
        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        InventoryWrapper inventoryWrapper = new InventoryWrapper(this.getPlugin(), player, crate, inventory);
        CrateWrapper crateWrapper = new CrateWrapper(crate, player, reward);
        this.animationExecutor.startAnimation(animation, new AnimationContext(playerWrapper, inventoryWrapper, crateWrapper), () -> {
            reward.give(player);
        });
    }

    @Override
    public void stopAllOpening() {
        this.animationExecutor.cancelAll();
    }

}
