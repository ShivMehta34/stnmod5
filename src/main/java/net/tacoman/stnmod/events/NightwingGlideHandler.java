package net.tacoman.stnmod.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightwingGlideHandler {

    private static final String GLIDE_ACTIVE = "stnmod_nightwing_glide_active";

    // Elytra-ish feel (tweak to taste)
    private static final double H_ACCEL     = 0.08;   // horizontal acceleration
    private static final double MAX_H_SPEED = 1.20;   // cap horizontal speed
    private static final double DRAG        = 0.991;  // air drag
    private static final double BASE_SINK   = -0.08;  // baseline descent
    private static final double LIFT_UP     = 0.03;   // small upward when looking up
    private static final double DIVE_DOWN   = -0.12;  // stronger descent when looking down
    private static final double MAX_DOWN    = -1.2;   // hard cap downward speed

    /** Called by packet: turn server glide on/off */
    public static void setGlideActive(Player player, boolean active) {
        if (!(player instanceof ServerPlayer sp)) return;
        var tag = sp.getPersistentData();

        if (active) {
            // Nightwing only & must be airborne
            if (!sp.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get())) return;
            if (sp.onGround() || sp.isInWaterOrBubble() || sp.isPassenger()) return;
            tag.putBoolean(GLIDE_ACTIVE, true);
            // tiny QoL: smooth engage (optional)
            sp.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOW_FALLING, 10, 0, false, false));
        } else {
            tag.remove(GLIDE_ACTIVE);
        }
    }

    /** Server physics while glide flag is set */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        var tag = sp.getPersistentData();
        if (!tag.getBoolean(GLIDE_ACTIVE)) return;

        // auto-stop when not valid
        if (sp.onGround() || sp.isInWaterOrBubble() || sp.isPassenger()
                || !sp.isAlive() || !sp.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get())) {
            tag.remove(GLIDE_ACTIVE);
            return;
        }

        Vec3 look = sp.getLookAngle();
        Vec3 vel  = sp.getDeltaMovement();

        // Horizontal accel toward look direction
        Vec3 fwd = new Vec3(look.x, 0, look.z);
        if (fwd.lengthSqr() > 1.0E-4) {
            fwd = fwd.normalize();
            Vec3 target = fwd.scale(Math.min(MAX_H_SPEED, Math.hypot(vel.x, vel.z) + H_ACCEL));
            Vec3 curH = new Vec3(vel.x, 0, vel.z);
            Vec3 newH = curH.scale(0.90).add(target.scale(0.10));
            vel = new Vec3(newH.x, vel.y, newH.z);
        }

        // Vertical behavior by pitch (look.y is vertical component)
        // base sink every tick
        double vy = vel.y + BASE_SINK;

        // add lift/dive bias based on where you're looking
        if (look.y > 0.15) {           // looking up a bit → slight lift
            vy = Math.min(vy + LIFT_UP * look.y, 0.10);
        } else if (look.y < -0.15) {   // looking down → stronger descent (faster travel feel)
            vy = Math.max(vy + DIVE_DOWN * (-look.y), MAX_DOWN);
        }

        // drag
        vel = new Vec3(vel.x * DRAG, vy * DRAG, vel.z * DRAG);

        sp.setDeltaMovement(vel);
        sp.hurtMarked = true;
        sp.fallDistance = 0.0F; // no fall damage while gliding
    }

    /** Belt-and-suspenders: remove fall damage if somehow flagged */
    @SubscribeEvent
    public static void onFall(LivingFallEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.getPersistentData().getBoolean(GLIDE_ACTIVE)) {
            e.setDamageMultiplier(0.0F);
        }
    }
}
