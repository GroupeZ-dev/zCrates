package fr.traqueur.crates.commands.completers;

import fr.traqueur.commands.api.arguments.TabCompleter;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EntityTypeTabCompleter implements TabCompleter<CommandSender> {
    @Override
    public List<String> onCompletion(CommandSender sender, List<String> args) {
        if(args.size() < 2) {
            return List.of();
        }
        String lastArg = args.get(args.size() - 2).toUpperCase();
        DisplayType displayType;
        try {
            displayType = DisplayType.valueOf(lastArg);
        } catch (IllegalArgumentException e) {
            System.out.println("EntityTypeTabCompleter: Invalid DisplayType " + lastArg);
            return List.of();
        }
        CrateDisplayFactoriesRegistry displayRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        CrateDisplayFactory<?> factory = displayRegistry.getById(displayType);
        return factory == null ? List.of() : factory.getSuggestions();
    }
}
