package net.daplumer.flies.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.daplumer.flies.Flies;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ShearsItem.class)
public abstract class ShearsMixin {
    @ModifyReturnValue(method = "createToolComponent", at = @At("RETURN"))
    private static ToolComponent aVoid(ToolComponent original){
        try{
            original.rules().add(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Flies.FLY_TRAP_MAW.getRegistryEntry()), 15.0F));
            return original;
        } catch (UnsupportedOperationException e){
            List<ToolComponent.Rule> rules = new ArrayList<>(original.rules());
            rules.add(ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Flies.FLY_TRAP_MAW.getRegistryEntry()), 15.0F));
            return new ToolComponent(rules,original.defaultMiningSpeed(),original.damagePerBlock(),original.canDestroyBlocksInCreative());
        }
    }
}
