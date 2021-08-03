package io.georgeous.piggyback;

import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class MyArmor extends EntityArmorStand{

    private Player player;
    private boolean followMode;
    private Location lastLoc;

    public MyArmor(Location loc, Player player, boolean followMode){
        super(EntityTypes.c, ((CraftWorld)loc.getWorld()).getHandle());
        this.player = player;
        this.followMode = followMode;
        this.lastLoc = new Location(player.getWorld(), 0,0,0);

        this.setPosition(loc.getX(),loc.getY(),loc.getZ());
        this.setNoGravity(true);
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.setSmall(true);
        this.setMarker(true);
        this.setBasePlate(false);
        this.addScoreboardTag("carryhelper");
    }

    @Override
    public void tick(){
        super.tick();
        updateSize();

        Location myLocation = new Location(player.getWorld(), this.locX(), this.locY(), this.locZ());
        Location destination = myLocation;

        if(true){ // Follow behind
            double minDistance = 2;

            Location difference = player.getLocation().subtract(myLocation);
            double distance = player.getLocation().distance(myLocation);

            if(distance > minDistance){
                destination = player.getLocation().subtract(difference.toVector().normalize().multiply(2));
                destination.setY(player.getLocation().getY());
            }

        }else{ // Carry on top Mode
            destination = player.getLocation().clone().add(0,2.5,0);
        }



        this.setPosition(destination.getX(),destination.getY(), destination.getZ());
    }

    public void setFollowMode(boolean value){
        followMode = value;
    }

    @Override
    public Vec3D b(EntityLiving entityliving) {
        return new Vec3D(this.locX(), 5, this.locZ());
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        return EnumInteractionResult.d;
    }


    private boolean samePostion(Location newL, Location oldL) {
        return newL.getX() == oldL.getX()
                && newL.getY() == oldL.getY()
                && newL.getZ() == oldL.getZ();
    }

    public void changeMode(){

    }

    public void vanish(){
        killEntity();
    }



}
