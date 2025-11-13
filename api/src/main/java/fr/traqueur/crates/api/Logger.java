package fr.traqueur.crates.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Logger utility class for logging messages with different levels and formatting.
 */
public class Logger {

    /** ANSI color codes mapping */
    private static final Map<String, String> ANSI_COLORS = new HashMap<>();
    /** SLF4J Logger instance */
    private static org.slf4j.Logger LOGGER;
    /** Debug mode flag */
    private static boolean DEBUG = false;

    static {
        ANSI_COLORS.put("<black>", "\u001B[0;30m");
        ANSI_COLORS.put("<dark_blue>", "\u001B[0;34m");
        ANSI_COLORS.put("<dark_green>", "\u001B[0;32m");
        ANSI_COLORS.put("<dark_aqua>", "\u001B[0;36m");
        ANSI_COLORS.put("<dark_red>", "\u001B[0;31m");
        ANSI_COLORS.put("<dark_purple>", "\u001B[0;35m");
        ANSI_COLORS.put("<gold>", "\u001B[0;33m");
        ANSI_COLORS.put("<gray>", "\u001B[0;37m");
        ANSI_COLORS.put("<dark_gray>", "\u001B[0;30;1m");
        ANSI_COLORS.put("<blue>", "\u001B[0;34;1m");
        ANSI_COLORS.put("<green>", "\u001B[0;32;1m");
        ANSI_COLORS.put("<aqua>", "\u001B[0;36;1m");
        ANSI_COLORS.put("<red>", "\u001B[0;31;1m");
        ANSI_COLORS.put("<light_purple>", "\u001B[0;35;1m");
        ANSI_COLORS.put("<yellow>", "\u001B[0;33;1m");
        ANSI_COLORS.put("<white>", "\u001B[0;37;1m");

        // Formatting
        ANSI_COLORS.put("<bold>", "\u001B[1m");
        ANSI_COLORS.put("<italic>", "\u001B[3m");
        ANSI_COLORS.put("<underlined>", "\u001B[4m");
        ANSI_COLORS.put("<strikethrough>", "\u001B[9m");
        ANSI_COLORS.put("<reset>", "\u001B[0m");
    }

    /** Private constructor to prevent instantiation */
    private Logger() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes the Logger with the given SLF4J logger and debug mode.
     *
     * @param logger the SLF4J logger instance
     * @param debug  whether to enable debug mode
     */
    public static void init(org.slf4j.Logger logger, boolean debug) {
        LOGGER = logger;
        DEBUG = debug;
    }

    /** Logs a debug message if debug mode is enabled.
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    public static void debug(String message, Object... args) {
        if (DEBUG) {
            info(message, args);
        }
    }

    /** Logs an informational message.
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    public static void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    /** Logs a success message in green color.
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    public static void success(String message, Object... args) {
        log(Level.INFO, "<green>" + message + "<reset>", args);
    }

    /** Logs a warning message in yellow color.
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    public static void warning(String message, Object... args) {
        log(Level.WARN, "<yellow>" + message + "<reset>", args);
    }

    /** Logs a severe error message in red color.
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    public static void severe(String message, Object... args) {
        log(Level.ERROR, "<red>" + message + "<reset>", args);
    }

    /** Logs a severe error message with an exception in red color.
     * @param message the message to log
     * @param exception the exception to log
     * @param args    the arguments for the message
     **/
    public static void severe(String message, Exception exception, Object... args) {
        log("<red>" + message + "<reset>", exception, args);
    }

    /** Logs an error message in red color.
     * @param level  the logging level
     * @param message the message to log
     * @param args    the arguments for the message
     **/
    private static void log(Level level, String message, Object... args) {
        ensureInitialized();
        String formatted = convertMiniMessageToAnsi(message);
        switch (level) {
            case INFO -> LOGGER.info(formatted, args);
            case WARN -> LOGGER.warn(formatted, args);
            case ERROR -> LOGGER.error(formatted, args);
        }
    }

    /** Logs an error message with an exception in red color.
     * @param message the message to log
     * @param exception the exception to log
     * @param args    the arguments for the message
     **/
    private static void log(String message, Exception exception, Object... args) {
        ensureInitialized();
        String formatted = convertMiniMessageToAnsi(message);
        LOGGER.error(formatted, args, exception);
    }

    /** Ensures that the logger has been initialized.
     * @throws IllegalStateException if not initialized
     **/
    private static void ensureInitialized() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger is not initialized. Call Logger.init() first.");
        }
    }

    /** Converts MiniMessage formatted strings to ANSI color codes.
     * @param message the MiniMessage formatted string
     * @return the ANSI formatted string
     * */
    private static String convertMiniMessageToAnsi(String message) {
        // Then convert MiniMessage tags to ANSI RGB codes
        for (Map.Entry<String, String> entry : ANSI_COLORS.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        message += ANSI_COLORS.get("<reset>");
        return message;
    }

    /** Sets the debug mode.
     * @param debug whether to enable debug mode
     **/
    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    /** Logging levels enumeration. */
    private enum Level {
        /** Informational level */
        INFO,
        /** Warning level */
        WARN,
        /** Error level */
        ERROR
    }
}