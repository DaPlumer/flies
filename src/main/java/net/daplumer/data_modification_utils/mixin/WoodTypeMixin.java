package net.daplumer.data_modification_utils.mixin;

import net.minecraft.block.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(WoodType.class)
public interface WoodTypeMixin {
    @Accessor("VALUES")
    static Map<String, WoodType> getValues(){
        throw new AssertionError();
    }
}
