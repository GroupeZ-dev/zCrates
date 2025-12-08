package fr.traqueur.crates.models.rewards;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record ItemReward(String id, ItemStackWrapper displayItem, double weight, ItemStackWrapper item) implements Reward {
    @Override
    public void give(Player player) {
        Logger.debug("Giving item reward {} to player {}", id, player.getName());
        ItemStack itemBuild = item.build(player);
        player.getInventory().addItem(itemBuild).forEach((__, dropped) -> player.getWorld().dropItemNaturally(player.getLocation(), dropped));
    }
}
