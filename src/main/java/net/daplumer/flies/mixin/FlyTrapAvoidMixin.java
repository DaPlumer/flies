package net.daplumer.flies.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.daplumer.flies.Flies;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PathAwareEntity.class)
public abstract class FlyTrapAvoidMixin extends MobEntity{
    protected FlyTrapAvoidMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyReturnValue(method = "getPathfindingFavor(Lnet/minecraft/util/math/BlockPos;)F",at = @At("TAIL"))
    float aVoid(float original, BlockPos pos){
        return getWorld().getBlockState(pos).isOf(Flies.FLY_TRAP_MAW)?-16:original;
    }
}
