package io.georgeous.piggyback;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;


public class CarryMob extends Wolf {

    private final org.bukkit.entity.Player player;
    private Location lastPlayerLocation;

    public CarryMob(Location loc, org.bukkit.entity.Player player) {
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

    //@Override
    public void tickBackup() {
        super.tick();
        this.refreshDimensions();

        Location myLocation = new Location(player.getWorld(), this.getX(), this.getY(), this.getZ());
        Location playerLocation = player.getLocation();
        Location destination = myLocation;
        double distance = playerLocation.distance(myLocation);
        double minDistance = 2;
        if (lastPlayerLocation.getBlock() != playerLocation.getBlock()) {
            destination = lastPlayerLocation.getBlock().getLocation().add(0.5, 0, 0.5);
        }

        Location movement = myLocation.clone().add(destination.clone().subtract(myLocation).multiply(0.1));


        if (distance > minDistance) {
            this.setPos(movement.getX(), movement.getY(), movement.getZ());
        }

        lastPlayerLocation = playerLocation;
    }


    public void vanish() {
        kill();
    }

    private static class CustomGoal extends Goal {

        public static final float DEFAULT_PROBABILITY = 0.02F;
        protected final Mob mob;
        @Nullable
        protected Entity lookAt;
        protected final float lookDistance;
        private int lookTime;
        protected final float probability;
        private final boolean onlyHorizontal;
        protected final Class<? extends LivingEntity> lookAtType;
        protected final TargetingConditions lookAtContext;


        public CustomGoal(Mob mob, Class<? extends LivingEntity> targetClass, float lookDistance) {
            this(mob, targetClass, lookDistance, 0.02F);
        }

        public CustomGoal(Mob mob, Class<? extends LivingEntity> targetClass, float lookDistance, float probability) {
            this(mob, targetClass, lookDistance, probability, false);
        }

        public CustomGoal(Mob mob, Class<? extends LivingEntity> targetClass, float lookDistance, float probability, boolean onlyHorizontal) {
            this.mob = mob;
            this.lookAtType = targetClass;
            this.lookDistance = lookDistance;
            this.probability = probability;
            this.onlyHorizontal = onlyHorizontal;
            this.setFlags(EnumSet.of(Flag.LOOK));
            if (targetClass == Player.class) {
                this.lookAtContext = TargetingConditions.forNonCombat().range((double)lookDistance).selector((var1x) -> {
                    return EntitySelector.notRiding(mob).test(var1x);
                });
            } else {
                this.lookAtContext = TargetingConditions.forNonCombat().range((double)lookDistance);
            }

        }

        public boolean canUse() {
            if (this.mob.getRandom().nextFloat() >= this.probability) {
                return false;
            } else {
                if (this.mob.getTarget() != null) {
                    this.lookAt = this.mob.getTarget();
                }

                if (this.lookAtType == Player.class) {
                    this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                } else {
                    this.lookAt = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance), (var0) -> {
                        return true;
                    }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                }

                return this.lookAt != null;
            }
        }

        public boolean canContinueToUse() {
            if (!this.lookAt.isAlive()) {
                return false;
            } else if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
                return false;
            } else {
                return this.lookTime > 0;
            }
        }

        public void start() {
            this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
        }

        public void stop() {
            this.lookAt = null;
        }

        public void tick() {
            if (this.lookAt.isAlive()) {
                double var0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
                this.mob.getLookControl().setLookAt(this.lookAt.getX(), var0, this.lookAt.getZ());
                --this.lookTime;
            }
        }
    }
}