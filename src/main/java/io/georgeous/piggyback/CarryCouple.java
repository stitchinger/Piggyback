package io.georgeous.piggyback;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CarryCouple {
    private Entity target;
    private Player carrier;
    private Entity carryInBetween;
    public boolean passengerMode = false;

    public CarryCouple(Entity target, Player carrier, ArmorStand carryInBetween){
        this.target = target;
        this.carrier = carrier;
        if(carryInBetween == null){
            passengerMode = true;
        }
        this.carryInBetween = carryInBetween;
    }

    public void update(){

    }

    public CarryCouple(Entity target, Player carrier, Entity carryInBetween){
        this.target = target;
        this.carrier = carrier;
        this.carryInBetween = carryInBetween;
    }

    public Entity getTarget() {
        return target;
    }

    public Player getCarrier() {
        return carrier;
    }

    public Entity getCarryInBetween() {
        return carryInBetween;
    }


}
