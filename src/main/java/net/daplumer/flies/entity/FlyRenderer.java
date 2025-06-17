package net.daplumer.flies.entity;

import net.daplumer.flies.Flies;
import net.daplumer.flies.FliesClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FlyRenderer extends LivingEntityRenderer<Fly,FlyRenderState, FlyModel> {
    public FlyRenderer(EntityRendererFactory.Context context) {
        super(context, new FlyModel(context.getPart(FliesClient.FLY_LAYER)), .4F);
    }


    @Override
    public Identifier getTexture(FlyRenderState state) {
        return Identifier.of(Flies.MOD_ID, "textures/entity/fly.png");
    }

    @Override
    protected @Nullable Text getDisplayName(Fly entity) {
        return entity.shouldRenderName()? super.getDisplayName(entity):null;
    }

    @Override
    public FlyRenderState createRenderState() {
        return new FlyRenderState();
    }

    @Override
    public void updateRenderState(Fly livingEntity, FlyRenderState livingEntityRenderState, float f) {
        super.updateRenderState(livingEntity, livingEntityRenderState, f);
        livingEntityRenderState.bodyYaw = MathHelper.lerpAngleDegrees(f, getDir(livingEntity.lastVelocity), getDir(livingEntity.getVelocity()));
    }
    float getDir(Vec3d velocity) {
        if(velocity == null) return 0F;
        return (float) (Math.atan2(-velocity.getX(), velocity.getZ()));
    }
}
