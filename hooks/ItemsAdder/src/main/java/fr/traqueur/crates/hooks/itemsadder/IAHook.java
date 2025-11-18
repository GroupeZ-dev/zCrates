package fr.traqueur.crates.hooks.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.hooks.itemsadder.crates.IACrateDisplayFactory;

@AutoHook("ItemsAdder")
public class IAHook implements Hook {
    @Override
    public void onEnable() {
        CrateDisplayFactoriesRegistry registry = Registry.get(CrateDisplayFactoriesRegistry.class);
        ItemsProvidersRegistry itemsProvidersRegistry = Registry.get(ItemsProvidersRegistry.class);
        registry.register(DisplayType.ITEMS_ADDER, new IACrateDisplayFactory());
        itemsProvidersRegistry.register("ItemsAdder", (player, itemId) -> {
            CustomStack customStack = CustomStack.getInstance(itemId);
            if(customStack != null) {
                return customStack.getItemStack();
            } else {
                return null;
            }
        });
    }
}
