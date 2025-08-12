package net.tacoman.stnmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.client.model.PaladinArmorModel;

import java.util.function.Consumer;

// ...imports unchanged...
@Mod.EventBusSubscriber(modid = "stnmod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientModels {
    private ClientModels() {}

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(PaladinArmorModel.LAYER_LOCATION, PaladinArmorModel::createBodyLayer);
    }

    public static void attachPaladinArmorClientExtensions(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            PaladinArmorModel<LivingEntity> cached;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (cached == null) {
                    ModelPart baked = Minecraft.getInstance().getEntityModels().bakeLayer(PaladinArmorModel.LAYER_LOCATION);
                    cached = new PaladinArmorModel<>(baked);
                }

                cached.head.visible     = (slot == EquipmentSlot.HEAD);
                cached.hat.visible      = false;
                cached.body.visible     = (slot == EquipmentSlot.CHEST);
                cached.rightArm.visible = (slot == EquipmentSlot.CHEST);
                cached.leftArm.visible  = (slot == EquipmentSlot.CHEST);
                cached.rightLeg.visible = (slot == EquipmentSlot.LEGS);
                cached.leftLeg.visible  = (slot == EquipmentSlot.LEGS);

                // copy pose from default model (no generics headaches)
                cached.head.copyFrom(defaultModel.head);
                cached.body.copyFrom(defaultModel.body);
                cached.rightArm.copyFrom(defaultModel.rightArm);
                cached.leftArm.copyFrom(defaultModel.leftArm);
                cached.rightLeg.copyFrom(defaultModel.rightLeg);
                cached.leftLeg.copyFrom(defaultModel.leftLeg);

                return cached;
            }
        });
    }
}
