package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PurgeCratesCommand extends Command<@NotNull CratesPlugin> {

    public PurgeCratesCommand(CratesPlugin plugin) {
        super(plugin, "purge");
        this.setPermission("zcrates.command.purge");
        this.setDescription("Purge all placed crates in the current chunk.");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        Chunk chunk = player.getLocation().getChunk();

        CratesManager cratesManager = this.getPlugin().getManager(CratesManager.class);

        List<PlacedCrate> cratesInChunk = cratesManager.getPlacedCratesInWorld(chunk.getWorld()).stream()
                .filter(crate -> {
                    int crateChunkX = crate.x() >> 4;
                    int crateChunkZ = crate.z() >> 4;
                    return crateChunkX == chunk.getX() && crateChunkZ == chunk.getZ();
                })
                .toList();

        if (cratesInChunk.isEmpty()) {
            Messages.NO_CRATES_IN_CHUNK.send(player);
            return;
        }

        int count = cratesInChunk.size();
        for (PlacedCrate placedCrate : cratesInChunk) {
            cratesManager.removePlacedCrate(placedCrate);
        }

        Messages.CRATES_PURGED.send(player, Placeholder.parsed("count", String.valueOf(count)));
    }
}