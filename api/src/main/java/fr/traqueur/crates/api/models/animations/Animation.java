package fr.traqueur.crates.api.models.animations;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents an animation that plays when a crate is opened.
 *
 * <p>Animations are defined in JavaScript files in {@code plugins/zCrates/animations/}
 * and consist of multiple phases with timing and visual effects.</p>
 *
 * <p><b>Example JavaScript animation:</b></p>
 * <pre>{@code
 * animations.register("roulette", {
 *     phases: [
 *         {
 *             name: "spin",
 *             duration: 3000,
 *             interval: 2,
 *             speedCurve: "EASE_OUT",
 *             onStart: function(context) { },
 *             onTick: function(context, tickData) { },
 *             onEnd: function(context) { }
 *         }
 *     ],
 *     onComplete: function(context) { },
 *     onCancel: function(context) { }
 * });
 * }</pre>
 *
 * @see AnimationPhase
 * @see AnimationContext
 */
public interface Animation {

    /**
     * Gets the unique identifier for this animation.
     *
     * <p>Referenced by crate configurations in the {@code animation} field.</p>
     *
     * @return the animation ID (e.g., "roulette", "csgo")
     */
    String id();

    /**
     * Gets the source JavaScript file path.
     *
     * @return the relative path to the animation script
     */
    String sourceFile();

    /**
     * Gets all phases of this animation.
     *
     * <p>Phases execute sequentially, each with its own duration and callbacks.</p>
     *
     * @return the list of animation phases
     * @see AnimationPhase
     */
    List<AnimationPhase> phases();

    /**
     * Gets a phase by its index.
     *
     * @param index the phase index (0-based)
     * @return the animation phase
     * @throws IndexOutOfBoundsException if index is invalid
     */
    default AnimationPhase phase(int index) {
        if(index < 0 || index >= phases().size()) {
            throw new IndexOutOfBoundsException("Invalid phase index: " + index);
        }
        return phases().get(index);
    }

    /**
     * Gets a phase by its name.
     *
     * @param name the phase name
     * @return the animation phase
     * @throws IllegalArgumentException if no phase with that name exists
     */
    default AnimationPhase phase(String name) {
        return phases().stream()
                .filter(phase -> phase.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No phase found with id: " + name));
    }

    /**
     * Gets the total duration of all phases combined.
     *
     * @return the total duration in milliseconds
     */
    default long duration() {
        return phases().stream().mapToLong(AnimationPhase::duration).sum();
    }

    /**
     * Gets the callback executed when the animation completes successfully.
     *
     * <p>Called after all phases finish and the reward is ready to be claimed.</p>
     *
     * @return the completion callback
     */
    Consumer<AnimationContext> onComplete();

    /**
     * Gets the callback executed when the animation is cancelled.
     *
     * <p>Called when the player closes the menu or the animation is interrupted.</p>
     *
     * @return the cancellation callback
     */
    Consumer<AnimationContext> onCancel();

}
