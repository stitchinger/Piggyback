package io.georgeous.piggyback.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class WolfTeleportListener implements Listener {

    @EventHandler
    public void onWolfTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof Wolf wolf)) {
            return;
        }

        if (!wolf.getScoreboardTags().contains("carryhelper")) {
            return;
        }

        wolfTeleportEffect(event.getFrom());
        wolfTeleportEffect(event.getTo());
    }

    private void wolfTeleportEffect(@Nullable Location location) {
        if (location == null) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.playSound(location, Sound.ENTITY_SHULKER_TELEPORT, 1, 1);
        world.spawnParticle(Particle.PORTAL, location, 100, 1, 1, 1, 0.0);
    }
}