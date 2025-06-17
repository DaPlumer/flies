package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.daplumer.data_modification_utils.block_set_generation.Shift;
import net.daplumer.data_modification_utils.mixin.FabricTagProviderAccessor;
import net.daplumer.data_modification_utils.mod_registries.ModDataRegisterer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.BlockSoundGroup;

import static net.daplumer.data_modification_utils.mod_registries.Registerer.registerBlockItem;
import static net.daplumer.data_modification_utils.mod_registries.registering_functions.BlocksKt.*;
public final class PlankBasedBlocks {
    private final Block planks;
    private final StairsBlock stairs;
    private final SlabBlock slab;
    private final FenceBlock fence;
    private final FenceGateBlock fenceGate;
    private final PressurePlateBlock pressurePlate;
    private final ButtonBlock button;
    public final BlockItem planksItem;
    public final BlockItem stairsItem;
    public final BlockItem slabItem;
    public final BlockItem fenceItem;
    public final BlockItem fenceGateItem;
    public final BlockItem pressurePlateItem;
    public final BlockItem buttonItem;

    private final String name;
    private PlankBasedBlocks(Block planks, Block stairs, Block slab, Block fence, Block fenceGate, Block pressurePlate, Block button, String name) {
        this.planks = planks;
        this.stairs = ((StairsBlock) stairs);
        this.slab = ((SlabBlock) slab);
        this.fence = ((FenceBlock) fence);
        this.fenceGate = ((FenceGateBlock) fenceGate);
        this.pressurePlate = ((PressurePlateBlock) pressurePlate);
        this.button = ((ButtonBlock) button);
        this.name = name;
        this.planksItem = registerBlockItem(planks);
        this.stairsItem = registerBlockItem(stairs);
        this.slabItem = registerBlockItem(slab);
        this.fenceItem = registerBlockItem(fence);
        this.fenceGateItem = registerBlockItem(fenceGate);
        this.pressurePlateItem = registerBlockItem(pressurePlate);
        this.buttonItem = registerBlockItem(button);

        if(planks.getDefaultState().isBurnable()){
            final FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();
            instance.add(planks, 5, 20);
            instance.add(slab, 5, 20);
            instance.add(stairs, 5, 20);
            instance.add(fence, 5, 20);
            instance.add(fenceGate, 5, 20);
        }
    }
    public static PlankBasedBlocks of(Block planks, Block stairs, Block slab, Block fence, Block fenceGate, Block pressurePlate, Block button, String name){
        return new PlankBasedBlocks(planks, stairs, slab, fence, fenceGate, pressurePlate, button, name);
    }
    public Block planks() {return planks;}
    public StairsBlock stairs() {return stairs;}
    public SlabBlock slab() {return slab;}
    public FenceBlock fence() {return fence;}
    public FenceGateBlock fenceGate() {return fenceGate;}
    public PressurePlateBlock pressurePlate() {return pressurePlate;}
    public ButtonBlock button() {return button;}
    public String name() {return name;}
    public void addToItemGroups(FabricItemGroupEntries entries, Shift shift, ItemConvertible item) {
        if (shift == Shift.BEFORE) {
            entries.addBefore(item, planks);
        } else {
            entries.addAfter(item, planks);
        }
        entries.addAfter(planks, stairs);
        entries.addAfter(stairs, slab);
        entries.addAfter(slab, fence);
        entries.addAfter(fence, fenceGate);
        entries.addAfter(fenceGate, pressurePlate);
        entries.addAfter(pressurePlate, button);
    }
    public void registerItemTags(FabricTagProvider.ItemTagProvider tagProvider){
        final FabricTagProviderAccessor accessor = (FabricTagProviderAccessor) tagProvider;
        accessor.builder(ItemTags.PLANKS).add(planksItem);
        accessor.builder(ItemTags.WOODEN_FENCES).add(fenceItem);
        accessor.builder(ItemTags.WOODEN_PRESSURE_PLATES).add(pressurePlateItem);
        accessor.builder(ItemTags.WOODEN_BUTTONS).add(buttonItem);
        accessor.builder(ItemTags.WOODEN_SLABS).add(slabItem);
        accessor.builder(ItemTags.WOODEN_STAIRS).add(stairsItem);
        accessor.builder(ItemTags.FENCE_GATES).add(fenceGateItem);
        if (!planks.getDefaultState().isBurnable()) {accessor.builder(ItemTags.NON_FLAMMABLE_WOOD)
                .add(planksItem, fenceItem, pressurePlateItem, buttonItem, slabItem, stairsItem, fenceGateItem);}
    }
    public void registerBlockTags(FabricTagProvider.BlockTagProvider tagProvider){
        final FabricTagProviderAccessor accessor = (FabricTagProviderAccessor) tagProvider;
        accessor.builder(BlockTags.PLANKS).add(planks);
        accessor.builder(BlockTags.FENCE_GATES).add(fenceGate);
        accessor.builder(BlockTags.WOODEN_STAIRS).add(stairs);
        accessor.builder(BlockTags.WOODEN_SLABS).add(slab);
        accessor.builder(BlockTags.WOODEN_BUTTONS).add(button);
        accessor.builder(BlockTags.WOODEN_FENCES).add(fence);
        accessor.builder(BlockTags.WOODEN_PRESSURE_PLATES).add(pressurePlate);
    }

