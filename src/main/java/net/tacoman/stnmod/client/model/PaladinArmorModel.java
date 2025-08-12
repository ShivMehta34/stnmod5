package net.tacoman.stnmod.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition; // correct package on 1.20.1
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Paladin armor as a HumanoidModel (no GeckoLib).
 * Texture size: 128x128    assets/stnmod/textures/models/armor/paladin_armor.png
 */
public class PaladinArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation("stnmod", "paladin_armor"), "main");

    public PaladinArmorModel(ModelPart root) {
        super(root);
    }

    /** Register via EntityRenderersEvent.RegisterLayerDefinitions (see ClientModels). */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Humanoid names required by the renderer:
        // head, hat, body, right_arm, left_arm, right_leg, left_leg

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-4.5F, -5.5F, -3.5F, 9.0F, 6.0F, 8.0F, CubeDeformation.NONE)
                        .texOffs(64, 32).addBox(-5.0F, -5.5F, -5.0F, 10.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(0, 42).addBox(-1.5F, -9.0F, -5.0F, 3.0F, 4.0F, 10.0F, CubeDeformation.NONE)
                        .texOffs(50, 75).addBox(4.0F, -9.0F, -2.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(50, 75).addBox(-5.0F, -9.0F, -2.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(76, 42).addBox(4.0F, -13.0F, 0.5F, 1.0F, 4.0F, 3.0F, CubeDeformation.NONE)
                        .texOffs(76, 42).addBox(-5.0F, -13.0F, 0.5F, 1.0F, 4.0F, 3.0F, CubeDeformation.NONE)
                        .texOffs(32, 49).addBox(4.0F, -14.0F, 2.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(32, 49).addBox(-5.0F, -14.0F, 2.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(26, 42).addBox(4.5F, -11.0F, -1.0F, 1.0F, 3.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(26, 42).addBox(-5.5F, -11.0F, -1.0F, 1.0F, 3.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(60, 76).addBox(-1.5F, -5.0F, 4.0F, 3.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(26, 52).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 10.0F, CubeDeformation.NONE)
                        .texOffs(76, 35).addBox(-2.0F, -3.0F, -5.0F, 1.0F, 4.0F, 3.0F, CubeDeformation.NONE)
                        .texOffs(76, 35).addBox(1.0F, -3.0F, -5.0F, 1.0F, 4.0F, 3.0F, CubeDeformation.NONE)
                        .texOffs(38, 63).addBox(-5.0F, -3.5F, -5.0F, 4.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(38, 63).addBox(1.0F, -3.5F, -5.0F, 4.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(26, 49).addBox(4.0F, -4.5F, -5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(26, 49).addBox(-5.0F, -4.5F, -5.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(68, 76).addBox(1.5F, -2.5F, -4.5F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(76, 76).addBox(-4.5F, -2.5F, -4.5F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                        .texOffs(0, 30).addBox(-4.5F, -8.5F, -4.5F, 9.0F, 3.0F, 9.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO); // keep empty hat

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(34, 0).addBox(-4.5F, -1.5F, -2.5F, 9.0F, 13.0F, 5.0F, CubeDeformation.NONE)
                        .texOffs(38, 67).addBox(-3.0F, 10.0F, -3.0F, 6.0F, 2.0F, 6.0F, CubeDeformation.NONE)
                        .texOffs(36, 18).addBox(-4.0F, -2.0F, -3.0F, 8.0F, 12.0F, 6.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(52, 52).addBox(-3.5F, -2.5F, -2.5F, 5.0F, 10.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(38, 75).addBox(-2.0F, 0.5F, -2.0F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-2.75F, -3.25F, 0.0F, 0.0F, 0.0F, 0.2618F));

        rightArm.addOrReplaceChild("cube_r2",
                CubeListBuilder.create()
                        .texOffs(72, 52).addBox(-2.0F, 1.5F, -4.0F, 2.0F, 4.0F, 6.0F, CubeDeformation.NONE)
                        .texOffs(62, 67).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-2.5F, -2.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        rightArm.addOrReplaceChild("cube_r3",
                CubeListBuilder.create()
                        .texOffs(72, 62).addBox(-1.0F, -2.5F, -2.0F, 3.0F, 1.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-2.75F, -1.75F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("cube_r4",
                CubeListBuilder.create()
                        .texOffs(38, 75).addBox(-2.4672F, -2.1947F, -2.0F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(72, 52).addBox(-1.9022F, -0.052F, -3.0F, 2.0F, 4.0F, 6.0F, CubeDeformation.NONE)
                        .texOffs(72, 62).addBox(-1.0789F, -3.7458F, -2.0F, 3.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                        .texOffs(62, 67).addBox(-1.9022F, -3.052F, -3.0F, 4.0F, 3.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(3.0038F, -0.5262F, 0.0F, 3.1416F, 0.0F, 2.8798F));

        leftArm.addOrReplaceChild("cube_r5",
                CubeListBuilder.create()
                        .texOffs(52, 52).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 10.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(1.0F, 2.5F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 67).addBox(-2.75F, -0.75F, -2.5F, 5.0F, 13.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        leftLeg.addOrReplaceChild("cube_r6",
                CubeListBuilder.create()
                        .texOffs(0, 67).addBox(-2.5F, -6.5F, -2.5F, 5.0F, 13.0F, 5.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.2F, 5.75F, 0.0F, 0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }
}
