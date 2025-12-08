package fr.traqueur.crates.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabCompleter;
import fr.traqueur.crates.api.models.crates.Crate;
import fr.traqueur.crates.api.registries.CratesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CrateArgument implements ArgumentConverter<Crate>, TabCompleter<CommandSender> {
    @Override
    public Crate apply(String s) {
        return Registry.get(CratesRegistry.class).getById(s);
    }

    @Override
    public List<String> onCompletion(CommandSender sender, List<String> args) {
        return Registry.get(CratesRegistry.class).getAll().stream().map(Crate::id).toList();
    }
}