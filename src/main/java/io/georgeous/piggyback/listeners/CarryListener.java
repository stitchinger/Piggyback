package io.georgeous.piggyback.listeners;

import io.georgeous.piggyback.Piggyback;
import io.georgeous.piggyback.events.PlayerStartCarryEvent;
import io.georgeous.piggyback.events.PlayerStopCarryEvent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.function.Predicate;


public class CarryListener implements Listener {
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Entity target = null;
        if (Bukkit.getServer().selectEntities(player, "@e[name=!" + player.getName() + ", sort=nearest,limit=1,tag=!surrogate,distance=..2]").size() > 0) {
            target = Bukkit.getServer().selectEntities(player, "@e[name=!" + player.getName() + ", sort=nearest,limit=1,tag=!surrogate,distance=..2]").get(0);
        }

        if (!Piggyback.isProperItem(player)
                || player.isSneaking()) {
            return;
        }

        if (Piggyback.carryPairs.get(player) == null) {
            if(target == null){
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
    public void entityDamage(EntityDamageEvent event) {
        Entity hitEntity = event.getEntity();
        boolean isCarryHelper = hitEntity.getScoreboardTags().contains("carryhelper");

        if(isCarryHelper){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        Entity hitEntity = event.getEntity();
        boolean isCarryHelper = hitEntity.getScoreboardTags().contains("carryhelper");

        if(isCarryHelper){

            ray(event.getDamager().getWorld(),event.getDamager());
            event.getDamager().sendMessage("You cant do that while holding your baby");
            event.setCancelled(true);
        }
    }



    public void ray(World world, Entity entity){

        Location start = entity.getLocation().add(0,entity.getHeight(),0);

        Vector direction = entity.getLocation().getDirection();
        double maxDistance = 10;
        FluidCollisionMode fluidCollisionMode = FluidCollisionMode.ALWAYS;
        boolean ignorePassableBlock = false;
        double raySize = 1;
        Predicate predicate = null;

        RayTraceResult result = world.rayTrace(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlock, raySize, null);
        if(result == null){
            return;
        }

        entity.sendMessage(result.toString());
    }

    @EventHandler
    public void disableDismount(EntityDismountEvent event) {
        if (Piggyback.carryPairs.containsValue(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void vehicleExit(VehicleExitEvent event) {
        if(event.getVehicle() instanceof Player){
            event.getVehicle().sendMessage("Vehicle Exit");
        }
        if(event.getVehicle() instanceof Player || event.getVehicle().getScoreboardTags().contains("carryhelper")){
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandEquip(PlayerArmorStandManipulateEvent event) {
        ArmorStand armorStand = event.getRightClicked();
        if(armorStand.getScoreboardTags().contains("carryhelper"))
            event.setCancelled(true);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        event.getPlayer().setInvulnerable(false);
    }
}
