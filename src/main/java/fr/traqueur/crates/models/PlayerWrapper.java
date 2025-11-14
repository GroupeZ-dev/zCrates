package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.placeholders.PlaceholderParser;
import fr.traqueur.crates.api.services.MessagesService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Wrapper for Player that exposes only safe methods for animations.
 * This prevents animations from accessing dangerous player methods.
 */
public class PlayerWrapper extends Wrapper<Player> {

    public PlayerWrapper(Player handle) {
        super(handle);
    }

    /**
     * Sends a message to the player.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        MessagesService.sendMessage(handle, message);
    }

    /**
     * Sends a title to the player.
     *
     * @param title    the main title
     * @param subtitle the subtitle
     * @param fadeIn   fade in duration in ticks
     * @param stay     stay duration in ticks
     * @param fadeOut  fade out duration in ticks
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        MessagesService.sendTitle(handle, title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Plays a sound for the player at their location.
     *
     * @param sound  the sound name (e.g., "ENTITY_PLAYER_LEVELUP")
     * @param volume the volume (0.0 to 1.0)
     * @param pitch  the pitch (0.5 to 2.0)
     */
    public void playSound(String sound, float volume, float pitch) {
        try {
            Sound bukkitSound = Sound.valueOf(sound);
            handle.playSound(handle.getLocation(), bukkitSound, volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound name, ignore silently
        }
    }
}