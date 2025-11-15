package fr.traqueur.crates.models.rewards;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.Reward;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record CommandReward(String id, ItemStackWrapper displayItem, double weight, String command) implements Reward {

    @Override
    public void give(Player player) {
        Logger.debug("Giving command reward {} to player {}", id, player.getName());
        String executedCommand = command.replace("%player%", player.getName());
        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), executedCommand);
    }
}
