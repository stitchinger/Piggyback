package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.Sven;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class HybridMode extends CarryMode {
    private final Player player;
    private final Entity target;

    @Nullable
    private Wolf sven;

    public HybridMode(CarryCouple cc) {
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
    }

    private static Wolf createCarryInBetween(Player player, Entity target) {
        Sven as = new Sven(target.getLocation(), player);
        ServerLevel world = ((CraftWorld) player.getWorld()).getHandle();
        world.addFreshEntity(as);

        return (Wolf) Objects.requireNonNull(Bukkit.getEntity(as.getBukkitEntity().getUniqueId()));
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
        }
        else {
            target.teleport(destination);
        }
    }

    private static void killCarryMob(Wolf wolf) {
        Location tp = new Location(wolf.getWorld(), wolf.getLocation().getX(), 1.0d, wolf.getLocation().getZ());
        wolf.teleport(tp);
        wolf.setInvisible(true);
        wolf.setVisibleByDefault(false);
        wolf.remove();
        net.minecraft.world.entity.animal.wolf.Wolf craftWolf = ((CraftWolf) (wolf)).getHandle();

        if (craftWolf instanceof Sven sven) {
            sven.vanish();
        }
    }

    @Override
    public void start(Wolf.Variant variant) {
        sven = createCarryInBetween(player, target);
        sven.setOwner(player);
        PotionEffect dolphin = new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 999999999, 20, false, false, false);
        sven.addPotionEffect(dolphin);
        ArmorStand inBetween = (ArmorStand) target.getWorld().spawnEntity(target.getLocation(), EntityType.ARMOR_STAND);
        inBetween.setMarker(true);
        inBetween.setInvulnerable(true);
        inBetween.setInvisible(true);
        inBetween.addScoreboardTag("inBetweenCarryEntity");
        sven.addPassenger(inBetween);
        inBetween.addPassenger(target);
        sven.setVariant(variant);
    }

    @Override
    public void stop() {
        if (sven == null) {
            return;
        }

        sven.getPassengers().forEach(passenger -> {
            sven.removePassenger(passenger);
            if (passenger.getScoreboardTags().contains("inBetweenCarryEntity")) {
                passenger.getPassengers().forEach(passenger::removePassenger);
                passenger.remove();
            }
            passenger.teleport(sven.getLocation());
        });

        killCarryMob(sven);
    }

    @Override
    public void update() {

    }
}