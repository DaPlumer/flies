package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.daplumer.data_modification_utils.mixin.BlockSetTypeMixin;
import net.daplumer.data_modification_utils.mixin.WoodTypeMixin;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;

public class WoodTypeCreator {
    public static WoodType createWoodType(String name, WoodTypeSounds sounds){
        final BlockSetType set = new BlockSetType(name, true,true,true, BlockSetType.ActivationRule.EVERYTHING,
                sounds.defaultSounds(),
                sounds.doorClose(),
                sounds.doorOpen(),
                sounds.trapdoorClose(),
                sounds.trapdoorOpen(),
                sounds.pressurePlateClick(),
                sounds.pressurePlateClickOff(),
                sounds.buttonClick(),
                sounds.buttonClickOff());
        BlockSetTypeMixin.getValues().put(name,set);
        final WoodType woodType = new WoodType(name,set,
                sounds.defaultSounds(),
                sounds.hangingSounds(),
                sounds.fenceGateClose(),
                sounds.fenceGateOpen()
        );
        WoodTypeMixin.getValues().put(name,woodType);
        return woodType;
    }
}
