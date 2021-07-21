package io.georgeous.piggyback.modes;

import io.georgeous.piggyback.CarryCouple;
import io.georgeous.piggyback.MyArmor;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class FollowPassengerMode extends CarryMode{

    private CarryCouple cc;
    private Player player;
    private Entity target;
    private ArmorStand carryInBetween;

    private long spaceAboveTime = 0;
    private long lastTime;

    public FollowPassengerMode(CarryCouple cc){
        this.cc = cc;
        this.player = cc.getCarrier();
        this.target = cc.getTarget();
        this.lastTime = System.currentTimeMillis();
    }


    @Override
    public void start() {
        //MyArmor myArmor = new MyArmor(player.getLocation());

        carryInBetween = createCarryInBetween(player);
        carryInBetween.addPassenger(target);
        //player.addPassenger(carryInBetween);

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
        if(hasSpaceAbove(player)){
            // startCountdown
            spaceAboveTime = spaceAboveTime + (System.currentTimeMillis() - lastTime);
        }else{
            spaceAboveTime = 0;
        }
        lastTime = System.currentTimeMillis();
        //player.sendMessage(carryInBetween.getLocation().getY() + "");
    }

    @Override
    public boolean toggleConditionTrue(){
        return hasSpaceAbove(player) && spaceAboveTime >= 2000;
    }

    private void avoidWaterDismount(){
        if(!player.getPassengers().contains(carryInBetween)){
            //player.addPassenger(carryInBetween);
        }
        if(!carryInBetween.getPassengers().contains(target)){
            carryInBetween.addPassenger(target);
        }
    }

    private static ArmorStand createCarryInBetween(Player player) {
        MyArmor as = new MyArmor(player.getLocation(),player, true);
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        world.addEntity(as);


        ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(as.getBukkitEntity().getUniqueId());
        return armorStand;
    }

    private static void dropCarry(Player player, Entity target) {
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

    private static void killCarryInBetween(ArmorStand armorStand){
        EntityArmorStand a = ((CraftArmorStand)(armorStand)).getHandle();
        if(a instanceof MyArmor){
            ((MyArmor) a).vanish();
        }
    }


}