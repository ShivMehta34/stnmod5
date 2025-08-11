package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.init.EntityRegistry;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.UUID;
import java.util.function.Supplier;

public class SummonClonesPacket {

    // Nightwing-only clone buffs
    private static final UUID NW_CLONE_DMG_UUID   = UUID.fromString("6f0e8e2c-7c9c-4a61-9a2e-2b2a6c2a7b11");
    private static final UUID NW_CLONE_HP_UUID    = UUID.fromString("f2b7a1d4-2a7b-4f3e-9e1c-0c6f9a8d2e44");
    private static final UUID NW_CLONE_SPEED_UUID = UUID.fromString("9b1d6c3e-2f74-4c3a-8e0f-1d2a3b4c5d6e");

    public static void encode(SummonClonesPacket msg, FriendlyByteBuf buf) {}
    public static SummonClonesPacket decode(FriendlyByteBuf buf) { return new SummonClonesPacket(); }

    public static void handle(SummonClonesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !player.isAlive()) return;

            ServerLevel world = player.serverLevel();
            Vec3 pos = player.position();

            // Class effects
            boolean hasNinja     = player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get());
            boolean hasNightwing = player.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get());

            // 2 clones normally; 4 clones if BOTH Ninja and Nightwing are active
            int cloneCount = (hasNinja && hasNightwing) ? 4 : 2;

            // Place clones in a ring around the player
            double radius = 1.8D;
            for (int i = 0; i < cloneCount; i++) {
                double angle = (Math.PI * 2.0) * i / cloneCount;
                double x = pos.x + Math.cos(angle) * radius;
                double z = pos.z + Math.sin(angle) * radius;

                NinjaCloneEntity clone = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), world);
                clone.setOwner(player);
                clone.moveTo(x, pos.y, z, player.getYRot(), player.getXRot());

                // If summoned by a Nightwing, give the clones a modest buff
                if (hasNightwing) {
                    buffCloneForNightwing(clone);
                }

                world.addFreshEntity(clone);
            }

            player.sendSystemMessage(Component.literal(
                    "Successfully summoned " + cloneCount + " clone" + (cloneCount > 1 ? "s!" : "!")
            ));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void buffCloneForNightwing(NinjaCloneEntity clone) {
        // +2 flat attack damage
        AttributeInstance atk = clone.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atk != null && atk.getModifier(NW_CLONE_DMG_UUID) == null) {
            atk.addTransientModifier(new AttributeModifier(
                    NW_CLONE_DMG_UUID, "nightwing_clone_bonus_damage", 2.0, AttributeModifier.Operation.ADDITION
            ));
        }

        // +10 max health, then heal to new max
        AttributeInstance hp = clone.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null && hp.getModifier(NW_CLONE_HP_UUID) == null) {
            hp.addTransientModifier(new AttributeModifier(
                    NW_CLONE_HP_UUID, "nightwing_clone_bonus_health", 10.0, AttributeModifier.Operation.ADDITION
            ));
            clone.setHealth(clone.getMaxHealth());
        }

        // +0.05 movement speed (base is ~0.25 for many mobs)
        AttributeInstance spd = clone.getAttribute(Attributes.MOVEMENT_SPEED);
        if (spd != null && spd.getModifier(NW_CLONE_SPEED_UUID) == null) {
            spd.addTransientModifier(new AttributeModifier(
                    NW_CLONE_SPEED_UUID, "nightwing_clone_bonus_speed", 0.05, AttributeModifier.Operation.ADDITION
            ));
        }
    }
}
