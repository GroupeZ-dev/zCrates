package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.api.models.animations.AnimationPhase;

import java.util.List;
import java.util.function.Consumer;

public record ZAnimation(String id, String sourceFile, List<AnimationPhase> phases, Consumer<AnimationContext> onComplete, Consumer<AnimationContext> onCancel) implements Animation {
}
