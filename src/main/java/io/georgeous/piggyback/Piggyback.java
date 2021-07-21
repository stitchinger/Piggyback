package io.georgeous.piggyback;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.georgeous.piggyback.commands.CarryCommand;
import io.georgeous.piggyback.listeners.CarryListener;
import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;


public final class Piggyback extends JavaPlugin {

    public static Map<Player, CarryCouple> carryCoupleMap = new HashMap<>();
    private static final boolean NEED_ITEM = true;
    private static final String ITEM_NAME = "Baby-Handler";



    @Override
    public void onEnable() {

        //EntityTypes.Builder entitytypes_builder = EntityTypes.Builder.a(EntityArmorStand::new, EnumCreatureType.g).a(0.1F, 0.1F).trackingRange(10);
        //custom = (EntityTypes<EntityArmorStand>) IRegistry.a(IRegistry.Y, "armor_stand", entitytypes_builder.a("armor_stand"));


        getServer().getPluginManager().registerEvents(new CarryListener(this), this);
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
        for (Map.Entry<Player, CarryCouple> entry : carryCoupleMap.entrySet()) {
            entry.getValue().update();
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Player, CarryCouple> entry : carryCoupleMap.entrySet()) {
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

        carryCoupleMap.put(player, carryCouple);
        startCarryEffects(target.getLocation());
    }


    public static void stopCarry(Player player) {
        CarryCouple carryCouple = carryCoupleMap.get(player);
        carryCouple.getTarget().setInvulnerable(false);

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
