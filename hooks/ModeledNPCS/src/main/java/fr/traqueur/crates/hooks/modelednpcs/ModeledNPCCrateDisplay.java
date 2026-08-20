package fr.traqueur.crates.hooks.modelednpcs;

import fr.elias.npcs.api.ModeledNPCsAPI;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.serialization.Keys;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ModeledNPCCrateDisplay implements CrateDisplay<Entity> {

    private final Location location;
    private final String modelName;
    private final float yaw;
    private int npcId = -1;

    public ModeledNPCCrateDisplay(Location location, String modelName, float yaw) {
        this.location = location;
        this.modelName = modelName;
        this.yaw = yaw;
    }

    @Override
    public void spawn() {
        Location centeredLocation = location.clone().add(0.5, 0, 0.5);
        centeredLocation.setYaw(yaw);

        // Try to find an existing NPC entity at this location (from a previous server session)
        Entity existingEntity = findExistingEntity(centeredLocation);
        if (existingEntity != null) {
            for (Integer id : ModeledNPCsAPI.get().getAllNPCIds()) {
                Entity npcEntity = ModeledNPCsAPI.get().getEntityByNPCId(id);
                if (npcEntity != null && npcEntity.getUniqueId().equals(existingEntity.getUniqueId())) {
                    this.npcId = id;
                    return;
                }
            }
        }

        // Spawn a fresh ModeledNPC
        this.npcId = ModeledNPCsAPI.get().createNPC("modelengine", modelName, centeredLocation);
        if (this.npcId == -1) {
            throw new IllegalArgumentException("Failed to create ModeledNPC with model: " + modelName);
        }

        ModeledNPCsAPI.get().hideNPCNametag(npcId);

        Entity entity = ModeledNPCsAPI.get().getEntityByNPCId(npcId);
        if (entity != null) {
            entity.setInvulnerable(true);
            entity.setPersistent(true);
            entity.setGravity(false);
            entity.setRotation(yaw, 0);
            Keys.PLACED_CRATE_ENTITY.set(entity.getPersistentDataContainer(), true);
        }
    }

    private Entity findExistingEntity(Location centeredLocation) {
        return centeredLocation.getWorld().getNearbyEntities(centeredLocation, 1, 1, 1).stream()
                .filter(e -> Keys.PLACED_CRATE_ENTITY.get(e.getPersistentDataContainer(), false))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void remove() {
        if (npcId != -1) {
            ModeledNPCsAPI.get().deleteNPC(npcId);
            npcId = -1;
        }
    }

    @Override
    public boolean matches(Entity entity) {
        if (npcId == -1) return false;
        Entity npcEntity = ModeledNPCsAPI.get().getEntityByNPCId(npcId);
        return npcEntity != null && entity.getUniqueId().equals(npcEntity.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
