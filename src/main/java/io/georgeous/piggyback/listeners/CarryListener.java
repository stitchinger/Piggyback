package io.georgeous.piggyback.listeners;

import io.georgeous.piggyback.Piggyback;
import io.georgeous.piggyback.events.PlayerStartCarryEvent;
import io.georgeous.piggyback.events.PlayerStopCarryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import java.util.List;

public class CarryListener implements Listener {

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        Entity target = null;
        List<Entity> entities = Bukkit.getServer().selectEntities(player, "@e[name=!" + player.getName() + ",type=!boat, sort=nearest,limit=1,tag=!surrogate,distance=..2]");
        if (entities.size() > 0) {
            target = entities.get(0);
        }

        if (!Piggyback.isProperItem(player)
                || player.isSneaking()) {
            return;
        }

        if (Piggyback.carryCoupleMap.get(player) == null) {
            if (target == null) {
                return;
            }
            PlayerStartCarryEvent e = new PlayerStartCarryEvent(player, target);
            Bukkit.getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                Piggyback.startCarry(player, target);
            }

        } else {
            PlayerStopCarryEvent e = new PlayerStopCarryEvent(player);
            Bukkit.getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                Piggyback.stopCarry(player);
            }
        }
    }

    @EventHandler
    public void disableSuffocationDamage(EntityDamageEvent event) {
        if (!(Piggyback.carryCoupleMap.carried.containsKey(event.getEntity())))
            return; // Damaged Entity is not carried
        if (!(event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)))
            return; // Damage not caused by suffocation

        event.setCancelled(true);
    }

    @EventHandler
    public void disableDismount(EntityDismountEvent event) {
        if (Piggyback.carryCoupleMap.isCarried(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void vehicleExit(VehicleExitEvent event) {
        if (event.getVehicle() instanceof org.bukkit.entity.Player) {
            event.getVehicle().sendMessage("Vehicle Exit");
        }
        if (event.getVehicle() instanceof org.bukkit.entity.Player || event.getVehicle().getScoreboardTags().contains("carryhelper")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandEquip(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        if (armorStand.getScoreboardTags().contains("carryhelper"))
            event.setCancelled(true);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.getPlayer().setInvulnerable(false);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if (Piggyback.carryCoupleMap.get(event.getPlayer()) == null) {
            return;
        }
        org.bukkit.entity.Player player = event.getPlayer();
        Piggyback.stopCarry(player);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if (Piggyback.carryCoupleMap.get(event.getEntity()) == null) {
            return;
        }
        org.bukkit.entity.Player player = event.getEntity();
        Piggyback.stopCarry(player);
    }
}