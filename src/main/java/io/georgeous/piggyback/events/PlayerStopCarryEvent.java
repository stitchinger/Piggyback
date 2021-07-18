package io.georgeous.piggyback.events;

import io.georgeous.piggyback.Piggyback;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStopCarryEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    Player player;
    Entity target;

    public PlayerStopCarryEvent(Player player){
        this.player = player;
        this.target = Piggyback.carryPairs.get(this.player).getTarget();
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }
}
