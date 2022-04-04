package io.georgeous.piggyback;

//import net.minecraft.world.EnumHand;
import net.minecraft.world.InteractionHand;
//import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.decoration.EntityArmorStand;
//import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;


public class MyArmor extends ArmorStand {

    private org.bukkit.entity.Player player;
    private Location lastPlayerLocation;
    private boolean followMode;
    private Location lastLoc;

    public MyArmor(Location loc, org.bukkit.entity.Player player, boolean followMode) {
        super(EntityType.ARMOR_STAND, ((CraftWorld) loc.getWorld()).getHandle());
        this.player = player;
        this.followMode = followMode;
        this.lastLoc = new Location(player.getWorld(), 0, 0, 0);

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
        if(lastPlayerLocation.getBlock() != playerLocation.getBlock()){
            destination = lastPlayerLocation.getBlock().getLocation().add(0.5,0,0.5);
        }

        //Location movement = myLocation.add(destination.multiply(0.1));
        Location movement = myLocation.clone().add(destination.clone().subtract(myLocation).multiply(0.1));


        if (distance > minDistance) {
            this.setPos(movement.getX(), movement.getY(), movement.getZ());
        }

        lastPlayerLocation = playerLocation;

    }


    public void oldtick() {
        super.tick();
        this.refreshDimensions();


        Location myLocation = new Location(player.getWorld(), this.getX(), this.getY(), this.getZ());
        Location destination = myLocation;

        if (true) { // Follow behind
            double minDistance = 2;

            Location difference = player.getLocation().subtract(myLocation);
            double distance = player.getLocation().distance(myLocation);

            if (distance > minDistance) {

                destination = player.getLocation().subtract(difference.toVector().normalize().multiply(2));
                destination.setY(player.getLocation().getY());
            }

        } else { // Carry on top Mode
            destination = player.getLocation().clone().add(0, 2.5, 0);
        }


        this.setPos(destination.getX(), destination.getY(), destination.getZ());

    }

    public void setFollowMode(boolean value) {
        followMode = value;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entityliving) {
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public InteractionResult interactAt(Player entityhuman, Vec3 vec3d, InteractionHand enumhand) {
        return InteractionResult.PASS;
    }

    private boolean samePostion(Location newL, Location oldL) {
        return newL.getX() == oldL.getX()
                && newL.getY() == oldL.getY()
                && newL.getZ() == oldL.getZ();
    }

    public void changeMode() {

    }

    public void vanish() {
        kill();
    }
}