package io.georgeous.piggyback;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import java.util.Objects;


public class Sven extends Wolf {

    private final org.bukkit.entity.Player player;
    private Location lastPlayerLocation;

    public Sven(Location loc, org.bukkit.entity.Player player) {
        super(EntityType.WOLF, ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle());
        this.player = player;
        this.setHealth(20);
        this.lastPlayerLocation = loc;
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        this.setInvulnerable(true);
        this.addTag("carryhelper");
        this.setOwnerUUID(player.getUniqueId());

    }

    public boolean rideableUnderWater() {
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.4D, 3.0F, 1.5F, true));

        // Todo Faster Swimming

        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public void vanish() {
        kill();
    }

}