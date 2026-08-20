package fr.traqueur.crates.hooks.modelednpcs;

import fr.elias.npcs.api.ModeledNPCsAPI;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplayFactory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class ModeledNPCCrateDisplayFactory implements CrateDisplayFactory<Entity> {

    @Override
    public CrateDisplay<Entity> create(Location location, String value, float yaw) {
        return new ModeledNPCCrateDisplay(location, value, yaw);
    }

    @Override
    public boolean isValidValue(String value) {
        return value != null && !value.isBlank();
    }

    @Override
    public List<String> getSuggestions() {
        return ModeledNPCsAPI.get().getAllNPCs().values().stream()
                .map(npc -> npc.getModelId())
                .filter(modelId -> modelId != null && !modelId.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
