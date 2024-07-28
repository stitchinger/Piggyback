package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.Sven;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class HybridMode extends CarryMode {
    private final Player player;
    private final Entity target;
    private Wolf sven;


    public HybridMode(CarryCouple cc) {
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
    }

    @Override
    public void start(Wolf.Variant variant) {
        sven = createCarryInBetween(player, target);
        sven.setOwner(player);
        PotionEffect dolphin = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 999999999,20, false, false, false);
        sven.addPotionEffect(dolphin);
        sven.addPassenger(target);
        sven.setVariant(variant);
    }

    @Override
    public void stop() {
        sven.getPassengers().forEach(passenger -> {
            sven.removePassenger(passenger);
            passenger.teleport(sven.getLocation());
        });

        killCarryMob(sven);
    }

    @Override
    public void update() {

    }

    private static Wolf createCarryInBetween(Player player, Entity target) {
        Sven as = new Sven(target.getLocation(), player);
        ServerLevel world = ((CraftWorld) player.getWorld()).getHandle();
        world.addFreshEntity(as);

        return (Wolf) Bukkit.getEntity(as.getBukkitEntity().getUniqueId());
    }

    private static void dropCarry(Player player, Entity target) {
        Location pos = player.getLocation().add(0, 0.1, 0);
        Vector dir = player.getLocation().getDirection().setY(0).multiply(1);
        Location destination = pos.add(dir);
        destination.setX(((int) (destination.getX())) + 0.5);
        destination.setY(((int) (destination.getY())) + 0.5);
        destination.setZ(((int) (destination.getZ())) + 0.5);

        // Avoid teleport in block
        if (destination.getBlock().getBlockData().getMaterial() != Material.AIR
                || destination.clone().add(0, 1, 0).getBlock().getBlockData().getMaterial() != Material.AIR
                || destination.clone().add(0, 2, 0).getBlock().getBlockData().getMaterial() != Material.AIR) {
            target.teleport(player.getLocation());
        } else {
            target.teleport(destination);
        }
    }

    private static void killCarryMob(Wolf wolf) {
        Location tp = new Location(wolf.getWorld(), wolf.getLocation().getX(), 1.0d, wolf.getLocation().getZ());
        wolf.teleport(tp);
        wolf.setInvisible(true);
        net.minecraft.world.entity.animal.Wolf a = ((CraftWolf) (wolf)).getHandle();
        if (a instanceof Sven) {
            ((Sven) a).vanish();
        }

    }
}