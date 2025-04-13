package io.georgeous.piggyback.modes;

import org.bukkit.entity.Wolf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CarryMode {

    public abstract void start(Wolf.Variant variant);

    public abstract void stop();

    public abstract void update();
}