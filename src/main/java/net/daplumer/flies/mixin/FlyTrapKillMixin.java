package net.daplumer.flies.mixin;

import net.daplumer.flies.Flies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class FlyTrapKillMixin extends Entity {
    public FlyTrapKillMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    void aVoid(DamageSource damageSource, CallbackInfo ci){
        if(damageSource.isOf(Flies.FLY_TRAP_DIGEST)) this.setVelocity(0,0,0);
    }

}
