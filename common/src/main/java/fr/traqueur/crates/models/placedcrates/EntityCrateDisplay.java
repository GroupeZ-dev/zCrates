package fr.traqueur.crates.models.placedcrates;

import fr.traqueur.crates.api.models.placedcrates.CrateDisplay;
import fr.traqueur.crates.api.serialization.Keys;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class EntityCrateDisplay implements CrateDisplay<Entity> {

    private final Location location;
    protected final String entityType;
    protected final float yaw;
    protected Entity entity;

    public EntityCrateDisplay(Location location, String value, float yaw) {
        this.location = location;
        this.entityType = value;
        this.yaw = yaw;
    }

    @Override
    public void spawn() {
        EntityType type = EntityType.valueOf(entityType);
        Class<? extends Entity> entityClass = type.getEntityClass();
        if (entityClass == null) {
            throw new IllegalArgumentException("Entity type is not spawnable: " + entityType);
        }
        // Rotation is applied before the entity is added to the world so the first packet is already oriented
        this.entity = location.getWorld().spawn(getSpawnLocation(), entityClass, this::configure);
    }

    /**
     * Centered location on the block, carrying the stored yaw.
     */
    protected Location getSpawnLocation() {
        Location spawnLocation = location.clone().add(0.5, 0, 0.5);
        spawnLocation.setYaw(yaw);
        spawnLocation.setPitch(0);
        return spawnLocation;
    }

    /**
     * Applies the stored orientation (yaw, head and body) to the entity.
     */
    protected void applyRotation(Entity entity) {
        entity.setRotation(yaw, 0);
        if (entity instanceof LivingEntity living) {
            living.setBodyYaw(yaw);
        }
    }

    protected void configure(Entity entity) {
        applyRotation(entity);
        entity.setInvulnerable(true);
        // Not persisted: the display is respawned from the chunk PDC on every chunk load
        entity.setPersistent(false);
        entity.setGravity(false);

        if (entity instanceof LivingEntity living) {
            living.setAI(false);
            living.setSilent(true);
            living.setCollidable(false);
            living.setRemoveWhenFarAway(false);
        }

        if (entity instanceof ArmorStand stand) {
            stand.setCanMove(false);
            stand.setCanTick(false);
        }

        Keys.PLACED_CRATE_ENTITY.set(entity.getPersistentDataContainer(), true);
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
