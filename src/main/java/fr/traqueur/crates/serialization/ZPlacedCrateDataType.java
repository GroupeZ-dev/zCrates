package fr.traqueur.crates.serialization;

import fr.traqueur.crates.api.models.placedcrates.DisplayType;
import fr.traqueur.crates.api.models.placedcrates.PlacedCrate;
import fr.traqueur.crates.api.serialization.Keys;
import fr.traqueur.crates.api.serialization.PlacedCrateDataType;
import fr.traqueur.crates.hooks.mythicmobs.MythicMobCrateDisplayFactory;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ZPlacedCrateDataType extends PlacedCrateDataType {

    public static void initialize() {
        PlacedCrateDataType.INSTANCE = new ZPlacedCrateDataType();
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull PlacedCrate crate, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        Keys.INTERNAL_PLACED_CRATE_ID.set(container, crate.id().toString());
        Keys.INTERNAL_PLACED_CRATE_CRATE_ID.set(container, crate.crateId());
        Keys.INTERNAL_PLACED_CRATE_WORLD_NAME.set(container, crate.worldName());
        Keys.INTERNAL_PLACED_CRATE_X.set(container, crate.x());
        Keys.INTERNAL_PLACED_CRATE_Y.set(container, crate.y());
        Keys.INTERNAL_PLACED_CRATE_Z.set(container, crate.z());
        Keys.INTERNAL_PLACED_CRATE_DISPLAY_TYPE.set(container, crate.displayType().name());
        Keys.INTERNAL_PLACED_CRATE_DISPLAY_VALUE.set(container, crate.displayValue());
        Keys.INTERNAL_PLACED_CRATE_YAW.set(container, crate.yaw());

        return container;
    }

    @Override
    public @NotNull PlacedCrate fromPrimitive(@NotNull PersistentDataContainer container, @NotNull PersistentDataAdapterContext context) {
        UUID id = UUID.fromString(Keys.INTERNAL_PLACED_CRATE_ID.get(container).orElseThrow());
        String crateId = Keys.INTERNAL_PLACED_CRATE_CRATE_ID.get(container).orElseThrow();
        String worldName = Keys.INTERNAL_PLACED_CRATE_WORLD_NAME.get(container).orElseThrow();
        int x = Keys.INTERNAL_PLACED_CRATE_X.get(container).orElseThrow();
        int y = Keys.INTERNAL_PLACED_CRATE_Y.get(container).orElseThrow();
        int z = Keys.INTERNAL_PLACED_CRATE_Z.get(container).orElseThrow();
        DisplayType displayType = DisplayType.valueOf(Keys.INTERNAL_PLACED_CRATE_DISPLAY_TYPE.get(container).orElseThrow());
        String displayValue = Keys.INTERNAL_PLACED_CRATE_DISPLAY_VALUE.get(container).orElseThrow();
        float yaw = Keys.INTERNAL_PLACED_CRATE_YAW.get(container).orElseThrow();

        return new PlacedCrate(id, crateId, worldName, x, y, z, displayType, displayValue, yaw);
    }
}