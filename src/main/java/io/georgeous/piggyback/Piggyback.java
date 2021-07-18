package io.georgeous.piggyback;


import io.georgeous.piggyback.commands.CarryCommand;
import io.georgeous.piggyback.listeners.CarryListener;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;


public final class Piggyback extends JavaPlugin {

    private static final boolean NEED_ITEM = true;
    private static final String ITEM_NAME = "Baby-Handler";
    public static boolean passengerMode = false;

    public static Map<Player, CarryCouple> carryPairs = new HashMap<>();


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CarryListener(), this);
        getServer().getPluginCommand("carry").setExecutor(new CarryCommand());

        for(Player player : Bukkit.getOnlinePlayers()){
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

    public void carryUpdate(){
        for (Map.Entry<Player, CarryCouple> entry : carryPairs.entrySet()) {
            entry.getValue().update();
        }
    }


    @Override
    public void onDisable() {
        for (Map.Entry<Player, CarryCouple> entry : carryPairs.entrySet()) {
            stopCarry(entry.getKey());
        }
    }

    public static boolean isProperItem(Player player) {
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

    public static void startCarry(Player player, Entity target) {

        target.setInvulnerable(true);
        CarryCouple carryCouple = new CarryCouple(target, player);
        carryCouple.start();

        carryPairs.put(player, carryCouple);
        startCarryEffects(target.getLocation());
    }


    public static void stopCarry(Player player) {
        CarryCouple carryCouple = carryPairs.get(player);
        Entity target = carryCouple.getTarget();
        target.setInvulnerable(false);

        carryCouple.stop();
        carryPairs.remove(player);
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
