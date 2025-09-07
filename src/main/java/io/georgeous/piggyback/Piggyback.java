package io.georgeous.piggyback;

import com.google.common.base.Preconditions;
import io.georgeous.piggyback.listeners.CarryListener;
import io.georgeous.piggyback.listeners.WolfTeleportListener;
import io.georgeous.piggyback.util.DualMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

@NullMarked
public final class Piggyback extends JavaPlugin {

    private static final boolean NEED_ITEM = true;
    public static DualMap carryCoupleMap = new DualMap();
    @Nullable
    private static Piggyback instance;
    
    public static final NamespacedKey ITEM_KEY = new NamespacedKey("piggyback", "baby_handler");

    private static Piggyback instance() {
        Preconditions.checkNotNull(instance, "Cannot access instance before plugin was initialized!");
        return instance;
    }

    public static void startCarry(org.bukkit.entity.Player player, Entity target, Wolf.Variant variant) {
        CarryCouple carryCouple = new CarryCouple(target, player);
        carryCouple.start(variant);

        carryCoupleMap.put(player, target, carryCouple);
        startCarryEffects(target.getLocation().clone().add(0, target.getBoundingBox().getHeight() / 2, 0));

        Bukkit.getScheduler().runTaskLater(instance(), () -> wolfSoundEffects(target.getLocation()), 10);
    }

    public static void stopCarry(org.bukkit.entity.Player player) {
        CarryCouple carryCouple = carryCoupleMap.getCCFromCarrierPlayer(player);
        if (carryCouple == null) {
            return;
        }

        carryCouple.stop();
        carryCoupleMap.remove(player);
        stopCarryEffects(carryCouple.getTarget().getLocation().clone().add(0, carryCouple.getTarget().getBoundingBox().getHeight() / 2, 0));
    }

    private static void startCarryEffects(Location pos) {
        World world = pos.getWorld();
        Objects.requireNonNull(world).playSound(pos, Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
        world.spawnParticle(Particle.CLOUD, pos, 50, 0.5, 0.5, 0.5, 0.0);
    }

    private static void wolfSoundEffects(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        if (Math.random() > 0.33) // Only play sound every 3rd time
        {
            return;
        }

        // Define sounds with their corresponding weights
        NavigableMap<Double, Sound> weightMap = new TreeMap<>();
        weightMap.put(32.0, Sound.ENTITY_WOLF_PANT);   // 32% chance
        weightMap.put(64.0, Sound.ENTITY_WOLF_SHAKE);  // 32% chance
        weightMap.put(96.0, Sound.ENTITY_WOLF_WHINE);  // 32% chance
        weightMap.put(100.0, Sound.ENTITY_WOLF_BIG_GROWL);  // 4% chance

        // Choose a random sound based on weight
        double totalWeight = weightMap.lastKey();
        double randomValue = Math.random() * totalWeight;
        Sound chosenSound = weightMap.ceilingEntry(randomValue).getValue();

        // Play the chosen sound
        world.playSound(location, chosenSound, SoundCategory.NEUTRAL, 1, 1);
    }

    private static void stopCarryEffects(Location pos) {
        World world = pos.getWorld();
        Objects.requireNonNull(pos.getWorld()).playSound(pos, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
        Objects.requireNonNull(world).spawnParticle(Particle.CLOUD, pos, 50, 0.5, 0.5, 0.5, 0.0);
    }

    public static boolean isProperItem(org.bukkit.entity.Player player) {
        if (!NEED_ITEM) {
            return true;
        }

        return player.getInventory().getItemInMainHand().getPersistentDataContainer().has(instance().ITEM_KEY);
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new CarryListener(), this);
        getServer().getPluginManager().registerEvents(new WolfTeleportListener(), this);

        // Set all players to vulnerable just in case
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            player.setInvulnerable(false);
        }

        // Start Update function
        Bukkit.getScheduler().runTaskTimer(this, this::carryUpdate, 0, 1);
    }

    @Override
    public void onDisable() {
        for (Map.Entry<org.bukkit.entity.Player, CarryCouple> entry : carryCoupleMap.carriers.entrySet()) {
            stopCarry(entry.getKey());
        }
    }

    private void carryUpdate() {
        for (Map.Entry<org.bukkit.entity.Player, CarryCouple> entry : carryCoupleMap.carriers.entrySet()) {
            entry.getValue().update();
        }
    }
}