package net.tacoman.stnmod.events;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickScheduler {
    private static final List<Entry> TASKS = new ObjectArrayList<>();

    public static void runLater(ServerLevel level, int delayTicks, Runnable task) {
        TASKS.add(new Entry(delayTicks, task));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        for (int i = TASKS.size() - 1; i >= 0; i--) {
            Entry en = TASKS.get(i);
            en.ticks--; // now mutable
            if (en.ticks <= 0) {
                try { en.r.run(); } catch (Throwable ignored) {}
                TASKS.remove(i);
            }
        }
    }

    // simple mutable entry instead of record
    private static class Entry {
        int ticks;
        final Runnable r;
        Entry(int ticks, Runnable r) {
            this.ticks = ticks;
            this.r = r;
        }
    }
}
