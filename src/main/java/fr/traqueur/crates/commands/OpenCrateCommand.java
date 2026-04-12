package fr.traqueur.crates.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.models.crates.OpenResult;
import fr.traqueur.crates.utils.OpenResultHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OpenCrateCommand extends Command<@NotNull CratesPlugin> {

    public OpenCrateCommand(@NotNull CratesPlugin plugin) {
        super(plugin, "open");

        this.setPermission("crates.command.open");
        this.setDescription("Force a player to open a crate");

        this.addArg("player", Player.class);
        this.addArg("crate", Crate.class);
        this.addOptionalArg("force", boolean.class);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player target = arguments.get("player");
        Crate crate = arguments.get("crate");
        Optional<Boolean> forceOpt = arguments.getOptional("force");
        boolean force = forceOpt.orElse(false);

        CratesManager cratesManager = this.getPlugin().getManager(CratesManager.class);
        if(force) {
            cratesManager.openCrate(target, crate, crate.animation());
            return;
        }

        OpenResult result = cratesManager.tryOpenCrate(target, crate);
        if (result.isError()) {
            OpenResultHandler.getInstance().sendError(target, crate, result);
        }
    }
}
