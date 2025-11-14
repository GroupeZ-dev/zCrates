package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.commands.animations.DebugCommand;
import org.bukkit.command.CommandSender;

public class AnimationsCommand extends Command<CratesPlugin> {

    public AnimationsCommand(CratesPlugin plugin) {
        super(plugin, "animations");
        this.setDescription("Manage crate animations");
        this.setPermission("crates.command.animations");
        this.addSubCommand(new DebugCommand(plugin));
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {}
}
