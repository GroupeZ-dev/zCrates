package fr.traqueur.crates.models.rewards;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import org.bukkit.entity.Player;

import java.util.List;

public record CommandsListReward(String id, ItemStackWrapper displayItem, double weight, List<String> commands) implements Reward {

    @Override
    public void give(Player player) {
        Logger.debug("Giving command reward {} to player {}", id, player.getName());
        for (String command : commands) {
            executeCommand(player, command);
        }
    }

    private void executeCommand(Player player, String command) {
        String executedCommand = command.replace("%player%", player.getName());
        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), executedCommand);
    }
}
