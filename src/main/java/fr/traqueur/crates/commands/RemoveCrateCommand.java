package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RemoveCrateCommand extends Command<@NotNull CratesPlugin> {

    public RemoveCrateCommand(CratesPlugin plugin) {
        super(plugin, "remove");
        this.setPermission("zcrates.command.remove");
        this.setDescription("Remove a placed crate you are looking at.");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        CratesManager cratesManager = this.getPlugin().getManager(CratesManager.class);

        Optional<PlacedCrate> placedCrateOpt = Optional.empty();

        // Use raytracing to find entity or block
        RayTraceResult rayTraceResult = player.getWorld().rayTrace(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                5,
                FluidCollisionMode.NEVER,
                true,
                0.1,
                entity -> !entity.equals(player)
        );

        if (rayTraceResult != null) {
            // First check entity (priority over block since entities are in front)
            Entity targetEntity = rayTraceResult.getHitEntity();
            if (targetEntity != null) {
                placedCrateOpt = cratesManager.findPlacedCrateByEntity(targetEntity);
            }

            // If not found, check block
            if (placedCrateOpt.isEmpty()) {
                Block targetBlock = rayTraceResult.getHitBlock();
                if (targetBlock != null) {
                    placedCrateOpt = cratesManager.findPlacedCrateByBlock(targetBlock);
                }
            }
        }

        if (placedCrateOpt.isEmpty()) {
            Messages.NO_CRATE_FOUND.send(player);
            return;
        }

        PlacedCrate placedCrate = placedCrateOpt.get();
        cratesManager.removePlacedCrate(placedCrate);

        Messages.CRATE_REMOVED.send(player, Placeholder.parsed("crate", placedCrate.crateId()));
    }
}