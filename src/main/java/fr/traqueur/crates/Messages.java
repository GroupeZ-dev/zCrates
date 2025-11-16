package fr.traqueur.crates;

import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

public enum Messages implements Loadable {

    NO_PERMISSION("<red>You do not have permission to execute this command."),
    ONLY_IN_GAME("<red>This command can only be executed in-game."),
    ARG_NOT_RECOGNIZED("<red>Argument not recognized."),
    REQUIREMENT_NOT_MET("<red>You do not meet the requirements to perform this command."),

    INVALID_AMOUNT("<red>Amount must be greater than 0."),
    KEYS_GIVEN("<green>Gave x<amount> key(s) for the crate <crate> to <player>."),
    ;
    private final String rawMessage;

    Messages(String message) {
        this.rawMessage = message;
    }

    /**
     * Sends this message to a command sender with placeholders replaced.
     * <p>
     * Example usage:
     * <pre>{@code
     * Messages.EFFECT_APPLIED.send(
     *     player,
     *     Placeholder.parsed("effect", "super_hammer")
     * )
     * }</pre>
     *
     * @param sender       the command sender
     * @param placeholders the placeholders to replace
     */
    public void send(CommandSender sender, TagResolver... placeholders) {
        MessagesService.sendMessage(sender, this.rawMessage, placeholders);
    }

    public String get() {
        return this.rawMessage;
    }
}
