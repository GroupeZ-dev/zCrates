package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GiveKeysCommand extends Command<@NotNull CratesPlugin> {

    public GiveKeysCommand(CratesPlugin plugin) {
        super(plugin, "givekeys");
        this.setPermission("crates.command.givekeys");
        this.setDescription("Give keys to a player");

        this.addArgs("player", Player.class);
        this.addArgs("crate", Crate.class);
        this.addArgs("amount", Integer.class, (sender, args) -> List.of("1", "5", "10", "64"));
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player player = arguments.get("player");
        Crate crate = arguments.get("crate");
        int amount = arguments.get("amount");

        if (amount <= 0) {
            Messages.INVALID_AMOUNT.send(sender);
            return;
        }

        Key key = crate.key();
        for (int i = 0; i < amount; i++) {
            key.give(player);
        }

        Messages.KEYS_GIVEN.send(sender,
                Placeholder.parsed("amount", String.valueOf(amount)),
                Placeholder.parsed("crate", crate.displayName()),
                Placeholder.parsed("player", player.getName())
        );
    }
}