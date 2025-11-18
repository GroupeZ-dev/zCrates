package fr.traqueur.crates.api.serialization;

import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class PlacedCrateDataType implements PersistentDataType<PersistentDataContainer, PlacedCrate> {

    public static PlacedCrateDataType INSTANCE;

    protected PlacedCrateDataType() {
    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<PlacedCrate> getComplexType() {
        return PlacedCrate.class;
    }
}