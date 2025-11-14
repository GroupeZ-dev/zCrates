package fr.traqueur.crates.commands.animations;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.animations.AnimationExecutor;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.models.CrateWrapper;
import fr.traqueur.crates.models.InventoryWrapper;
import fr.traqueur.crates.models.PlayerWrapper;
import org.bukkit.Bukkit;
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
        Animation animation = arguments.get("animation");
        Player player = (Player) sender;
        Inventory inventory = Bukkit.createInventory(player, 36, "Crate Animation");
        AnimationExecutor executor = new AnimationExecutor(this.getPlugin());
        player.openInventory(inventory);
        executor.startAnimation(animation, new AnimationContext(new PlayerWrapper(player), new InventoryWrapper(this.getPlugin(), player, inventory) , new CrateWrapper(null)));
    }
}
