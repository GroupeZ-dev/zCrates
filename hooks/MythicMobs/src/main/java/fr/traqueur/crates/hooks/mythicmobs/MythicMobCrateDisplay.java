package fr.traqueur.crates.hooks.mythicmobs;

import fr.traqueur.crates.models.placedcrates.EntityCrateDisplay;
import fr.traqueur.crates.api.serialization.Keys;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class MythicMobCrateDisplay extends EntityCrateDisplay {

    private ActiveMob activeMob;

    public MythicMobCrateDisplay(Location location, String value, float yaw) {
        super(location, value, yaw);
    }

    @Override
    public void spawn() {
        // Create centered location while preserving yaw and pitch from original location
        Location originalLocation = getLocation();
        Location centeredLocation = originalLocation.clone().add(0.5, 0, 0.5);
        centeredLocation.setYaw(originalLocation.getYaw());
        centeredLocation.setPitch(originalLocation.getPitch());

        // Remove any existing entity at this location (from previous server session)
        // This ensures ModelEngine properly applies the model to a fresh spawn
        Entity existingEntity = findExistingMythicEntity(centeredLocation);
        if (existingEntity != null) {
            // Check if it's an ActiveMob and remove it properly
            Optional<ActiveMob> existingActiveMob = MythicBukkit.inst().getMobManager().getActiveMob(existingEntity.getUniqueId());
            if (existingActiveMob.isPresent()) {
                existingActiveMob.get().remove();
            } else {
                existingEntity.remove();
            }
        }

        // Always spawn a fresh MythicMob to ensure ModelEngine applies the model correctly
        Optional<MythicMob> mythicMobOpt = MythicBukkit.inst().getMobManager().getMythicMob(this.entityType);
        if (mythicMobOpt.isEmpty()) {
            throw new IllegalArgumentException("MythicMob type not found: " + this.entityType);
        }

        // Spawn with the centered location that includes the correct yaw
        this.activeMob = mythicMobOpt.get().spawn(BukkitAdapter.adapt(centeredLocation), 1);
        this.entity = activeMob.getEntity().getBukkitEntity();

        // Apply rotation immediately
        this.entity.setRotation(centeredLocation.getYaw(), 0);
        this.entity.setInvulnerable(true);
        this.entity.setPersistent(true);
        this.entity.setGravity(false);

        Keys.PLACED_CRATE_ENTITY.set(this.entity.getPersistentDataContainer(), true);
    }

    private Entity findExistingMythicEntity(Location centeredLocation) {
        // Search for entities with the PDC marker in the vicinity
        return centeredLocation.getWorld().getNearbyEntities(centeredLocation, 1, 1, 1).stream()
                .filter(e -> {
                    // Check if entity has our marker
                    if (!Keys.PLACED_CRATE_ENTITY.get(e.getPersistentDataContainer(), false)) {
                        return false;
                    }
                    // Check if this is a MythicMob entity
                    return MythicBukkit.inst().getMobManager().isMythicMob(e);
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public void remove() {
        if (activeMob != null) {
            activeMob.remove();
        } else {
            super.remove();
        }
    }
}