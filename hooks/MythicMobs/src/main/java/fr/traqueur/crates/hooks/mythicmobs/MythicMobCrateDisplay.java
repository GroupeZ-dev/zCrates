package fr.traqueur.crates.hooks.mythicmobs;

import fr.traqueur.crates.api.CratesPlugin;
import fr.traqueur.crates.models.placedcrates.EntityCrateDisplay;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

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

        this.activeMob = mythicMobOpt.get().spawn(BukkitAdapter.adapt(getSpawnLocation()), 1);
        this.entity = activeMob.getEntity().getBukkitEntity();
        this.configure(this.entity);

        // ModelEngine attaches the model asynchronously and may reset the orientation, re-apply it next tick
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(CratesPlugin.class), () -> {
            if (this.entity != null && this.entity.isValid()) {
                this.applyRotation(this.entity);
            }
        });
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
