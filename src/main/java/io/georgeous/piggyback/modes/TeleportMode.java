package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TeleportMode extends CarryMode {

    private CarryCouple cc;
    private Player player;
    private Entity target;
    private ArmorStand carryInBetween;
    private Location lastLoc;
    private Vector lastDir;
    private Location lastDest;

    public TeleportMode(CarryCouple cc) {
        this.cc = cc;
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
        lastLoc = new Location(player.getWorld(),0,0,0);
        lastDest = new Location(player.getWorld(),0,0,0);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        dropCarry(player, target);
    }

    @Override
    public void update() {
        Vector distanceAway = player.getLocation().getDirection().normalize().multiply(-1).setY(0);
        Location destination = lastDest;
        if(!samePostion(player.getLocation(), lastLoc)){
            destination = player.getLocation().add(distanceAway);
            target.teleport(destination.setDirection(target.getLocation().getDirection()));
        }else{
            target.teleport(lastDest);
        }
        lastLoc = player.getLocation();
        lastDest = destination;
    }

    private boolean samePostion(Location newL, Location oldL) {
        return newL.getX() == oldL.getX()
                && newL.getY() == oldL.getY()
                && newL.getZ() == oldL.getZ();
    }

    @Override
    public boolean toggleConditionTrue() {
        return hasSpaceAbove(player);
    }


    private static void dropCarry(Player player, Entity target) {
        target.teleport(player.getLocation());

    }


}
