package fr.traqueur.crates.views.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Reward;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class RerollButton extends Button {

    private final CratesPlugin plugin;

    public RerollButton(Plugin plugin) {
        this.plugin = (CratesPlugin) plugin;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        CratesManager cratesManager = this.plugin.getManager(CratesManager.class);

        boolean animationCompleted = cratesManager.isAnimationCompleted(player);
        boolean canReroll = cratesManager.canReroll(player);

        if (!animationCompleted) {
            return;
        }

        if (!canReroll) {
            Messages.NO_REROLLS_LEFT.send(player);
            return;
        }

        if (cratesManager.reroll(player)) {
            int remaining = cratesManager.getRerollsRemaining(player);
            Messages.REROLL_SUCCESS.send(player, Placeholder.parsed("remaining", String.valueOf(remaining)));
            this.onRender(player, inventory);
        } else {
           Logger.warning("Reroll failed for player: " + player.getName());
        }
    }

    @Override
    public ItemStack getCustomItemStack(Player player) {
        CratesManager cratesManager = this.plugin.getManager(CratesManager.class);
        int remaining = cratesManager.getRerollsRemaining(player);
        Placeholders placeholders = new Placeholders();
        placeholders.register("remaining", String.valueOf(remaining));
        return getItemStack().build(player, false, placeholders);
    }
}