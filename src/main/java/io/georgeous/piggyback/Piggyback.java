package io.georgeous.piggyback;

import io.georgeous.piggyback.commands.CarryCommand;
import io.georgeous.piggyback.listeners.CarryListener;
import io.georgeous.piggyback.util.DualMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;


public final class Piggyback extends JavaPlugin {

    public static DualMap carryCoupleMap = new DualMap();
    private static final boolean NEED_ITEM = false;
    private static final String ITEM_NAME = "Baby-Handler";


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CarryListener(this), this);
        getServer().getPluginCommand("carry").setExecutor(new CarryCommand());

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
        pos.getWorld().playSound(pos, Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
        //world.spawnParticle(Particle.BLOCK_DUST, pos, 5, 0.5, 0.5, 0.5);
    }

    public static void stopCarryEffects(Location pos) {
        pos.getWorld().playSound(pos, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
    }

}