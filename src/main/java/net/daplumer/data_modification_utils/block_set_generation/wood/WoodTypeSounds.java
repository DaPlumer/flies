package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;

@SuppressWarnings("unused")
public record WoodTypeSounds(BlockSoundGroup defaultSounds, BlockSoundGroup hangingSounds,
                             SoundEvent doorClose, SoundEvent doorOpen,
                             SoundEvent trapdoorClose, SoundEvent trapdoorOpen,
                             SoundEvent fenceGateClose, SoundEvent fenceGateOpen,
                             SoundEvent buttonClick, SoundEvent buttonClickOff,
                             SoundEvent pressurePlateClick, SoundEvent pressurePlateClickOff) {
    public static WoodTypeSounds copyOf(WoodTypeSounds sounds) {
        return new WoodTypeSounds(sounds.defaultSounds, sounds.hangingSounds,
                sounds.doorClose, sounds.doorOpen,
                sounds.trapdoorClose, sounds.trapdoorOpen,
                sounds.fenceGateClose, sounds.fenceGateOpen,
                sounds.buttonClick, sounds.buttonClickOff,
                sounds.pressurePlateClick, sounds.pressurePlateClickOff);
    }
    public static WoodTypeSounds copyFromWoodType(WoodType woodType) {
        BlockSetType set = woodType.setType();
        return new WoodTypeSounds(woodType.soundType(),woodType.hangingSignSoundType(),
                set.doorClose(),            set.doorOpen(),
                set.trapdoorClose(),        set.trapdoorOpen(),
                woodType.fenceGateClose(),  woodType.fenceGateOpen(),
                set.buttonClickOn(),        set.buttonClickOff(),
                set.pressurePlateClickOn(), set.pressurePlateClickOff());
    }
    public static final WoodTypeSounds DEFAULT = copyFromWoodType(WoodType.OAK);
    public static final WoodTypeSounds BAMBOO = copyFromWoodType(WoodType.BAMBOO);
    public static final WoodTypeSounds NETHER = copyFromWoodType(WoodType.CRIMSON);
    public static final WoodTypeSounds CHERRY = copyFromWoodType(WoodType.CHERRY);
}
