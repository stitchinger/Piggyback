package io.georgeous.piggyback.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStartCarryEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    Player player;
    Entity target;
    boolean canceled;

    public PlayerStartCarryEvent(Player player, Entity target) {
        this.player = player;
        this.target = target;
        this.canceled = false;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
    }
}