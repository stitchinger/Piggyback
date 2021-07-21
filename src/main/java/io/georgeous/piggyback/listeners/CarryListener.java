package io.georgeous.piggyback.listeners;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.Piggyback;
import io.georgeous.piggyback.events.PlayerStartCarryEvent;
import io.georgeous.piggyback.events.PlayerStopCarryEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;

import org.bukkit.entity.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.function.Predicate;


public class CarryListener implements Listener {

    private final Piggyback plugin;

    public CarryListener(Piggyback plugin){
        this.plugin = plugin;
    }

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

        if (Piggyback.carryCoupleMap.get(player) == null) {
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
        // Todo if entity get carried and damagecause is suffocation stop damage
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        Entity hitEntity = event.getEntity();
        boolean isCarryHelper = hitEntity.getScoreboardTags().contains("carryhelper");

        if(isCarryHelper){
            event.getDamager().sendMessage("You cant do that while holding your baby");
            //event.setCancelled(true);
        }
    }

    @EventHandler
    public void rightClick(PlayerInteractAtEntityEvent event){
        Entity hitEntity = event.getRightClicked();
        Player player = event.getPlayer();
        boolean isCarryHelper = hitEntity.getScoreboardTags().contains("carryhelper");

        if(!isCarryHelper)
            return;
        RayTraceResult rr = ray(player);
        if(rr == null)
            return;

        if(rr.getHitBlock() != null){
            Block b = rr.getHitBlock();
            //player.sendMessage(b.toString());

            if(b.getBlockData() instanceof Openable){
                player.sendMessage(b.getBlockData().toString());
                Openable openable = (Openable) b.getBlockData();
                openable.setOpen(!openable.isOpen());
                b.getState().setBlockData(openable);
                b.getState().update();

            }else{
                World world = player.getWorld();
                Location loc = rr.getHitPosition().toLocation(world);
                Slime slime = (Slime) world.spawnEntity(loc, EntityType.SLIME);
                slime.setSilent(true);
                slime.setGravity(false);
                slime.setAI(false);
                slime.setSize(0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        slime.setHealth(0);
                    }
                }.runTaskLater(plugin,40);
            }

        } else if(rr.getHitEntity() != null){
            Entity e = rr.getHitEntity();
            player.sendMessage(e.toString());
        }

    }



    public RayTraceResult ray(Entity entity){
        World world = entity.getWorld();
        Location start = entity.getLocation().add(0,((LivingEntity)entity).getEyeHeight(),0);
        start = start.add(entity.getLocation().getDirection().normalize().multiply(2));
        Vector direction = entity.getLocation().getDirection();
        double maxDistance = 10;
        FluidCollisionMode fluidCollisionMode = FluidCollisionMode.ALWAYS;
        boolean ignorePassableBlock = false;
        double raySize = 0.01;

        RayTraceResult result = world.rayTrace(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlock, raySize, null);

        if(result == null){
            return null;
        }
        return result;
    }

    @EventHandler
    public void disableDismount(EntityDismountEvent event) {
        if (Piggyback.carryCoupleMap.containsValue(event.getEntity())) {
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

    @EventHandler
    public void playerQuit(PlayerQuitEvent event){
        if(Piggyback.carryCoupleMap.get(event.getPlayer()) == null){
            return;
        }
        Player player = event.getPlayer();
        Piggyback.stopCarry(player);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        if(Piggyback.carryCoupleMap.get(event.getEntity()) == null){
            return;
        }
        Player player = event.getEntity();
        Piggyback.stopCarry(player);
    }
}
