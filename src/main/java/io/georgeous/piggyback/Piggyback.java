package io.georgeous.piggyback;


import io.georgeous.piggyback.listeners.EventReactor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;


public final class Piggyback extends JavaPlugin {

    private static boolean configAltMode = false;

    private static final boolean NEED_ITEM = true;
    private static final String ITEM_NAME = "Baby-Handler";
    public static boolean passengerMode = false;

    public static Map<Player, CarryCouple> carryPairs = new HashMap<>();


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventReactor(), this);
        getServer().getPluginCommand("carry").setExecutor(new CarryCommand());

        for(Player player : Bukkit.getOnlinePlayers()){
            player.setInvulnerable(false);
        }

        // Start Update function
        new BukkitRunnable() {
            @Override
            public void run() {
                carryUpdate();

            }
        }.runTaskTimer(this, 0L, 1L);
    }

    public void carryUpdate(){
        for (Map.Entry<Player, CarryCouple> entry : carryPairs.entrySet()) {
            CarryCouple ct = entry.getValue();
            Player carrier = ct.getCarrier();
            Entity target = ct.getTarget();

            if(ct.passengerMode){
                avoidWaterDismount(ct);
                // If Block above
                    // toggle Mode

            }else{
                Location destination = carrier.getLocation().add(carrier.getLocation().getDirection().normalize().multiply(-1));
                target.teleport(destination);

                // If No Block above
                    // toggle Mode
            }
        }
    }

    public void toggleMode(CarryCouple carryCouple){
        Player player = carryCouple.getCarrier();
        Entity target = carryCouple.getTarget();

        //stopCarry(player);
        passengerMode = !passengerMode;
        //startCarry(player, target);
    }

    private void avoidWaterDismount(CarryCouple ct){
        Player carrier = ct.getCarrier();
        Entity target = ct.getTarget();
        Entity carryHelper = ct.getCarryInBetween();

        if(!carrier.getPassengers().contains(carryHelper)){
            carrier.addPassenger(carryHelper);
        }
        if(!carryHelper.getPassengers().contains(target)){
            carryHelper.addPassenger(target);
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Player, CarryCouple> entry : carryPairs.entrySet()) {
            stopCarry(entry.getKey());
        }
    }

    public static boolean isProperItem(Player player) {
        // Check for correct item in Hand
        if (!NEED_ITEM) {
            return true;
        }
        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        boolean properItem = false;
        if (meta != null) {
            if (meta.hasDisplayName()) {
                properItem = meta.getDisplayName().contains(ITEM_NAME);
            }
        }
        return properItem;
    }

    public static void startCarry(Player player, Entity target) {
        World world = player.getWorld();

        target.setInvulnerable(true);

        ArmorStand carryInBetween = null;
        if(passengerMode){
            //passengerizeThem(player, target);
            carryInBetween = createCarryHelper(player.getLocation());
            carryInBetween.addPassenger(target);
            player.addPassenger(carryInBetween);
        }


        carryPairs.put(player, new CarryCouple(target, player, carryInBetween));
        startCarryEffects(world, target.getLocation());
    }


    public static void stopCarry(Player player) {
        CarryCouple carryCouple = carryPairs.get(player);
        Entity target = carryCouple.getTarget();
        target.setInvulnerable(false);

        if(passengerMode){
            for (Entity passenger : player.getPassengers()) {
                player.removePassenger(passenger);
            }

            Entity carryInBetween = carryCouple.getCarryInBetween();
            for (Entity passenger : carryInBetween.getPassengers()) {
                player.removePassenger(passenger);
            }
            killEntity(carryInBetween);
            dropCarry(player, target);
        }

        carryPairs.remove(player);
        stopCarryEffects(player.getWorld(), player.getLocation());
    }

    private static void killEntity(Entity entity){
        entity.teleport(new Location(entity.getWorld(), entity.getLocation().getX(), 250, entity.getLocation().getZ()));
        if(entity instanceof LivingEntity){
            ((LivingEntity) entity).setHealth(0);
        }
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



    private static ArmorStand createCarryHelper(Location location) {
        ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

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


    /*
    public static void startCarryAlt(Player player, Entity target) {
        World world = player.getWorld();
        target.setInvulnerable(true);
        target.addScoreboardTag("nocollision");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "team join nocollision @e[tag=nocollision,sort=nearest]");
        startCarryEffects(world, target.getLocation());
    }

    public static void stopCarryAlt(Player player) {
        World world = player.getWorld();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "team leave @e[tag=nocollision,limit=1,sort=nearest]");

        Entity target = carryPairs.get(player).getTarget();

        target.removeScoreboardTag("nocollision");
        target.setInvulnerable(false);
        //((LivingEntity) target).setAI(true);

        carryPairs.remove(player);
        stopCarryEffects(world, player.getLocation());
    }


     */
    public static void startCarryEffects(World world, Location pos) {
        world.playSound(pos, Sound.ITEM_ARMOR_EQUIP_TURTLE, 1, 1);
        //world.spawnParticle(Particle.BLOCK_DUST, pos, 5, 0.5, 0.5, 0.5);
    }

    public static void stopCarryEffects(World world, Location pos) {
        world.playSound(pos, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
    }

    public static void setAltMode(boolean value) {
        configAltMode = value;
    }

    public static boolean isAltMode() {
        return configAltMode;
    }
}
