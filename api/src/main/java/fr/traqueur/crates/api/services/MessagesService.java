package fr.traqueur.crates.api.services;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.PlatformType;
import fr.traqueur.crates.api.providers.PlaceholderProvider;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.Map;

/**
 * Utility class for sending messages using Adventure API.
 * Automatically detects Paper (native Adventure) vs Spigot (Adventure Platform wrapper).
 */
public class MessagesService {

    /**
     * Private constructor to prevent instantiation.
     */
    private MessagesService() {
        // Utility class
    }

    /**
     * Legacy Minecraft color codes mapping to MiniMessage tags.
     */
    private static final Map<String, String> LEGACY_TO_MINIMESSAGE = Map.ofEntries(
            Map.entry("&0", "<black>"),
            Map.entry("&1", "<dark_blue>"),
            Map.entry("&2", "<dark_green>"),
            Map.entry("&3", "<dark_aqua>"),
            Map.entry("&4", "<dark_red>"),
            Map.entry("&5", "<dark_purple>"),
            Map.entry("&6", "<gold>"),
            Map.entry("&7", "<gray>"),
            Map.entry("&8", "<dark_gray>"),
            Map.entry("&9", "<blue>"),
            Map.entry("&a", "<green>"),
            Map.entry("&b", "<aqua>"),
            Map.entry("&c", "<red>"),
            Map.entry("&d", "<light_purple>"),
            Map.entry("&e", "<yellow>"),
            Map.entry("&f", "<white>"),
            Map.entry("&l", "<bold>"),
            Map.entry("&m", "<strikethrough>"),
            Map.entry("&n", "<underlined>"),
            Map.entry("&o", "<italic>"),
            Map.entry("&r", "<reset>")
    );
    private static BukkitAudiences bukkitAudiences;

    /**
     * MiniMessage instance for parsing messages.
     */
    public static MiniMessage MINI_MESSAGE;

    /**
     * Initializes the MessagesService with the plugin instance.
     * Detects server type and sets up appropriate Adventure backend.
     *
     * @param plugin The plugin instance
     */
    public static void initialize(Plugin plugin) {
        PlatformType platformType = PlatformType.detect();
        MINI_MESSAGE = MiniMessage.miniMessage();

        if (platformType == PlatformType.SPIGOT) {
            bukkitAudiences = BukkitAudiences.create(plugin);
            Logger.info("<yellow>Detected Spigot server - Using Adventure Platform wrapper");
        } else {
            Logger.info("<yellow>Detected Paper server - Using native Adventure API");
        }
    }

    /**
     * Closes the BukkitAudiences instance (Spigot only).
     * Should be called on plugin disable.
     */
    public static void close() {
        if (bukkitAudiences != null) {
            bukkitAudiences.close();
            bukkitAudiences = null;
        }
    }

    /**
     * Sends a Component message to a command sender.
     * Handles Paper (native) vs Spigot (wrapper) automatically.
     *
     * @param sender    The command sender
     * @param message   The message to send
     * @param placeholders the placeholder resolvers
     */
    public static void sendMessage(CommandSender sender, String message, TagResolver... placeholders) {
        String parsedString = sender instanceof Player player ? PlaceholderProvider.parsePlaceholders(player, message) : message;
        Component parsedComponent = parseMessage(parsedString, placeholders);
        if (PlatformType.isPaper()) {
            sender.sendMessage(parsedComponent);
        } else {
            Audience audience = bukkitAudiences.sender(sender);
            audience.sendMessage(parsedComponent);
        }
    }

    /**
     * Sends a title to a player.
     * Handles Paper (native) vs Spigot (wrapper) automatically.
     **
     * @param player   The player to send the title to
     * @param title    The main title text
     * @param subtitle The subtitle text
     * @param fadeIn   Fade in duration in ticks
     * @param stay     Stay duration in ticks
     * @param fadeOut  Fade out duration in ticks
     * @param placeholders Optional placeholder resolvers
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, TagResolver... placeholders) {
        // Parse title and subtitle with PlaceholderAPI
        String parsedTitle = PlaceholderProvider.parsePlaceholders(player, title);
        String parsedSubtitle = PlaceholderProvider.parsePlaceholders(player, subtitle);

        // Parse MiniMessage and custom placeholders
        Component titleComponent = parseMessage(parsedTitle, placeholders);
        Component subtitleComponent = parseMessage(parsedSubtitle, placeholders);

        // Create title times
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );

        Title adventureTitle = Title.title(
                titleComponent,
                subtitleComponent,
                times
        );

        // Send using appropriate backend
        if (PlatformType.isPaper()) {
            player.showTitle(adventureTitle);
        } else {
            Audience audience = bukkitAudiences.player(player);
            audience.showTitle(adventureTitle);
        }
    }


    /**
     * Parses a message with MiniMessage tags, legacy color codes, and custom placeholders.
     * Converts legacy codes to MiniMessage format first, then parses with placeholder resolution.
     * <p>
     * Example usage:
     * <pre>{@code
     * import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
     *
     * Component message = MessagesService.parseMessage(
     *     "<green>Player <player> earned <amount> coins!",
     *     Placeholder.parsed("player", playerName),
     *     Placeholder.parsed("amount", String.valueOf(coins))
     * );
     * }</pre>
     *
     * @param message      The message to parse
     * @param placeholders The TagResolvers for placeholder replacement
     * @return The parsed Component with placeholders replaced
     */
    public static Component parseMessage(String message, TagResolver... placeholders) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        String converted = convertLegacyToMiniMessage(message);
        return MINI_MESSAGE.deserialize(converted, placeholders);
    }

    /**
     * Converts legacy Minecraft color codes (&<char>) to MiniMessage tags.
     *
     * @param message The message with legacy color codes
     * @return The message with MiniMessage tags
     */
    private static String convertLegacyToMiniMessage(String message) {
        if (!message.contains("&")) {
            return message;
        }

        StringBuilder builder = new StringBuilder(message.length() + 20);
        int length = message.length();

        for (int i = 0; i < length; i++) {
            if (message.charAt(i) == '&' && i + 1 < length) {
                String code = "&" + message.charAt(i + 1);
                String replacement = LEGACY_TO_MINIMESSAGE.get(code);
                if (replacement != null) {
                    builder.append(replacement);
                    i++; // Skip next char
                    continue;
                }
            }
            builder.append(message.charAt(i));
        }

        return builder.toString();
    }

}