package fr.traqueur.crates.api.models.animations;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface Animation {

    String id();

    String sourceFile();

    List<AnimationPhase> phases();

    default AnimationPhase phase(int index) {
        if(index < 0 || index >= phases().size()) {
            throw new IndexOutOfBoundsException("Invalid phase index: " + index);
        }
        return phases().get(index);
    }

    default AnimationPhase phase(String name) {
        return phases().stream()
                .filter(phase -> phase.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No phase found with id: " + name));
    }

    default long duration() {
        return phases().stream().mapToLong(AnimationPhase::duration).sum();
    }

    Consumer<AnimationContext> onComplete();

    Consumer<AnimationContext> onCancel();

    void play(Player player);

}
