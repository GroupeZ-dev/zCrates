package fr.traqueur.crates.models.wrappers;

import fr.traqueur.crates.api.models.Wrapper;
import fr.traqueur.crates.api.services.MessagesService;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Wrapper for Player that exposes only safe methods for animations.
 * This prevents animations from accessing dangerous player methods.
 */
public class PlayerWrapper extends Wrapper<Player> {

    public PlayerWrapper(Player delegate) {
        super(delegate);
    }

    /**
     * Sends a message to the player.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        MessagesService.sendMessage(delegate, message);
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
        MessagesService.sendTitle(delegate, title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Plays a sound for the player at their location.
     *
     * @param sound  the sound name (e.g., "ENTITY_PLAYER_LEVELUP")
     * @param volume the volume (0.0 to 1.0)
     * @param pitch  the pitch (0.5 to 2.0)
     */
    public void playSound(String sound, float volume, float pitch) {
        Sound bukkitSound = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).get(NamespacedKey.minecraft(sound));
        if(bukkitSound == null) return;
        delegate.playSound(delegate.getLocation(), bukkitSound, volume, pitch);
    }
}