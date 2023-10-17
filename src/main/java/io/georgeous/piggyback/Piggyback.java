package io.georgeous.piggyback;

import io.georgeous.piggyback.listeners.CarryListener;
import io.georgeous.piggyback.listeners.WolfTeleportListener;
import io.georgeous.piggyback.util.DualMap;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Objects;


public final class Piggyback extends JavaPlugin {

    private static final boolean NEED_ITEM = true;
    private static final String ITEM_NAME = "Baby-Handler";
    public static DualMap carryCoupleMap = new DualMap();

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
        CarryCouple carryCouple = new CarryCouple(target, player);
        carryCouple.start();

        carryCoupleMap.put(player, target, carryCouple);
        startCarryEffects(target.getLocation().clone().add(0, target.getBoundingBox().getHeight() / 2, 0));
    }

    public static void stopCarry(org.bukkit.entity.Player player) {
        CarryCouple carryCouple = carryCoupleMap.getCCFromCarrierPlayer(player);

        carryCouple.stop();
        carryCoupleMap.remove(player);
        stopCarryEffects(carryCouple.getTarget().getLocation().clone().add(0, carryCouple.getTarget().getBoundingBox().getHeight() / 2, 0));
    }

    public static void startCarryEffects(Location pos) {
        World world = pos.getWorld();
        Objects.requireNonNull(world).playSound(pos, Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
        world.spawnParticle(Particle.CLOUD, pos, 50, 0.5, 0.5, 0.5, 0.0);
    }

    public static void stopCarryEffects(Location pos) {
        World world = pos.getWorld();
        Objects.requireNonNull(pos.getWorld()).playSound(pos, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
        Objects.requireNonNull(world).spawnParticle(Particle.CLOUD, pos, 50, 0.5, 0.5, 0.5, 0.0);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CarryListener(), this);
        getServer().getPluginManager().registerEvents(new WolfTeleportListener(), this);

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

}