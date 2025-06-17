package net.daplumer.data_modification_utils.mixin;

import net.minecraft.block.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(BlockSetType.class)
public interface BlockSetTypeMixin {
    @Accessor("VALUES")
    static Map<String,BlockSetType> getValues(){
        throw new AssertionError();
    }
}
