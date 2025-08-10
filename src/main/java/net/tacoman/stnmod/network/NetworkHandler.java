package net.tacoman.stnmod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.tacoman.stnmod.stnmod;

import java.util.Optional;
import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(stnmod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SummonClonesPacket.class, SummonClonesPacket::encode, SummonClonesPacket::decode, SummonClonesPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, ApplyEffectPacket.class, ApplyEffectPacket::toBytes, ApplyEffectPacket::new, ApplyEffectPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, LegShotPacket.class, LegShotPacket::toBytes, LegShotPacket::new, LegShotPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, ArrowRainPacket.class, ArrowRainPacket::encode, ArrowRainPacket::decode, ArrowRainPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, TriggerElementalRangerPacket.class, TriggerElementalRangerPacket::encode, TriggerElementalRangerPacket::decode, TriggerElementalRangerPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, TriggerEarthArrowPacket.class, TriggerEarthArrowPacket::encode, TriggerEarthArrowPacket::decode, TriggerEarthArrowPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, StartWhirlwindPacket.class, (msg, buf) -> msg.toBytes(buf), StartWhirlwindPacket::new, (msg, ctx) -> msg.handle(ctx), Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, WhirlwindStartedPacket.class, (msg, buf) -> msg.toBytes(buf), WhirlwindStartedPacket::new, (msg, ctx) -> msg.handle(ctx));
        CHANNEL.registerMessage(id++, StartKnightDashPacket.class, StartKnightDashPacket::toBytes, StartKnightDashPacket::new, (msg, ctx) -> msg.handle(ctx));
        CHANNEL.registerMessage(id++, ArmShieldBashPacket.class, ArmShieldBashPacket::toBytes, ArmShieldBashPacket::new, (msg, ctx) -> msg.handle(ctx));

    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
