package fr.traqueur.crates.api.models.animations;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record AnimationPhase(String name, long duration, long interval, SpeedCurve speedCurve,
                             Consumer<AnimationContext> onStart,
                             BiConsumer<AnimationContext, TickData> onTick,
                             Consumer<AnimationContext> onComplete) {

    public record TickData(int tickNumber, double progress, long elapsedTime) { }

    public enum SpeedCurve {
        LINEAR(progress -> progress),
        EASE_IN(progress -> progress * progress),
        EASE_OUT(progress -> 1 - Math.pow(1 - progress, 2)),
        EASE_IN_OUT(progress -> {
            if (progress < 0.5) {
                return 2 * progress * progress;
            } else {
                return 1 - Math.pow(-2 * progress + 2, 2) / 2;
            }
        });

        private final Function<Double, Double> curveFunction;

        SpeedCurve(Function<Double, Double> curveFunction) {
            this.curveFunction = curveFunction;
        }

        public double apply(double progress) {
            return curveFunction.apply(progress);
        }
    }
}