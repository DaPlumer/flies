package net.daplumer.data_modification_utils.mixin;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FabricTagProvider.class)
public interface FabricTagProviderAccessor {
    @Invoker("getOrCreateTagBuilder")
    <T> FabricTagProvider<T>.FabricTagBuilder builder(TagKey<T> key);
}
