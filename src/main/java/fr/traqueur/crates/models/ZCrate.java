package fr.traqueur.crates.models;

import fr.traqueur.crates.api.models.Crate;
import fr.traqueur.crates.api.models.Reward;
import fr.traqueur.crates.api.models.animations.Animation;
import fr.traqueur.crates.api.providers.PlaceholderProvider;
import fr.traqueur.crates.api.services.MessagesService;
import fr.traqueur.crates.api.settings.models.ItemStackWrapper;
import fr.traqueur.structura.annotations.validation.Max;
import fr.traqueur.structura.annotations.validation.Min;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ZCrate(String id, @Min(9) @Max(54) int size, Animation animation, String title, List<Reward> rewards) implements Crate, Loadable {

    public ZCrate {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Crate size must be a multiple of 9");
        }
    }

    public Inventory inventory(Player player) {
        return Bukkit.createInventory(this, this.size, MessagesService.parseMessage(PlaceholderProvider.parsePlaceholders(player, this.title)));
    }

    @Override
    public ItemStackWrapper randomDisplay() {
        double totalWeight = rewards.stream().mapToDouble(Reward::weight).sum();
        double randomValue = Math.random() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Reward reward : rewards) {
            cumulativeWeight += reward.weight();
            if (randomValue <= cumulativeWeight) {
                return reward.displayItem();
            }
        }

        return rewards.getLast().displayItem();
    }

    @Override
    public @NotNull Inventory getInventory() {
        throw new UnsupportedOperationException("Use inventory(Player) method to get a crate inventory for a specific player.");
    }
}
