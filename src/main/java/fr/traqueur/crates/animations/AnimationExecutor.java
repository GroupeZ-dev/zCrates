package fr.traqueur.crates.animations;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.models.animations.AnimationContext;
import fr.traqueur.crates.api.models.animations.AnimationPhase;
import fr.traqueur.crates.api.models.animations.AnimationPhase.TickData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Executes animations by running their phases and callbacks.
 * Manages the lifecycle of animation instances and their timing.
 */
public class AnimationExecutor {

    private final Plugin plugin;
    private final Map<UUID, RunningAnimation> runningAnimations;

    public AnimationExecutor(Plugin plugin) {
        this.plugin = plugin;
        this.runningAnimations = new HashMap<>();
    }

    /**
     * Starts an animation for a specific context.
     *
     * @param animation the animation to run
     * @param context   the context object passed to callbacks
     * @return the unique ID of this animation instance
     */
    public UUID startAnimation(Animation animation, AnimationContext context) {
        UUID instanceId = UUID.randomUUID();
        
        Logger.debug("Starting animation: {} (instance: {})", animation.id(), instanceId);
        
        RunningAnimation running = new RunningAnimation(animation, context, instanceId);
        runningAnimations.put(instanceId, running);
        
        // Start the first phase
        running.startNextPhase();
        
        return instanceId;
    }

    /**
     * Cancels a running animation.
     *
     * @param instanceId the animation instance ID
     * @return true if the animation was cancelled, false if not found
     */
    public boolean cancelAnimation(UUID instanceId) {
        RunningAnimation running = runningAnimations.remove(instanceId);
        if (running != null) {
            running.cancel();
            return true;
        }
        return false;
    }

    /**
     * Checks if an animation is currently running.
     *
     * @param instanceId the animation instance ID
     * @return true if running, false otherwise
     */
    public boolean isRunning(UUID instanceId) {
        return runningAnimations.containsKey(instanceId);
    }

    /**
     * Cancels all running animations.
     */
    public void cancelAll() {
        Logger.debug("Cancelling {} running animation(s)", runningAnimations.size());
        runningAnimations.values().forEach(RunningAnimation::cancel);
        runningAnimations.clear();
    }

    /**
     * Gets the number of currently running animations.
     *
     * @return the count of running animations
     */
    public int getRunningCount() {
        return runningAnimations.size();
    }

    /**
     * Represents a running instance of an animation.
     */
    private class RunningAnimation {
        private final Animation animation;
        private final AnimationContext context;
        private final UUID instanceId;
        private int currentPhaseIndex;
        private int currentTick;
        private long phaseStartTime;
        private BukkitTask currentTask;

        public RunningAnimation(Animation animation, AnimationContext context, UUID instanceId) {
            this.animation = animation;
            this.context = context;
            this.instanceId = instanceId;
            this.currentPhaseIndex = 0;
            this.currentTick = 0;
        }

        /**
         * Starts the next phase in the animation sequence.
         */
        public void startNextPhase() {
            if (currentPhaseIndex >= animation.phases().size()) {
                // Animation complete
                complete();
                return;
            }

            AnimationPhase phase = animation.phase(currentPhaseIndex);
            Logger.debug("Starting phase: {} ({}/{})", 
                       phase.name(), currentPhaseIndex + 1, animation.phases().size());

            currentTick = 0;
            phaseStartTime = System.currentTimeMillis();

            // Execute onStart callback if present
            Consumer<AnimationContext> onStart = phase.onStart();
            if (onStart != null) {
                try {
                    onStart.accept(context);
                } catch (Exception e) {
                    Logger.warning("Error in phase onStart: {}", e.getMessage());
                }
            }

            // Calculate tick interval in server ticks (1 tick = 50ms)
            long intervalTicks = phase.interval() / 50;
            if (intervalTicks < 1) intervalTicks = 1;

            // Schedule the phase execution
            currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                long elapsed = System.currentTimeMillis() - phaseStartTime;
                
                // Check if phase duration exceeded
                if (elapsed >= phase.duration()) {
                    // Phase complete
                    Consumer<AnimationContext> onComplete = phase.onComplete();
                    if (onComplete != null) {
                        try {
                            onComplete.accept(context);
                        } catch (Exception e) {
                            Logger.warning("Error in phase onComplete: {}", e.getMessage());
                        }
                    }
                    
                    // Cancel current task and move to next phase
                    if (currentTask != null) {
                        currentTask.cancel();
                    }
                    currentPhaseIndex++;
                    startNextPhase();
                    return;
                }

                // Execute onTick callback
                BiConsumer<AnimationContext, TickData> onTick = phase.onTick();
                if (onTick != null) {
                    // Calculate progress (0.0 to 1.0)
                    double linearProgress = (double) elapsed / phase.duration();
                    
                    // Apply speed curve
                    double curvedProgress = phase.speedCurve().apply(linearProgress);
                    
                    // Create tick data
                    TickData tickData = new TickData(currentTick, curvedProgress, elapsed);
                    
                    // Execute callback
                    try {
                        onTick.accept(context, tickData);
                    } catch (Exception e) {
                        Logger.warning("Error in phase onTick: {}", e.getMessage());
                    }
                }

                currentTick++;
            }, 0L, intervalTicks);
        }

        /**
         * Completes the animation successfully.
         */
        private void complete() {
            Logger.debug("Animation completed: {} (instance: {})", animation.id(), instanceId);
            
            if (currentTask != null) {
                currentTask.cancel();
            }

            // Execute onComplete callback
            Consumer<AnimationContext> onComplete = animation.onComplete();
            if (onComplete != null) {
                try {
                    onComplete.accept(context);
                } catch (Exception e) {
                    Logger.warning("Error in animation onComplete: {}", e.getMessage());
                }
            }

            runningAnimations.remove(instanceId);
        }

        /**
         * Cancels the animation.
         */
        public void cancel() {
            Logger.debug("Animation cancelled: {} (instance: {})", animation.id(), instanceId);
            
            if (currentTask != null) {
                currentTask.cancel();
            }

            // Execute onCancel callback
            Consumer<AnimationContext> onCancel = animation.onCancel();
            if (onCancel != null) {
                try {
                    onCancel.accept(context);
                } catch (Exception e) {
                    Logger.warning("Error in animation onCancel: {}", e.getMessage());
                }
            }
        }
    }
}