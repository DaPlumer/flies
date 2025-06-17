package net.daplumer.flies.entity;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.util.math.MathHelper;

import static org.joml.Math.floor;

public class FlyModel extends EntityModel<FlyRenderState> {
	public static final ModelTransformer TRANSFORMER = ModelTransformer.scaling(0.5F);
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private final ModelPart root;
	private final ModelPart backlegs;
	private final ModelPart leftwing;
	private final ModelPart rghtwing;
	private final ModelPart frontlegs;
	private final ModelPart midlegs;

	public FlyModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
		this.backlegs = this.root.getChild("backlegs");
		this.leftwing = this.root.getChild("leftwing");
		this.rghtwing = this.root.getChild("rghtwing");
		this.frontlegs = this.root.getChild("frontlegs");
		this.midlegs = this.root.getChild("midlegs");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData partdefinition = modelData.getRoot();

		ModelPartData root = partdefinition.addChild("root", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -4.0F, 5.0F, 5.0F, 8.0F),
		//.uv(0, 25).cuboid(-2.5F, -4.0F, -1.0F, 2.0F, 3.0F, 1.0F)
		//.uv(6, 25).cuboid(0.5F, -4.0F, -1.0F, 2.0F, 3.0F, 1.0F),
				ModelTransform.origin(0.0F, 23.0F, 0.0F)
		);

		ModelPartData backlegs  = root.addChild("backlegs",  ModelPartBuilder.create().uv(26, 0 ).cuboid(-2.0F, -3.0F, -3.0F, 4.0F, 1.0F, 0.0F), ModelTransform.origin(0.0F, 0.0F, 6.0F));
		ModelPartData frontlegs = root.addChild("frontlegs", ModelPartBuilder.create().uv(12, 25).cuboid(-2.0F, -3.0F, -3.0F, 4.0F, 1.0F, 0.0F), ModelTransform.origin(0.0F, 0.0F, 2.0F));
		ModelPartData midlegs   = root.addChild("midlegs",   ModelPartBuilder.create().uv(20, 25).cuboid(-2.0F, -3.0F, -3.0F, 4.0F, 1.0F, 0.0F), ModelTransform.origin(0.0F, 0.0F, 4.0F));

		ModelPartData leftwing = root.addChild("leftwing", ModelPartBuilder.create(), ModelTransform.origin(-1.5F, -5.0F, 1.0F));
		ModelPartData rghtwing = root.addChild("rghtwing", ModelPartBuilder.create(), ModelTransform.origin(1.5F, -5.0F, 1.0F));

		ModelPartData cube_r1 = leftwing.addChild("cube_r1", ModelPartBuilder.create().uv(42, 0)           .cuboid(-7.0F, 0.0F, -4.0F, 7.0F, 0.0F, 8.0F), ModelTransform.rotation(0.0439F, 0.0421F, 0.0113F));
		ModelPartData cube_r2 = rghtwing.addChild("cube_r2", ModelPartBuilder.create().uv(42, 0).mirrored().cuboid(0.0F, 0.0F, -4.0F, 7.0F, 0.0F, 8.0F).mirrored(false), ModelTransform.rotation(0.0439F, -0.0421F, -0.0113F));





		return TexturedModelData.of(modelData, 64, 64).transform(TRANSFORMER);
	}

    @Override
	public void setAngles(FlyRenderState state) {
		super.setAngles(state);
		float f = state.age * 120.32113F * (float) (Math.PI / 180.0);
		this.root.pitch = MathHelper.cos(state.age * 0.18F) * 0.25F;
		this.root.yaw = state.bodyYaw;
		this.rghtwing.yaw = 0.0F;
		this.rghtwing.roll = MathHelper.cos(f) * (float) Math.PI * 0.15F;
		this.leftwing.pitch = this.rghtwing.pitch;
		this.leftwing.yaw = this.rghtwing.yaw;
		this.leftwing.roll = -this.rghtwing.roll;
		this.frontlegs.pitch = (float) (Math.PI / 4);
		this.midlegs.pitch = (float) (Math.PI / 4);
		this.backlegs.pitch = (float) (Math.PI / 4);
	}
}