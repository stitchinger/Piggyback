package io.georgeous.piggyback.modes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CarryMode {


    public abstract void start();

    public abstract void stop();

    public abstract void update();

    public boolean toggleConditionTrue(){
        return false;
    }

    public boolean hasSpaceAbove(Player player){
        List<Location> locs = new ArrayList<>();
        locs.add(player.getLocation().clone().add(-1,2,-1));
        locs.add(player.getLocation().clone().add(-1,2,0));
        locs.add(player.getLocation().clone().add(-1,2,1));
        locs.add(player.getLocation().clone().add(0,2,-1));
        locs.add(player.getLocation().clone().add(0,2,0));
        locs.add(player.getLocation().clone().add(0,2,1));
        locs.add(player.getLocation().clone().add(1,2,-1));
        locs.add(player.getLocation().clone().add(1,2,0));
        locs.add(player.getLocation().clone().add(1,2,1));

        locs.add(player.getLocation().clone().add(-1,3,-1));
        locs.add(player.getLocation().clone().add(-1,3,0));
        locs.add(player.getLocation().clone().add(-1,3,1));
        locs.add(player.getLocation().clone().add(0,3,-1));
        locs.add(player.getLocation().clone().add(0,3,1));
        locs.add(player.getLocation().clone().add(1,3,-1));
        locs.add(player.getLocation().clone().add(1,3,0));
        locs.add(player.getLocation().clone().add(1,3,1));
        locs.add(player.getLocation().clone().add(0,3,0));

        boolean value = true;
        for(Location l : locs){
            value = value && noBlockThere(l);
        }
        return value;
    }

    public boolean noBlockThere(Location loc){
        return loc.getBlock().getBlockData().getMaterial() == Material.AIR;
    }


}
