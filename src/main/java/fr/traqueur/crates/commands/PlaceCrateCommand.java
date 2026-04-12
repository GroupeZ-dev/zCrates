package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.commands.completers.EntityTypeTabCompleter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceCrateCommand extends Command<@NotNull CratesPlugin> {

    public PlaceCrateCommand(CratesPlugin plugin) {
        super(plugin, "place");
        this.setPermission("zcrates.command.place");
        this.setDescription("Place a crate at your location.");
        this.setGameOnly(true);
        this.addArg("crate", Crate.class);
        this.addArg("displayType", DisplayType.class);
        this.addArg("displayValue", String.class, new EntityTypeTabCompleter());
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        Crate crate = arguments.get("crate");
        DisplayType displayType = arguments.get("displayType");
        String displayValue = arguments.get("displayValue");

        CrateDisplayFactoriesRegistry displayRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        if (displayRegistry.getById(displayType) == null) {
            Messages.DISPLAY_TYPE_NOT_AVAILABLE.send(player, Placeholder.parsed("type", displayType.name()));
            return;
        }

        Location location = player.getLocation().getBlock().getLocation();
        float yaw = player.getLocation().getYaw();

        CratesManager cratesManager = this.getPlugin().getManager(CratesManager.class);
        cratesManager.placeCrate(crate.id(), location, displayType, displayValue, yaw);

        Messages.CRATE_PLACED.send(player,
                Placeholder.parsed("crate", crate.id()),
                Placeholder.parsed("type", displayType.name())
        );
    }
}