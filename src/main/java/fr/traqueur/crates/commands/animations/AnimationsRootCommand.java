package fr.traqueur.crates.commands.animations;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AnimationsRootCommand extends Command<@NotNull CratesPlugin> {

    public AnimationsRootCommand(CratesPlugin plugin) {
        super(plugin, "animations");
        this.setDescription("Manage crate animations");
        this.setPermission("crates.command.animations");
        this.addSubCommand(new DebugCommand(plugin));
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {}
}
