package fr.traqueur.crates.hooks.modelednpcs;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.Registry;

@AutoHook("ModeledNPCs")
public class ModeledNPCsHook implements Hook {

    @Override
    public void onEnable() {
        CrateDisplayFactoriesRegistry displayRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        displayRegistry.registerGeneric(DisplayType.MODELED_NPC, new ModeledNPCCrateDisplayFactory());
        Logger.info("ModeledNPCs hook enabled. MODELED_NPC display type is now available.");
    }
}
