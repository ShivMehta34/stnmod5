package net.tacoman.stnmod.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.tacoman.stnmod.init.EntityRegistry;
import net.tacoman.stnmod.init.ItemRegistry;

public class ShurikenEntity extends ThrowableItemProjectile {

    // ---- Tunables ----
    private static final float BASE_DAMAGE = 4.0f;   // 2 hearts
    private static final boolean PIERCE = false;     // set true to pass through first hit
    private static final float KNOCKBACK = 0.35f;
    private static final float GRAVITY = 0.01f;

    private int hits = 0;

    // Required empty ctor for the engine
    public ShurikenEntity(EntityType<? extends ShurikenEntity> type, Level level) {
        super(type, level);
    }

    // Convenience ctor used by the item
    public ShurikenEntity(Level level, LivingEntity thrower) {
        super(EntityRegistry.SHURIKEN.get(), thrower, level);
        this.setOwner(thrower);
    }

    // Make sure clients can see spawns from server
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // For ThrownItemRenderer (uses the item's texture/model)
    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.SHURIKEN.get();
    }

    @Override
    protected float getGravity() {
        return GRAVITY;
    }

    // -- Collision with entities --
    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (level().isClientSide) return;

        Entity target = hit.getEntity();
        Entity owner = getOwner();

        // Build a proper thrown damage source
        DamageSource src = this.damageSources().thrown(this, owner);

        boolean damaged = target.hurt(src, BASE_DAMAGE);
        if (damaged) {
            // tiny push away to sell impact
            Vec3 push = target.position().subtract(this.position()).normalize().scale(KNOCKBACK);
            target.setDeltaMovement(target.getDeltaMovement().add(push.x, 0.05, push.z));
            target.hurtMarked = true;
            level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.TRIDENT_HIT, SoundSource.PLAYERS, 0.7f, 1.5f);
        }

        hits++;
        if (!PIERCE || hits >= 1) {
            this.discard(); // stop after first hit (default)
        }
    }

    // -- Collision with blocks --
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            // Drop a recoverable shuriken on block hit
            spawnAtLocation(new ItemStack(ItemRegistry.SHURIKEN.get()), 0.1f);
            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.TRIDENT_HIT_GROUND, SoundSource.PLAYERS, 0.6f, 1.2f);
            this.discard();
        }
    }

    // Save/Load (optional)
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
