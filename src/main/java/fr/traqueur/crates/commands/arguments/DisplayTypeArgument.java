package fr.traqueur.crates.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabCompleter;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class DisplayTypeArgument implements ArgumentConverter<DisplayType>, TabCompleter<CommandSender> {

    @Override
    public DisplayType apply(String s) {
        try {
            return DisplayType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<String> onCompletion(CommandSender sender, List<String> args) {
        CrateDisplayFactoriesRegistry registry = Registry.get(CrateDisplayFactoriesRegistry.class);
        return Arrays.stream(DisplayType.values())
                .filter(type -> registry.getById(type) != null)
                .map(DisplayType::name)
                .toList();
    }
}