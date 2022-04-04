package io.georgeous.piggyback;

import io.georgeous.piggyback.commands.CarryCommand;
import io.georgeous.piggyback.listeners.CarryListener;
import io.georgeous.piggyback.util.DualMap;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;


public final class Piggyback extends JavaPlugin {

    public static DualMap carryCoupleMap = new DualMap();
    private static final boolean NEED_ITEM = false;
    private static final String ITEM_NAME = "Baby-Handler";


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CarryListener(), this);
        Objects.requireNonNull(getServer().getPluginCommand("carry")).setExecutor(new CarryCommand());

        // Set all players to vulnerable just in case
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            player.setInvulnerable(false);
        }

        // Start Update function
        new BukkitRunnable() {
            @Override
            public void run() {
                carryUpdate();

            }
        }.runTaskTimer(this, 0L, 1L);

    }

    public void carryUpdate() {
        for (Map.Entry<org.bukkit.entity.Player, CarryCouple> entry : carryCoupleMap.carriers.entrySet()) {
            entry.getValue().update();
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<org.bukkit.entity.Player, CarryCouple> entry : carryCoupleMap.carriers.entrySet()) {
            stopCarry(entry.getKey());
        }
    }

    public static boolean isProperItem(org.bukkit.entity.Player player) {
        // Check for correct item in Hand
        if (!NEED_ITEM) {
            return true;
        }
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        boolean properItem = false;
        if (meta != null) {
            if (meta.hasDisplayName()) {
                properItem = meta.getDisplayName().contains(ITEM_NAME);
            }
        }
        return properItem;
    }

    public static void startCarry(org.bukkit.entity.Player player, Entity target) {
        //target.setInvulnerable(true);
        CarryCouple carryCouple = new CarryCouple(target, player);
        carryCouple.start();

        carryCoupleMap.put(player, target, carryCouple);
        startCarryEffects(target.getLocation());
    }


    public static void stopCarry(org.bukkit.entity.Player player) {
        CarryCouple carryCouple = carryCoupleMap.get(player);
        //carryCouple.getTarget().setInvulnerable(false);

        carryCouple.stop();
        carryCoupleMap.remove(player);
        stopCarryEffects(player.getLocation());
    }

    public static void startCarryEffects(Location pos) {
        World world = pos.getWorld();
        Objects.requireNonNull(world).playSound(pos, Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
        //particle minecraft:cloud ^1 ^2 ^ 0.1 0.1 0.1 0.05 10 normal @p
        //particle minecraft:poof ^ ^2 ^ 0.2 0.2 0.2 0.03 100 normal @p
        //world.spawnParticle(Particle.BLOCK, pos, 20, 0.5, 0.5, 0.5);
        //world.spawnParticle(Particle.CLOUD, pos, 100, 0.2,0.2,0.2);

        //world.spawnParticle(Particle.CLOUD, pos, 100, 0.1,0.1,0.1);
        //Vector vel = new Vector(loc2.getX() - loc.getX(), loc2.getY() - loc.getY(), loc2.getZ() - loc.getZ());
        //world.spawnParticle(Particle.FLAME, loc, 0, vel.getX(), vel.getY(), vel.getZ(), 0.05);

    }

    public static void stopCarryEffects(Location pos) {
        Objects.requireNonNull(pos.getWorld()).playSound(pos, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
    }

}