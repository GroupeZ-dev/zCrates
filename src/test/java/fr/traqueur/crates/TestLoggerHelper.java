package fr.traqueur.crates;

import fr.traqueur.crates.api.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper pour initialiser le Logger dans les tests
 */
public class TestLoggerHelper {

    /**
     * Initialise le Logger avec un logger SLF4J pour les tests
     */
    public static void initLogger() {
        Logger.init(LoggerFactory.getLogger("TestLogger"), true);
    }
}
