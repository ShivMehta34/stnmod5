package net.tacoman.stnmod.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.tacoman.stnmod.client.model.NinjaCloneModel;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.stnmod;

public class NinjaCloneRenderer extends MobRenderer<NinjaCloneEntity, NinjaCloneModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(stnmod.MODID, "textures/entity/ninja_clone.png");

    public NinjaCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new NinjaCloneModel(context.bakeLayer(NinjaCloneModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(NinjaCloneEntity entity) {
        return TEXTURE;
    }
}
