package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassengerMode extends CarryMode{

    private CarryCouple cc;
    private Player player;
    private Entity target;
    private ArmorStand carryInBetween;

    public PassengerMode(CarryCouple cc){
        this.cc = cc;
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
    }


    @Override
    public void start() {
        carryInBetween = createCarryInBetween(player.getLocation());
        carryInBetween.addPassenger(target);
        player.addPassenger(carryInBetween);
    }

    @Override
    public void stop() {
        for (Entity passenger : player.getPassengers()) {
            player.removePassenger(passenger);
        }
        for (Entity passenger : carryInBetween.getPassengers()) {
            player.removePassenger(passenger);
        }
        killCarryInBetween(carryInBetween);
        dropCarry(player, target);
    }

    @Override
    public void update() {
        avoidWaterDismount();
    }

    @Override
    public boolean toggleConditionTrue(){
       return !hasSpaceAbove(player);
    }

    private void avoidWaterDismount(){
        if(!player.getPassengers().contains(carryInBetween)){
            player.addPassenger(carryInBetween);
        }
        if(!carryInBetween.getPassengers().contains(target)){
            carryInBetween.addPassenger(target);
        }
    }

    private static ArmorStand createCarryInBetween(Location location) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as.setCanPickupItems(false);
        as.setInvisible(true);
        as.setInvulnerable(true);
        as.setSmall(true);
        as.setMarker(false);
        as.setBasePlate(false);
        as.setCanPickupItems(false);
        as.setCollidable(false);
        as.addScoreboardTag("carryhelper");
        return as;
    }

    private static void dropCarry(Player player, Entity target) {
        // tp carry-target in front of player
        Location pos = player.getLocation().add(0, 0.1, 0);
        Vector dir = player.getLocation().getDirection().setY(0).multiply(1);
        Location destination = pos.add(dir);

        // Avoid teleport in block
        if (destination.getBlock().getBlockData().getMaterial() != Material.AIR
                || destination.add(0,1,0).getBlock().getBlockData().getMaterial() != Material.AIR) {
            target.teleport(player.getLocation());
        } else{
            target.teleport(destination);
        }
    }

    private static void killCarryInBetween(Entity entity){
        entity.teleport(new Location(entity.getWorld(), entity.getLocation().getX(), 250, entity.getLocation().getZ()));
        if(entity instanceof LivingEntity){
            ((LivingEntity) entity).setHealth(0);
        }
    }


}
