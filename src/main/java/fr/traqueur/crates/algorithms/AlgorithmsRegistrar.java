package fr.traqueur.crates.algorithms;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.algorithms.AlgorithmContext;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.models.algorithms.ZRandomAlgorithm;
import fr.traqueur.crates.models.wrappers.RewardsWrapper;
import org.mozilla.javascript.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Registrar for random algorithms loaded from JavaScript.
 * Similar to AnimationsRegistrar, this captures algorithm registrations
 * and converts JavaScript functions to Java functions.
 */
public class AlgorithmsRegistrar {

    private final String sourceFile;
    private final ZAlgorithmEngine engine;
    private final List<RandomAlgorithm> algorithms;

    public AlgorithmsRegistrar(String sourceFile, ZAlgorithmEngine engine) {
        this.sourceFile = sourceFile;
        this.engine = engine;
        this.algorithms = new ArrayList<>();
    }

    public List<RandomAlgorithm> algorithms() {
        return algorithms;
    }

    /**
     * Called from JavaScript: algorithms.register(id, selectorFunction)
     *
     * @param id Unique algorithm identifier
     * @param jsFunction JavaScript function that receives context and returns a reward
     */
    public void register(String id, org.mozilla.javascript.Function jsFunction) {
        try {
            // Wrap the JavaScript function into a Java Function<AlgorithmContext, Reward>
            Function<AlgorithmContext, Reward> selector = context -> {
                // Execute the JavaScript function with the context (contains wrappers)
                Object result = engine.executeFunction(jsFunction, context);

                // Unwrap JavaScript object to Java object
                if (result instanceof NativeJavaObject nativeJavaObject) {
                    Object unwrapped = nativeJavaObject.unwrap();
                    if (unwrapped instanceof Reward reward) {
                        return reward;
                    }
                }

                // Direct check if already unwrapped
                if (result instanceof Reward reward) {
                    return reward;
                }

                Logger.warning("Algorithm {} returned invalid result (type: {}), using fallback", id, result != null ? result.getClass().getName() : "null");
                return ((RewardsWrapper) context.rewards()).weightedRandom();
            };

            RandomAlgorithm algorithm = new ZRandomAlgorithm(id, sourceFile, selector);
            algorithms.add(algorithm);

            Logger.debug("Captured algorithm: {}", id);

        } catch (Exception e) {
            Logger.warning("Failed to register algorithm {}: {}", id, e.getMessage());
            Logger.debug("Registration error:", e);
        }
    }

    /**
     * Internal implementation of RandomAlgorithm
     */

}
