package fr.traqueur.crates.hooks.modelednpcs;

import fr.elias.npcs.api.ModeledNPCsAPI;
import fr.traqueur.crates.api.Logger;
import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ModeledNPCCrateDisplay implements CrateDisplay<Entity> {

    private final Location location;
    private final int npcId;
    private final float yaw;

    public ModeledNPCCrateDisplay(Location location, String value, float yaw) {
        this.location = location;
        this.npcId = Integer.parseInt(value);
        this.yaw = yaw;
    }

    @Override
    public void spawn() {
        if (ModeledNPCsAPI.get().getNPCById(npcId) == null) {
            Logger.warning("ModeledNPC with ID " + npcId + " not found.");
            return;
        }

        Location centeredLocation = location.clone().add(0.5, 0, 0.5);
        centeredLocation.setYaw(yaw);

        ModeledNPCsAPI.get().moveNPC(npcId, centeredLocation);
        ModeledNPCsAPI.get().hideNPCNametag(npcId);
    }

    @Override
    public void remove() {
        ModeledNPCsAPI.get().deleteNPC(npcId);
    }

    @Override
    public boolean matches(Entity entity) {
        Entity npcEntity = ModeledNPCsAPI.get().getEntityByNPCId(npcId);
        return npcEntity != null && entity.getUniqueId().equals(npcEntity.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
