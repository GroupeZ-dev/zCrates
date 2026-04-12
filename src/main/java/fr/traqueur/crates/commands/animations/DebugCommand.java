package fr.traqueur.crates.commands.animations;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.crates.models.ZCrate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class DebugCommand extends Command<@NotNull CratesPlugin> {


    public DebugCommand(CratesPlugin plugin) {
        super(plugin, "debug");
        this.setDescription("Debug an animation");
        this.setPermission("crates.command.animation.debug");

        this.addArg("animation", Animation.class);
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        CratesManager cratesManager = this.getPlugin().getManager(CratesManager.class);

        Animation animation = arguments.get("animation");
        Player player = (Player) sender;
        Crate crate = Registry.get(CratesRegistry.class).getAll().getFirst();
        if (crate == null) {
            MessagesService.sendMessage(player, "<red>No crate found to debug the animation.");
            return;
        }
        cratesManager.openCrate(player, crate, animation);
    }
}