    private static AbstractBlock.Settings createFromBurnable(boolean burnable, BlockSoundGroup soundGroup, MapColor color){
        return burnable?
                AbstractBlock.Settings.create().instrument(NoteBlockInstrument.BASS).sounds(soundGroup).mapColor(color).burnable():
                AbstractBlock.Settings.create().instrument(NoteBlockInstrument.BASS).sounds(soundGroup).mapColor(color);
    }

    public static PlankBasedBlocks of(ModDataRegisterer<Block,AbstractBlock.Settings, ?> BLOCKS, MapColor lightColor, Boolean burnable, BlockSoundGroup soundGroup, WoodType woodType){
        final Block planks = BLOCKS.register(woodType.name() + "_planks",
                createFromBurnable(burnable, soundGroup,lightColor)
                        .strength(2.0F, 3.0F)
                        .mapColor(lightColor)
        );
        final Block stairs = BLOCKS.register(woodType.name() + "_stairs",
                STAIRS(planks.getDefaultState()),
                AbstractBlock.Settings.copy(planks)
        );
        final Block slab = BLOCKS.register(woodType.name() + "_slab",
                createFromBurnable(burnable, soundGroup, lightColor)
                        .mapColor(lightColor)
                        .strength(2.0F, 3.0F),
                SlabBlock::new
        );
        final Block fence = BLOCKS.register(woodType.name() + "_fence",
                createFromBurnable(burnable, soundGroup, lightColor)
                        .mapColor(lightColor)
                        .strength(2.0F,3.0F),
                FenceBlock::new
        );
        final Block fenceGate = BLOCKS.register(woodType.name() + "_fence_gate",
                createFromBurnable(burnable, soundGroup, lightColor)
                        .mapColor(lightColor)
                        .solid()
                        .strength(2.0F,3.0F),
                FENCE_GATE(woodType)
        );
        final Block pressurePlate = BLOCKS.register(woodType.name() + "_pressure_plate",
                createFromBurnable(burnable, soundGroup, lightColor)
                        .solid()
                        .noCollision()
                        .strength(0.5F)
                        .pistonBehavior(PistonBehavior.DESTROY),
                PRESSURE_PLATE(woodType.setType())
        );
        final Block buttonBlock = BLOCKS.register(woodType.name() + "_button",
                Blocks.createButtonSettings(),
                BUTTON(woodType.setType(), 30)
        );
        return PlankBasedBlocks.of(planks, stairs, slab, fence, fenceGate, pressurePlate, buttonBlock, woodType.name());
    }
}
