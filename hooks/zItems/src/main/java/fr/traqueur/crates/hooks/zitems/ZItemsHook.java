package fr.traqueur.crates.hooks.zitems;

import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.registries.ItemsRegistry;

@AutoHook("zItems")
public class ZItemsHook implements Hook {
    @Override
    public void onEnable() {
        ItemsProvidersRegistry itemsProvidersRegistry = Registry.get(ItemsProvidersRegistry.class);
        itemsProvidersRegistry.register("zItems", (player, itemId) -> {
            ItemsRegistry registry = fr.traqueur.items.api.registries.Registry.get(ItemsRegistry.class);
            Item item = registry.getById(itemId);
            return item.build(player, 1);
        });
    }
}
