package net.tacoman.stnmod.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

@Mod.EventBusSubscriber
public class VillagerLevelUpHandler {

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Villager villager) {
            Player player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
            if (player != null && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() &&
                    villager.getVillagerData().getProfession() == VillagerProfession.ARMORER &&
                    villager.getVillagerData().getLevel() == 5) {
                transformToBob(villager);
            }
        }
    }

    private static void transformToBob(Villager villager) {
        MinecraftServer server = villager.getServer();
        if (server != null) {
            String command = String.format(
                    "execute at @e[type=minecraft:villager,limit=1,sort=nearest,x=%.2f,y=%.2f,z=%.2f,distance=..1] run summon minecraft:villager ~ ~ ~ {VillagerData:{profession:\"stnmod:bob\",level:2},CustomName:\"\\\"BOB\\\"\"}",
                    villager.getX(), villager.getY(), villager.getZ()
            );
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
            villager.discard(); // Remove the old villager
        }
    }
}
