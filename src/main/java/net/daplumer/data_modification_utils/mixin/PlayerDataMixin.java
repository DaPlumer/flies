package net.daplumer.data_modification_utils.mixin;

import net.daplumer.data_modification_utils.PlayerData.PlayerDataCallbacks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerDataMixin {
    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    void readData(NbtCompound nbt, CallbackInfo ci){
        PlayerDataCallbacks.LOAD_DATA.getCallbacks().forEach(nbtComponentConsumer -> nbtComponentConsumer.accept(nbt, (PlayerEntity) (Object) this));
    }
    @Inject(method = "writeCustomDataToNbt",at = @At("TAIL"))
    void writeData(NbtCompound nbt, CallbackInfo ci){
        PlayerDataCallbacks.STORE_DATA.getCallbacks().forEach(nbtComponentConsumer -> nbtComponentConsumer.accept(nbt,(PlayerEntity) (Object) this));
    }
}
