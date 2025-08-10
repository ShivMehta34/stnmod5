package net.tacoman.stnmod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Local-only camera spin for Whirlwind; does NOT sync to other players. */
@Mod.EventBusSubscriber(modid = "stnmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WhirlwindClientSpin {
    private static long endMs = 0L;
    private static final float SPIN_PER_TICK_DEG = 50f; // tweak if you want faster/slower

    /** Start spinning camera locally for the given duration (ms). */
    public static void startForMs(long ms) {
        endMs = System.currentTimeMillis() + ms;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        var mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (System.currentTimeMillis() > endMs) return;

        float yaw = mc.player.getYRot();
        mc.player.setYRot(yaw + SPIN_PER_TICK_DEG);
        mc.player.yHeadRot = mc.player.getYRot();
        mc.player.yBodyRot = mc.player.getYRot();
    }
}
