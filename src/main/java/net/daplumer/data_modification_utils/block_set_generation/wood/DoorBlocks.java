package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.daplumer.data_modification_utils.block_set_generation.Shift;
import net.daplumer.data_modification_utils.mixin.FabricTagProviderAccessor;
import net.daplumer.data_modification_utils.mod_registries.ModDataRegisterer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import static net.daplumer.data_modification_utils.mod_registries.Registerer.registerBlockItem;
import static net.daplumer.data_modification_utils.mod_registries.registering_functions.BlocksKt.DOOR;
import static net.daplumer.data_modification_utils.mod_registries.registering_functions.BlocksKt.TRAPDOOR;
public class DoorBlocks {
    private final DoorBlock door;
    private final TrapdoorBlock trapdoor;
    public final BlockItem doorItem;
    public final BlockItem trapdoorItem;

    private DoorBlocks(DoorBlock door, TrapdoorBlock trapdoor) {
        this.door = door;
        this.trapdoor = trapdoor;
        this.doorItem = registerBlockItem(door);
        this.trapdoorItem = registerBlockItem(trapdoor);
    }
    public void addToItemGroups(FabricItemGroupEntries entries, Shift shift, ItemConvertible item){
        if (shift == Shift.BEFORE) {
            entries.addBefore(item, doorItem);
        } else {
            entries.addAfter(item, doorItem);
        }
        entries.addAfter(doorItem, trapdoorItem);
    }
    public void registerItemTags(FabricTagProvider.ItemTagProvider tagProvider){
        ((FabricTagProviderAccessor) tagProvider).builder(ItemTags.WOODEN_DOORS).add(doorItem);
        ((FabricTagProviderAccessor) tagProvider).builder(ItemTags.WOODEN_TRAPDOORS).add(trapdoorItem);
    }
    public void registerBlockTags(FabricTagProvider.BlockTagProvider tagProvider){
        ((FabricTagProviderAccessor) tagProvider).builder(BlockTags.WOODEN_DOORS).add(door);
        ((FabricTagProviderAccessor) tagProvider).builder(BlockTags.WOODEN_TRAPDOORS).add(trapdoor);
    }
    public static DoorBlocks of(Block door, Block trapdoor){
        return new DoorBlocks((DoorBlock) door,(TrapdoorBlock) trapdoor);
    }
    private static AbstractBlock.Settings createSettings(boolean burnable, MapColor lightColor){
        AbstractBlock.Settings settings = AbstractBlock.Settings.create()
                .strength(3.0F)
                .instrument(NoteBlockInstrument.BASS)
                .nonOpaque()
                .mapColor(lightColor);
        return burnable?settings.burnable():settings;
    }

    public static DoorBlocks of(ModDataRegisterer<Block,AbstractBlock.Settings, ?> BLOCKS, MapColor lightColor, WoodType woodType, boolean burnable){
        Block door = BLOCKS.register(woodType.name() + "_door",
            DOOR(woodType.setType()),
            createSettings(burnable, lightColor)
                    .pistonBehavior(PistonBehavior.DESTROY)
        );
        Block trapdoor = BLOCKS.register(woodType.name() + "_trapdoor",
                TRAPDOOR(woodType.setType()),
                createSettings(burnable, lightColor)
                        .allowsSpawning(Blocks::never)
                );
        return DoorBlocks.of(door,trapdoor);
    }

    public TrapdoorBlock trapdoor() {
        return trapdoor;
    }

    public DoorBlock door() {
        return door;
    }
}
