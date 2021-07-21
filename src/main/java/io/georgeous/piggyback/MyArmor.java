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

        if(followMode){
            double minDistance = 2;
           // Vector distanceAway = player.getLocation().getDirection().setY(0).normalize().multiply(minDistance * (-1)).setY(0);
            Location myLocation = new Location(player.getWorld(), this.locX(), this.locY(), this.locZ());

            Location difference = player.getLocation().subtract(myLocation); // 5,5 - 0,0 = 5,5

            double distance = player.getLocation().distance(myLocation);

            if(distance > minDistance){
                Location destination = myLocation.add(difference.multiply(0.1));
                this.setPosition(destination.getX(), destination.getY(), destination.getZ());
            }
            lastLoc = player.getLocation();

        }else{
            this.setPosition(player.getLocation().getX(), player.getLocation().getY() + 2.5, player.getLocation().getZ());
        }
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
