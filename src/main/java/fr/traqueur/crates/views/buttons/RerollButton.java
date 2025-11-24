package fr.traqueur.crates.views.buttons;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.crates.Messages;
import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.managers.CratesManager;
import fr.traqueur.crates.api.models.crates.Reward;
import fr.traqueur.crates.api.services.ItemsService;
import fr.traqueur.crates.api.services.MessagesService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * zMenu button that allows players to reroll their reward after animation completes.
 * Only visible/clickable when rerolls are available and animation is done.
 */
public class RerollButton extends Button {

    private final CratesPlugin plugin;

    public RerollButton(Plugin plugin) {
        this.plugin = (CratesPlugin) plugin;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        Logger.debug("[RerollButton] onClick called for player {} at slot {}", player.getName(), slot);

        CratesManager cratesManager = this.plugin.getManager(CratesManager.class);

        boolean animationCompleted = cratesManager.isAnimationCompleted(player);
        boolean canReroll = cratesManager.canReroll(player);
        int rerollsRemaining = cratesManager.getRerollsRemaining(player);

        Logger.debug("[RerollButton] animationCompleted={}, canReroll={}, rerollsRemaining={}",
                animationCompleted, canReroll, rerollsRemaining);

        if (!animationCompleted) {
            Logger.debug("[RerollButton] Animation not completed, ignoring click");
            return;
        }

        if (!canReroll) {
            Logger.debug("[RerollButton] Cannot reroll, sending NO_REROLLS_LEFT message");
            Messages.NO_REROLLS_LEFT.send(player);
            return;
        }

        Logger.debug("[RerollButton] Attempting reroll...");
        if (cratesManager.reroll(player)) {
            Logger.debug("[RerollButton] Reroll successful!");
            // Get the new reward and notify player
            Optional<Reward> newReward = cratesManager.getCurrentReward(player);
            int remaining = cratesManager.getRerollsRemaining(player);

            newReward.ifPresent(reward -> {
                Logger.debug("[RerollButton] New reward: {}", reward.id());
                Messages.REROLL_SUCCESS.send(player,
                        Placeholder.parsed("reward", reward.displayItem().displayName()),
                        Placeholder.parsed("remaining", String.valueOf(remaining))
                );
            });
        } else {
            Logger.debug("[RerollButton] Reroll failed!");
        }
    }

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        CratesManager cratesManager = this.plugin.getManager(CratesManager.class);
        int remaining = cratesManager.getRerollsRemaining(player);
        ItemStack item = this.getItemStack().build(player).clone();
        ItemsService.addLoreLine(item, MessagesService.parseMessage(
                Messages.REROLLS_REMAINING_LORE.get(),
                Placeholder.parsed("remaining", String.valueOf(remaining))
        ));

        for (Integer slot : this.slots) {
            inventoryEngine.addItem(slot, item);
        }
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }
    
}