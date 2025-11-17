package fr.traqueur.crates.models.placedcrates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.serialization.Keys;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EntityCrateDisplay implements CrateDisplay<Entity> {

    private final Location location;
    protected final String entityType;
    private final float yaw;
    protected Entity entity;

    public EntityCrateDisplay(Location location, String value, float yaw) {
        this.location = location;
        this.entityType = value.toUpperCase();
        this.yaw = yaw;
    }

    @Override
    public void spawn() {
        // First, check if an entity already exists at this location (from previous server session)
        Location centeredLocation = location.clone().add(0.5, 0, 0.5);
        Entity existingEntity = findExistingEntity(centeredLocation);

        if (existingEntity != null) {
            this.entity = existingEntity;
            // Ensure properties are still correct
            this.entity.setRotation(yaw, 0);
            this.entity.setInvulnerable(true);
            this.entity.setPersistent(true);
            this.entity.setGravity(false);

            if (entity instanceof LivingEntity living) {
                living.setAI(false);
                living.setSilent(true);
                living.setCollidable(false);
            }

            if (entity instanceof ArmorStand stand) {
                stand.setCanMove(false);
                stand.setCanTick(false);
            }
            return;
        }

        // Spawn new entity centered on the block
        EntityType type = EntityType.valueOf(entityType);
        this.entity = location.getWorld().spawnEntity(centeredLocation, type);
        this.entity.setRotation(yaw, 0);
        this.entity.setInvulnerable(true);
        this.entity.setPersistent(true);
        this.entity.setGravity(false);

        if (entity instanceof LivingEntity living) {
            living.setAI(false);
            living.setSilent(true);
            living.setCollidable(false);
        }

        if (entity instanceof ArmorStand stand) {
            stand.setCanMove(false);
            stand.setCanTick(false);
        }

        Keys.PLACED_CRATE_ENTITY.set(entity.getPersistentDataContainer(), true);
    }

    private Entity findExistingEntity(Location centeredLocation) {
        // Search for entities with the PDC marker in the vicinity
        return centeredLocation.getWorld().getNearbyEntities(centeredLocation, 1, 1, 1).stream()
                .filter(e -> {
                    // Check if entity has our marker
                    if (!Keys.PLACED_CRATE_ENTITY.get(e.getPersistentDataContainer(), false)) {
                        return false;
                    }
                    // Check if entity type matches
                    try {
                        EntityType expectedType = EntityType.valueOf(entityType);
                        return e.getType() == expectedType;
                    } catch (IllegalArgumentException ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public void remove() {
        if (entity != null && !entity.isDead()) {
            entity.remove();
        }
    }

    @Override
    public boolean matches(Entity entity) {
        return this.entity != null && entity.getUniqueId().equals(this.entity.getUniqueId());
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Entity getEntity() {
        return entity;
    }
}