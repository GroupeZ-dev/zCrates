package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.placeholders.PlaceholderParser;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.structura.annotations.validation.Max;
import fr.traqueur.structura.annotations.validation.Min;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public record ZCrate(String id, @Min(9) @Max(54) int size, Animation animation, String title) implements Crate, Loadable {

    public ZCrate {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Crate size must be a multiple of 9");
        }
    }

    public Inventory inventory(Player player) {
        return Bukkit.createInventory(this, this.size, MessagesService.parseMessage(PlaceholderParser.parsePlaceholders(player, this.title)));
    }

    @Override
    public @NotNull Inventory getInventory() {
        throw new UnsupportedOperationException("Use inventory(Player) method to get a crate inventory for a specific player.");
    }
}
