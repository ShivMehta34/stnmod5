package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.UUID;
import java.util.function.Supplier;

public class ToggleMarksmanModePacket {
    public ToggleMarksmanModePacket() {
    }

    private static final UUID MARKSMAN_SPEED_MODIFIER_UUID = UUID.fromString("d1e5f8c1-9b2a-4b8c-8765-2f3b39c905df");


    public static void encode(ToggleMarksmanModePacket packet, FriendlyByteBuf buffer) {
    }

    public static ToggleMarksmanModePacket decode(FriendlyByteBuf buffer) {
        return new ToggleMarksmanModePacket();
    }

    public static void handle(ToggleMarksmanModePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null && player.hasEffect(PotionEffectRegistry.MARKSMAN_STRENGTH.get())) {
                String currentMode = ServerEventHandler.getMarksmanMode(player);
                String nextMode;

                // Remove Quickfire bonus if active
                if ("Quickfire".equals(currentMode)) {
                    AttributeInstance drawSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
                    if (drawSpeed != null && drawSpeed.getModifier(MARKSMAN_SPEED_MODIFIER_UUID) != null) {
                        drawSpeed.removeModifier(MARKSMAN_SPEED_MODIFIER_UUID);
                    }
                }

                // Cycle through modes
                switch (currentMode) {
                    case "Quickfire" -> nextMode = "Concentrate";
                    case "Concentrate" -> nextMode = "Penetrate";
                    case "Penetrate" -> nextMode = "Quickfire";
                    default -> nextMode = "Quickfire";
                }

                ServerEventHandler.setMarksmanMode(player, nextMode);
                player.sendSystemMessage(Component.literal("Marksman Mode switched to: " + nextMode));
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
