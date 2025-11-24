package fr.traqueur.crates.api.models.animations;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a phase in an animation sequence.
 *
 * @param name       The name of the animation phase.
 * @param duration   The total duration of the phase in milliseconds.
 * @param interval   The interval between ticks in milliseconds.
 * @param speedCurve The speed curve to apply during the phase.
 * @param onStart    A consumer that is called when the phase starts.
 * @param onTick     A bi-consumer that is called on each tick with the animation context and tick data.
 * @param onComplete A consumer that is called when the phase completes.
 */
public record AnimationPhase(String name, long duration, long interval, SpeedCurve speedCurve,
                             Consumer<AnimationContext> onStart,
                             BiConsumer<AnimationContext, TickData> onTick,
                             Consumer<AnimationContext> onComplete) {

    /**
     * Data provided on each tick of the animation phase.
     *
     * @param tickNumber  The current tick number.
     * @param progress    The progress of the phase as a value between 0.0 and 1.0.
     * @param elapsedTime The elapsed time since the start of the phase in milliseconds.
     */
    public record TickData(int tickNumber, double progress, long elapsedTime) { }

    /**
     * Represents different speed curves for animation phases.
     */
    public enum SpeedCurve {
        /** A linear speed curve. */
        LINEAR(progress -> progress),
        /** An ease-in speed curve. */
        EASE_IN(progress -> progress * progress),
        /** An ease-out speed curve. */
        EASE_OUT(progress -> 1 - Math.pow(1 - progress, 2)),
        /** An ease-in-out speed curve. */
        EASE_IN_OUT(progress -> {
            if (progress < 0.5) {
                return 2 * progress * progress;
            } else {
                return 1 - Math.pow(-2 * progress + 2, 2) / 2;
            }
        });

        /** The function defining the speed curve. */
        private final Function<Double, Double> curveFunction;

        /**
         * Constructs a SpeedCurve with the given curve function.
         *
         * @param curveFunction A function that defines the speed curve.
         */
        SpeedCurve(Function<Double, Double> curveFunction) {
            this.curveFunction = curveFunction;
        }

        /**
         * Applies the speed curve to the given progress value.
         *
         * @param progress The progress value between 0.0 and 1.0.
         * @return The adjusted progress value according to the speed curve.
         */
        public double apply(double progress) {
            return curveFunction.apply(progress);
        }
    }
}