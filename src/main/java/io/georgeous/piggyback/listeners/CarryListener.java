package io.georgeous.piggyback.listeners;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.Piggyback;
import io.georgeous.piggyback.events.PlayerStartCarryEvent;
import io.georgeous.piggyback.events.PlayerStopCarryEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.List;
import java.util.stream.Collectors;

public class CarryListener implements Listener {

    public static List<Entity> filterEntities(List<Entity> entities) {
        return entities.stream()
                .filter(entity -> entity instanceof LivingEntity) // Exclude non-living entities
                .filter(entity -> !entity.getScoreboardTags().contains("surrogate")) // Exclude entities with "surrogate" tag
                .collect(Collectors.toList()); // Collect the remaining entities
    }

    public static List<Entity> sortEntitiesByDistance(List<Entity> entities, Location location) {
        return entities.stream()
                .sorted((entity1, entity2) -> {
                    double distance1 = entity1.getLocation().distance(location);
                    double distance2 = entity2.getLocation().distance(location);
                    return Double.compare(distance1, distance2);
                })
                .toList(); // Collect the sorted entities into a new list
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        Entity target = null;
        List<Entity> nearbyEntities = player.getNearbyEntities(1,1,1);
        List<Entity> filtered = filterEntities(nearbyEntities);
        List<Entity> entities = sortEntitiesByDistance(filtered,player.getLocation());

        if (!entities.isEmpty()) {
            target = entities.get(0);
        }

        if (!Piggyback.isProperItem(player) || player.isSneaking()) {
            return;
        }

        CarryCouple carryCouple = Piggyback.carryCoupleMap.getCCFromCarrierPlayer(player);
        if (carryCouple == null) {
            if (target == null) {
                return;
            }
            PlayerStartCarryEvent e = new PlayerStartCarryEvent(player, target);
            Bukkit.getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                Wolf.Variant variant = e.getVariant();
                Piggyback.startCarry(player, target, variant);
            }

        } else {
            double distance = player.getLocation().distance(carryCouple.getTarget().getLocation());
            if(distance > 2)
                return;

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
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CarryCouple carrierCC = Piggyback.carryCoupleMap.getCCFromCarrierPlayer(player);
        if (carrierCC != null) {
            Piggyback.stopCarry(carrierCC.getCarrier());
            return;
        }
        CarryCouple carriedCC = Piggyback.carryCoupleMap.getCCFromCarriedEntity(player);
        if (carriedCC != null) {
            Piggyback.stopCarry(carriedCC.getCarrier());
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        CarryCouple carriedCC = Piggyback.carryCoupleMap.getCCFromCarriedEntity(entity);
        if (carriedCC != null) {
            Piggyback.stopCarry(carriedCC.getCarrier());
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