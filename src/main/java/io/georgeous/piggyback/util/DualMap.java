package io.georgeous.piggyback.util;

import io.georgeous.piggyback.CarryCouple;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DualMap {
    public Map<Player, CarryCouple> carriers = new HashMap<>();
    public Map<Entity, CarryCouple> carried = new HashMap<>();

    public DualMap(){
        carriers = new HashMap<>();
        carried = new HashMap<>();
    }

    public CarryCouple get(Player player){
        return carriers.get(player);
    }

    public CarryCouple get(Entity entity){
        return carried.get(entity);
    }

    public void put(Player player, Entity entity, CarryCouple carryCouple){
        carriers.put(player, carryCouple);
        carried.put(entity, carryCouple);
    }

    public void remove(Player player){
        Entity target = carriers.get(player).getTarget();
        carried.remove(target);
        carriers.remove(player);
    }

    public void remove(Entity entity){
        Player player = carried.get(entity).getCarrier();
        carriers.remove(player);
        carried.remove(entity);
    }

    public boolean containsKey(Object object){
        return carriers.containsKey(object) || carried.containsKey(object);
    }

    public boolean isCarrier(Object object){
        return carriers.containsKey(object);
    }

    public boolean isCarried(Object object){
        return carried.containsKey(object);
    }
}
