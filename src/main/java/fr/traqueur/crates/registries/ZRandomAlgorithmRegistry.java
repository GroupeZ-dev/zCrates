package fr.traqueur.crates.registries;

import fr.traqueur.crates.algorithms.AlgorithmsRegistrar;
import fr.traqueur.crates.algorithms.ZAlgorithmEngine;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.algorithms.RandomAlgorithm;
import fr.traqueur.crates.api.registries.RandomAlgorithmsRegistry;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ZRandomAlgorithmRegistry extends RandomAlgorithmsRegistry {

    private final ZAlgorithmEngine engine;

    public ZRandomAlgorithmRegistry(CratesPlugin plugin, ZAlgorithmEngine engine, String resourceFolder) {
        super(plugin, resourceFolder);
        this.engine = engine;
    }

    @Override
    protected RandomAlgorithm loadFile(Path file) {
        try {
            String scriptContent = Files.readString(file);
            Logger.debug("Loading algorithm from: {}", file.getFileName());

            // Create a capture context to intercept algorithm registrations
            AlgorithmsRegistrar registrar = new AlgorithmsRegistrar(file.toString(), engine);

            // Create secure scope
            Scriptable scope = engine.createSecureScope();

            // Add the algorithm registration API
            ScriptableObject.putProperty(scope, "algorithms", Context.javaToJS(registrar, scope));

            // Execute the script
            engine.evaluateFile(scriptContent, file.getFileName().toString(), scope);

            // Retrieve captured algorithms (should be exactly one per file)
            List<RandomAlgorithm> captured = registrar.algorithms();
            if (captured.isEmpty()) {
                Logger.warning("No algorithm registered in file: {}", file.getFileName());
                return null;
            }

            if (captured.size() > 1) {
                Logger.warning("Multiple algorithms registered in file: {}, using first one", file.getFileName());
            }

            RandomAlgorithm algorithm = captured.getFirst();
            this.storage.put(algorithm.id(), algorithm);
            return algorithm;
        } catch (Exception e) {
            Logger.severe("Failed to parse algorithm file {}", e, file.getFileName());
            return null;
        }
    }
}
