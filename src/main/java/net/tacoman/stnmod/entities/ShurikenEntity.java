package net.tacoman.stnmod.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.tacoman.stnmod.init.EntityRegistry;
import net.tacoman.stnmod.init.ItemRegistry;
import net.tacoman.stnmod.init.EntityRegistry;

public class ShurikenEntity extends ThrowableItemProjectile {
    // tuning
    private static final float BASE_DAMAGE = 4.0f;  // 2 hearts
    private static final boolean PIERCE_ONCE = true; // pass through first hit? (true = can hit multiple)
    private int hits = 0;

    public ShurikenEntity(EntityType<? extends ShurikenEntity> type, Level level) { super(type, level); }

    public ShurikenEntity(Level level, LivingEntity thrower) {
        super(EntityRegistry.SHURIKEN.get(), thrower, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.SHURIKEN.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        Entity owner = getOwner();

        DamageSource src = damageSources().thrown(this, owner);
        float dmg = BASE_DAMAGE;

        if (target.hurt(src, dmg)) {
            target.hurtMarked = true;
            // tiny knock to sell the hit
            var dir = target.position().subtract(position()).normalize().scale(0.25);
            target.setDeltaMovement(target.getDeltaMovement().add(dir.x, 0.05, dir.z));
            level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.6f, 1.6f);
        }

        hits++;
        if (!PIERCE_ONCE || hits >= 1) {
            discard(); // stop after first hit unless we want piercing
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            // Hit a block => drop a recoverable shuriken (optional)
            ItemStack drop = new ItemStack(ItemRegistry.SHURIKEN.get());
            spawnAtLocation(drop, 0.1f);
            discard();
        }
    }

    @Override
    public boolean isPickable() { return true; } // allows interaction if needed

    @Override
    protected float getGravity() { return 0.01f; } // very slight drop

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("hits", hits);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        hits = tag.getInt("hits");
    }
}
