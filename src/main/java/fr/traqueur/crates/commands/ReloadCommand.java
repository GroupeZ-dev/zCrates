package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends Command<@NotNull CratesPlugin> {

    public ReloadCommand(CratesPlugin plugin) {
        super(plugin, "reload");
        this.setPermission("crates.command.reload");
        this.setDescription("Reloads the plugin");
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        this.getPlugin().reloadConfig();
        sender.sendMessage("§aCrates plugin reloaded successfully.");
    }
}
