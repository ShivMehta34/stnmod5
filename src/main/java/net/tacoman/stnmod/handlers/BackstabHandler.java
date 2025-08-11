package net.tacoman.stnmod.handlers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.items.DaggerItem;
import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public final class BackstabHandler {

    private BackstabHandler() {}

    private static final String NBT_ARMED_UNTIL = "stn_backstab_armed_until";
    private static final boolean DEBUG = false; // set true to log reasons

    // Window to land a hit after arming (ticks). 60 = 3s.
    private static final int ARM_WINDOW_TICKS = 60;

    // Damage numbers
    private static final float BASE_DAMAGE = 40.0f;   // your original value
    private static final float ASSASSIN_MULT = 3.0f;  // triples if Assassin also active

    /** Called by your server packet from the keybind. */
    public static void arm(ServerPlayer sp) {
        if (!sp.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            if (DEBUG) stnmod.LOGGER.info("[Backstab] arm(): not a Thief");
            return;
        }
        long now = sp.serverLevel().getGameTime();
        sp.getPersistentData().putLong(NBT_ARMED_UNTIL, now + ARM_WINDOW_TICKS);
        if (DEBUG) stnmod.LOGGER.info("[Backstab] armed until {}", now + ARM_WINDOW_TICKS);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ServerLevel level = sp.serverLevel();
        Entity tgt = event.getTarget();
        if (!(tgt instanceof LivingEntity target)) return;

        long now = level.getGameTime();
        long armedUntil = sp.getPersistentData().getLong(NBT_ARMED_UNTIL);
        if (armedUntil == 0L) { if (DEBUG) stnmod.LOGGER.info("[Backstab] not armed"); return; }
        if (now > armedUntil) { if (DEBUG) stnmod.LOGGER.info("[Backstab] arm expired"); return; }

        // Must still be Thief
        if (!sp.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            if (DEBUG) stnmod.LOGGER.info("[Backstab] not a Thief at hit time");
            return;
        }

        // Must be holding a dagger in main hand
        if (!(sp.getMainHandItem().getItem() instanceof DaggerItem)) {
            if (DEBUG) stnmod.LOGGER.info("[Backstab] not holding dagger");
            return;
        }

        // OPTIONAL checks that can cause flakiness — disabled by default:
        // 1) Attack strength — many misses were this being < 1.0
        // if (sp.getAttackStrengthScale(0f) < 0.2f) { if (DEBUG) stnmod.LOGGER.info("[Backstab] low attack strength"); return; }

        // 2) Angle check — players/targets rotate a lot; allow 360° for reliability
        // if (!isBehindOrSide(sp, target)) { if (DEBUG) stnmod.LOGGER.info("[Backstab] not behind/side"); return; }

        // Compute damage; only consume arm if damage actually lands
        float damage = BASE_DAMAGE;
        if (sp.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) damage *= ASSASSIN_MULT;

        float before = target.getHealth();
        boolean hurt = target.hurt(level.damageSources().playerAttack(sp), damage);

        if (!hurt || target.getHealth() == before) {
            if (DEBUG) stnmod.LOGGER.info("[Backstab] damage failed (immune/canceled?)");
            return; // do NOT consume arm if nothing landed
        }

        // Consume the arm ONLY after a successful hit
        sp.getPersistentData().putLong(NBT_ARMED_UNTIL, 0L);

        // Apply bleed (duration in ticks). Bump if you want longer bleed.
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                PotionEffectRegistry.BLEED.get(), 100, 1
        ));

        // Feedback
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.2f);
        if (DEBUG) stnmod.LOGGER.info("[Backstab] success: dealt {} dmg", (before - target.getHealth()));

        // Optional snappy feel
        sp.resetAttackStrengthTicker();
    }

    // If you decide to re-enable directional gating later:
    /*
    private static boolean isBehindOrSide(Player attacker, LivingEntity target) {
        var toAttacker = attacker.position().subtract(target.position()).normalize();
        var targetLook = target.getLookAngle().normalize();
        double dot = targetLook.dot(toAttacker); // 1 front, 0 side, -1 behind
        return dot <= 0.5; // allow side/behind
    }
    */
}
