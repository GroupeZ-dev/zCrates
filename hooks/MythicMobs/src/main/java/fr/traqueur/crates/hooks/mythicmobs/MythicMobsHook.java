package fr.traqueur.crates.hooks.mythicmobs;

import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.annotations.AutoHook;
import fr.traqueur.crates.api.hooks.Hook;
import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.registries.CrateDisplayFactoriesRegistry;
import fr.traqueur.crates.api.registries.Registry;

@AutoHook("MythicMobs")
public class MythicMobsHook implements Hook {

    @Override
    public void onEnable() {
        CrateDisplayFactoriesRegistry displayRegistry = Registry.get(CrateDisplayFactoriesRegistry.class);
        displayRegistry.registerGeneric(DisplayType.MYTHIC_MOB, new MythicMobCrateDisplayFactory());
        Logger.info("MythicMobs hook enabled. MYTHIC_MOB display type is now available.");
    }
}