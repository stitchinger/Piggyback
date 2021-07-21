package io.georgeous.piggyback;

import io.georgeous.piggyback.modes.CarryMode;
import io.georgeous.piggyback.modes.FollowPassengerMode;
import io.georgeous.piggyback.modes.PassengerMode;
import io.georgeous.piggyback.modes.TeleportMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CarryCouple {
    private Entity target;
    private Player carrier;
    private CarryMode currentMode;

    public CarryCouple(Entity target, Player carrier){
        this.target = target;
        this.carrier = carrier;
        this.currentMode = new PassengerMode(this);
    }

    public void update(){
        currentMode.update();
        if(currentMode.toggleConditionTrue()){
            toggleMode();
        }
    }

    public void start(){
        currentMode.start();
    }

    public void stop(){
        currentMode.stop();;
    }

    public void toggleMode(){
        currentMode.stop();
        if(currentMode instanceof PassengerMode){
            currentMode = new FollowPassengerMode(this);
        } else{
            currentMode = new PassengerMode(this);
        }
        currentMode.start();
    }

    public Entity getTarget() {
        return target;
    }

    public Player getCarrier() {
        return carrier;
    }
}
