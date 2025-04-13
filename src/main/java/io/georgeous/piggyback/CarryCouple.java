package io.georgeous.piggyback;

import io.georgeous.piggyback.modes.CarryMode;
import io.georgeous.piggyback.modes.HybridMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CarryCouple {
    private final Entity target;
    private final Player carrier;
    private final CarryMode currentMode;

    public CarryCouple(Entity target, Player carrier) {
        this.target = target;
        this.carrier = carrier;
        this.currentMode = new HybridMode(this);
    }

    public void update() {
        currentMode.update();
    }

    public void start(Wolf.Variant variant) {
        currentMode.start(variant);
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