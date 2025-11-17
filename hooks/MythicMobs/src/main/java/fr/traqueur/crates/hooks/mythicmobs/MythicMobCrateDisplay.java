package fr.traqueur.crates.hooks.mythicmobs;

import fr.traqueur.crates.models.placedcrates.EntityCrateDisplay;
import fr.traqueur.crates.serialization.Keys;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;

import java.util.Optional;

public class MythicMobCrateDisplay extends EntityCrateDisplay {

    private ActiveMob activeMob;

    public MythicMobCrateDisplay(Location location, String value, float yaw) {
        super(location, value, yaw);
    }

    @Override
    public void spawn() {
        Optional<MythicMob> mythicMobOpt = MythicBukkit.inst().getMobManager().getMythicMob(this.entityType);
        if (mythicMobOpt.isEmpty()) {
            throw new IllegalArgumentException("MythicMob type not found: " + this.entityType);
        }

        this.activeMob = mythicMobOpt.get().spawn(BukkitAdapter.adapt(getLocation()), 1);
        this.entity = activeMob.getEntity().getBukkitEntity();

        this.entity.setRotation(getLocation().getYaw(), 0);
        this.entity.setInvulnerable(true);
        this.entity.setPersistent(true);
        this.entity.setGravity(false);

        Keys.PLACED_CRATE_ENTITY.set(this.entity.getPersistentDataContainer(), true);
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