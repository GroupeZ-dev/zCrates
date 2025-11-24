package fr.traqueur.crates.hooks.oraxen;

import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.hooks.oraxen.crates.OraxenCrateDisplayFactory;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;

@AutoHook("Oraxen")
public class OraxenHook implements Hook {
    @Override
    public void onEnable() {
        CrateDisplayFactoriesRegistry registry = Registry.get(CrateDisplayFactoriesRegistry.class);
        ItemsProvidersRegistry itemsProvidersRegistry = Registry.get(ItemsProvidersRegistry.class);
        registry.register(DisplayType.ORAXEN, new OraxenCrateDisplayFactory());
        itemsProvidersRegistry.register("Oraxen", (player, itemId) -> {
            ItemBuilder builder = OraxenItems.getItemById(itemId);
            if (builder == null) {
                return null;
            }
            return builder.build();
        });
    }
}