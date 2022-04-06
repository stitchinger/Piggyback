package io.georgeous.piggyback;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;

import javax.annotation.Nullable;
import java.util.Objects;


public class MyArmor extends ArmorStand {

    private final org.bukkit.entity.Player player;
    private Location lastPlayerLocation;

    public MyArmor(Location loc, org.bukkit.entity.Player player) {
        super(EntityType.ARMOR_STAND, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.player = player;

        this.lastPlayerLocation = loc;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setNoGravity(false);
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.setSmall(true);
        this.setMarker(true);
        this.setNoBasePlate(true);
        this.addTag("carryhelper");
    }

    @Override
    public double getPassengersRidingOffset() {
        //return (double)this.super..dimensions.height * 0.75D;
        return 10.0D;
    }

    public boolean rideableUnderWater() {
        return true;
    }


    @Override
    public void tick() {
        super.tick();
        this.refreshDimensions();

        Location myLocation = new Location(player.getWorld(), this.getX(), this.getY(), this.getZ());
        Location playerLocation = player.getLocation();
        Location destination = myLocation;
        double distance = playerLocation.distance(myLocation);
        double minDistance = 2;
        if (lastPlayerLocation.getBlock() != playerLocation.getBlock()) {
            destination = lastPlayerLocation.getBlock().getLocation().add(0.5, 0, 0.5);
        }

        Location movement = myLocation.clone().add(destination.clone().subtract(myLocation).multiply(0.1));


        if (distance > minDistance) {
            this.setPos(movement.getX(), movement.getY(), movement.getZ());
        }

        lastPlayerLocation = playerLocation;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entityliving) {
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }


    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public InteractionResult interactAt(Player entityhuman, Vec3 vec3d, InteractionHand enumhand) {
        return InteractionResult.PASS;
    }

    public void vanish() {
        kill();
    }
}