package fr.traqueur.crates.commands.animations;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.AnimationsManager;
import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DebugCommand extends Command<CratesPlugin> {


    public DebugCommand(CratesPlugin plugin) {
        super(plugin, "debug");
        this.setDescription("Debug an animation");
        this.setPermission("crates.command.animation.debug");

        this.addArgs("animation", Animation.class);
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        AnimationsManager animationsManager = this.getPlugin().getManager(AnimationsManager.class);

        Animation animation = arguments.get("animation");
        Player player = (Player) sender;
        Crate crate = Registry.get(CratesRegistry.class).getAll().getFirst();
        Inventory inventory = crate.inventory(player);
        player.openInventory(inventory);
        animationsManager.startAnimation(player, crate, animation, inventory);
    }
}
