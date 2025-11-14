package fr.traqueur.crates.managers;

import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.managers.AnimationsManager;
import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.models.wrappers.CrateWrapper;
import fr.traqueur.crates.models.wrappers.InventoryWrapper;
import fr.traqueur.crates.models.wrappers.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ZAnimationsManager implements AnimationsManager {

    private final AnimationExecutor animationExecutor;

    public ZAnimationsManager() {
        this.animationExecutor = new AnimationExecutor(this.getPlugin());
    }

    @Override
    public void startAnimation(Player player, Crate crate, Animation animation, Inventory inventory) {
        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        InventoryWrapper inventoryWrapper = new InventoryWrapper(this.getPlugin(), player, inventory);
        CrateWrapper crateWrapper = new CrateWrapper(crate);
        this.animationExecutor.startAnimation(animation, new AnimationContext(playerWrapper, inventoryWrapper, crateWrapper));
    }

    @Override
    public void stopAllAnimations() {
        this.animationExecutor.cancelAll();
    }

}
