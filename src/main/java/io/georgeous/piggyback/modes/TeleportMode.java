package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportMode extends CarryMode{

    private CarryCouple cc;
    private Player player;
    private Entity target;
    private ArmorStand carryInBetween;

    public TeleportMode(CarryCouple cc){
        this.cc = cc;
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void update() {
        Location destination = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(-1).setY(0));
        target.teleport(destination);
    }

    @Override
    public boolean toggleConditionTrue(){
        return hasSpaceAbove(player);
    }


}
