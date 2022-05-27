package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.CarryMob;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class HybridMode extends CarryMode {
    private final Player player;
    private final Entity target;
    private Wolf carryInBetween;


    public HybridMode(CarryCouple cc) {
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
    }

    @Override
    public void start() {
        carryInBetween = createCarryInBetween(player, target);
        carryInBetween.setOwner(player);
        carryInBetween.addPassenger(target);
    }

    @Override
    public void stop() {

        carryInBetween.getPassengers().forEach(passenger -> {
            System.out.println(passenger.toString());
            carryInBetween.removePassenger(passenger);
            passenger.teleport(carryInBetween.getLocation());

        });

        killCarryInBetween(carryInBetween);
    }

    @Override
    public void update() {

    }

    private static Wolf createCarryInBetween(Player player, Entity target) {
        CarryMob as = new CarryMob(target.getLocation(), player);
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

    private static void killCarryInBetween(Wolf armorStand) {
        Location tp = new Location(armorStand.getWorld(), armorStand.getLocation().getX(), 1.0d, armorStand.getLocation().getZ());
        armorStand.teleport(tp);
        net.minecraft.world.entity.animal.Wolf a = ((CraftWolf) (armorStand)).getHandle();
        if (a instanceof CarryMob) {
            ((CarryMob) a).vanish();
        }
    }
}