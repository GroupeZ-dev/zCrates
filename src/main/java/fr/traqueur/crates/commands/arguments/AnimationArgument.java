package fr.traqueur.crates.commands.arguments;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabCompleter;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.registries.AnimationsRegistry;
import fr.traqueur.crates.api.registries.Registry;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AnimationArgument implements ArgumentConverter<Animation>, TabCompleter<CommandSender> {
    @Override
    public Animation apply(String s) {
        return Registry.get(AnimationsRegistry.class).getById(s);
    }

    @Override
    public List<String> onCompletion(CommandSender sender, List<String> args) {
        return Registry.get(AnimationsRegistry.class).getAll().stream().map(Animation::id).toList();
    }
}
