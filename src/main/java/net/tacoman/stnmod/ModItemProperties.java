package net.tacoman.stnmod;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tacoman.stnmod.init.ItemRegistry;

public class ModItemProperties {

    public static void register() {
        registerBow(ItemRegistry.LONGBOW.get());
    }

    private static void registerBow(Item longbow) {
        ItemProperties.register(longbow, new ResourceLocation("pull"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    } else {
                        return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    }
                }
        );

        ItemProperties.register(longbow, new ResourceLocation("pulling"),
                (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
        );
    }
}
