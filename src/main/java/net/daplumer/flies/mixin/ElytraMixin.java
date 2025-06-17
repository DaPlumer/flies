package net.daplumer.flies.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.daplumer.flies.Flies;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraFeatureRenderer.class)
public abstract class ElytraMixin {
    @ModifyReturnValue(method = "getTexture", at = @At("RETURN"))
    private static Identifier aVoid(Identifier original, BipedEntityRenderState state){
        if(state.equippedChestStack.isOf(Flies.FLY_WINGS)) return Identifier.of(Flies.MOD_ID,"textures/entity/equipment/wings/fly.png");
        return original;
    }
}
