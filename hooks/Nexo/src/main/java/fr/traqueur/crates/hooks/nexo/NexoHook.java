package fr.traqueur.crates.hooks.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.ItemsProvidersRegistry;
import fr.traqueur.crates.api.registries.Registry;
import fr.traqueur.crates.hooks.nexo.crates.NexoCrateDisplayFactory;

@AutoHook("Nexo")
public class NexoHook implements Hook {
    @Override
    public void onEnable() {
        CrateDisplayFactoriesRegistry registry = Registry.get(CrateDisplayFactoriesRegistry.class);
        ItemsProvidersRegistry itemsProvidersRegistry = Registry.get(ItemsProvidersRegistry.class);
        registry.register(DisplayType.NEXO, new NexoCrateDisplayFactory());
        itemsProvidersRegistry.register("Nexo", (player, itemId) -> {
            ItemBuilder builder = NexoItems.itemFromId(itemId);
            if(builder != null) {
                return builder.build();
            }
            return null;
        });
    }
}