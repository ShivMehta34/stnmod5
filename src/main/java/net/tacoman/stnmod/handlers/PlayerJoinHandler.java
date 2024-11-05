package net.tacoman.stnmod.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.utils.WelcomeBook;

@Mod.EventBusSubscriber(modid = "stnmod")
public class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack welcomeBook = WelcomeBook.createWelcomeBook();
            player.getInventory().add(welcomeBook);
        }
    }
}
