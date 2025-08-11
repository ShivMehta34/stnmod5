package net.tacoman.stnmod.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ObjectHolder;
import net.tacoman.stnmod.stnmod;

public class NinjaCloneEntity extends TamableAnimal {

    public static final String NINJA_CLONE_REGISTRY_NAME = "ninja_clone";

    @ObjectHolder(registryName = stnmod.MODID, value = stnmod.MODID + ":ninja_clone")
    public static final EntityType<NinjaCloneEntity> TYPE = null;

    private ServerPlayer owner;
    private int lifespan = 400; // 20 seconds * 20 ticks per second

    public NinjaCloneEntity(EntityType<? extends TamableAnimal> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public void setOwner(ServerPlayer owner) {
        this.owner = owner;
        this.setTame(true);
        this.setOwnerUUID(owner.getUUID());
    }

    public ServerPlayer getOwner() {
        return owner;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return true;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null; // Ninja clones do not breed
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.lifespan-- <= 0) {
            this.remove(RemovalReason.DISCARDED); // Despawn after 20 seconds
        }
    }
}
