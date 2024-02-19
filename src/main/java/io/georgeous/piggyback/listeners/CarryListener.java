package io.georgeous.piggyback.listeners;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.Piggyback;
import io.georgeous.piggyback.events.PlayerStartCarryEvent;
import io.georgeous.piggyback.events.PlayerStopCarryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;

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

        if (Piggyback.carryCoupleMap.getCCFromCarrierPlayer(player) == null) {
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
        if (!Piggyback.carryCoupleMap.isCarried(event.getEntity()))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        if (!player.isSneaking())
            return;

        event.setCancelled(true);
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
        CarryCouple carrierCC = Piggyback.carryCoupleMap.getCCFromCarrierPlayer(event.getPlayer());
        if (carrierCC != null) {
            Piggyback.stopCarry(carrierCC.getCarrier());
            return;
        }
        CarryCouple carriedCC = Piggyback.carryCoupleMap.getCCFromCarriedEntity(event.getPlayer());
        if (carriedCC != null) {
            Piggyback.stopCarry(carriedCC.getCarrier());
            return;
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        CarryCouple carrierCC = Piggyback.carryCoupleMap.getCCFromCarrierPlayer(event.getEntity());
        if (carrierCC != null) {
            Piggyback.stopCarry(carrierCC.getCarrier());
            return;
        }
        CarryCouple carriedCC = Piggyback.carryCoupleMap.getCCFromCarriedEntity(event.getEntity());
        if (carriedCC != null) {
            Piggyback.stopCarry(carriedCC.getCarrier());
            return;
        }
    }

    @EventHandler
    public void onCarryHelperDeath(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (!(e.getScoreboardTags().contains("carryhelper"))) {
            return;
        }

        if (event.getDamage() < ((LivingEntity) e).getHealth()) {
            return;
        }

        if (!(e instanceof Tameable tameable))
            return;

        tameable.setOwner(null);
    }

    @EventHandler
    public void onSvenRightClick(PlayerInteractEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();

        if (!(clickedEntity.getScoreboardTags().contains("carryhelper"))) {
            return;
        }

        if (!(clickedEntity instanceof Tameable))
            return;

        event.setCancelled(true);
    }


}