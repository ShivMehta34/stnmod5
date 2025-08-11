package net.tacoman.stnmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.network.NightwingGlideToggleC2SPacket;
import net.tacoman.stnmod.network.NetworkHandler;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightwingGlideClientController {

    private static boolean prevJumpDown = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;

        // TEMP: disable Nightwing glide client->server toggles
        if (true) return;  // <-- remove later to re-enable

        var mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // ...rest of your glide toggle code...


    boolean jumpDown = mc.options.keyJump.isDown();

        // Start glide when Space goes down while airborne
        if (jumpDown && !prevJumpDown) {
            if (!player.onGround() && !player.isInWater() && !player.isPassenger()) {
                NetworkHandler.CHANNEL.sendToServer(new NightwingGlideToggleC2SPacket(true));
            }
        }

        // Stop glide when Space released
        if (!jumpDown && prevJumpDown) {
            NetworkHandler.CHANNEL.sendToServer(new NightwingGlideToggleC2SPacket(false));
        }

        // If we land, make sure server stops glide (server also auto-stops, so this is optional)
        if (player.onGround() || player.isInWater() || player.isPassenger()) {
            // optional: tell server to stop if you want instant cut
            // NetworkHandler.CHANNEL.sendToServer(new NightwingGlideToggleC2SPacket(false));
        }

        prevJumpDown = jumpDown;
    }
}
