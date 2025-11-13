package fr.traqueur.crates.api.placeholders;

import org.bukkit.entity.Player;

/**
 * Singleton interface for parsing placeholders in text strings.
 *
 * <p>This interface provides a unified way to handle placeholder parsing across
 * different systems (PlaceholderAPI, custom placeholders, etc.). The implementation
 * is set at runtime when PlaceholderAPI is detected.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * String parsed = PlaceholderParser.parse(player, "Hello %player_name%!");
 * // Result: "Hello John!" (if PlaceholderAPI is available)
 * }</pre>
 *
 * <h2>Implementations</h2>
 * <ul>
 *   <li><b>EmptyParser:</b> Returns text unchanged (default when no placeholder system is available)</li>
 *   <li><b>PAPIHook:</b> Integrates with PlaceholderAPI for full placeholder support</li>
 * </ul>
 */
public interface PlaceholderParser {

    /**
     * The singleton instance holder.
     */
    class Holder {

        /**
         * Private constructor to prevent instantiation.
         */
        private Holder() {}

        private static PlaceholderParser instance = new EmptyParser();

        /**
         * Sets the global placeholder parser instance.
         *
         * @param parser The parser implementation to use
         */
        public static void setInstance(PlaceholderParser parser) {
            instance = parser;
        }

        /**
         * Gets the current placeholder parser instance.
         *
         * @return The active parser (never null, defaults to EmptyParser)
         */
        public static PlaceholderParser getInstance() {
            return instance;
        }
    }

    /**
     * Parses placeholders in a single text string for a specific player.
     *
     * @param player The player context for placeholder resolution
     * @param text   The text containing placeholders
     * @return The text with placeholders replaced
     */
    String parse(Player player, String text);

    /**
     * Convenience method to parse using the singleton instance.
     *
     * @param player The player context
     * @param text   The text to parse
     * @return The parsed text
     */
    static String parsePlaceholders(Player player, String text) {
        return Holder.getInstance().parse(player, text);
    }


    /**
     * Empty placeholder parser that returns text unchanged.
     *
     * <p>This implementation is used as the default when no placeholder system
     * (like PlaceholderAPI) is available.</p>
     */
    class EmptyParser implements PlaceholderParser {

        /**
         * Constructs an EmptyParser instance.
         */
        private EmptyParser() {}

        @Override
        public String parse(Player player, String text) {
            return text;
        }

    }
}