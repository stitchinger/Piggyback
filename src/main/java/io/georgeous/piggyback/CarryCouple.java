package io.georgeous.piggyback;

import io.georgeous.piggyback.modes.CarryMode;
import io.georgeous.piggyback.modes.HybridMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CarryCouple {
    private Entity target;
    private Player carrier;
    private CarryMode currentMode;

    public CarryCouple(Entity target, Player carrier) {
        this.target = target;
        this.carrier = carrier;
        this.currentMode = new HybridMode(this);
    }

    public void update() {
        currentMode.update();
    }

    public void start() {
        currentMode.start();
    }

    public void stop() {
        currentMode.stop();
    }

    public Entity getTarget() {
        return target;
    }

    public Player getCarrier() {
        return carrier;
    }
}