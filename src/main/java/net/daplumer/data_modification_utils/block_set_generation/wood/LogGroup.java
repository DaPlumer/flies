package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.daplumer.data_modification_utils.block_set_generation.Shift;
import net.daplumer.data_modification_utils.mixin.FabricTagProviderAccessor;
import net.daplumer.data_modification_utils.mod_registries.ModDataRegisterer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.daplumer.data_modification_utils.mod_registries.Registerer.registerBlockItem;

public final class LogGroup {
    private final PillarBlock log;
    private final PillarBlock strippedLog;
    private final PillarBlock wood;
    private final PillarBlock strippedWood;
    public final BlockItem logItem;
    public final BlockItem strippedLogItem;
    public final BlockItem woodItem;
    public final BlockItem strippedWoodItem;
    private final Identifier identifier;
    private LogGroup(Identifier identifier, PillarBlock log, PillarBlock strippedLog, PillarBlock wood, PillarBlock strippedWood){
        this.identifier = identifier;
        this.log = log;
        this.strippedLog = strippedLog;
        this.wood = wood;
        this.strippedWood = strippedWood;
        logItem = registerBlockItem(log);
        strippedLogItem = registerBlockItem(strippedLog);
        woodItem = registerBlockItem(wood);
        strippedWoodItem = registerBlockItem(strippedWood);
        StrippableBlockRegistry.register(log, strippedLog);
        StrippableBlockRegistry.register(wood, strippedWood);
        if(log.getDefaultState().isBurnable()){
            FlammableBlockRegistry.getDefaultInstance().add(log, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(strippedLog, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(wood, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(strippedWood, 5, 5);
        }
    }
    public void addToItemGroups(FabricItemGroupEntries entries, Shift shift, ItemConvertible item){
        if (shift == Shift.BEFORE) {
            entries.addBefore(item, log);
        } else {
            entries.addAfter(item, log);
        }
        entries.addAfter(log, strippedLog);
        entries.addAfter(strippedLog, wood);
        entries.addAfter(wood, strippedWood);
    }
    public void registerItemTags(FabricTagProvider.ItemTagProvider tagProvider){
        final TagKey<Item> tag = getItemTag();
        ((FabricTagProviderAccessor) tagProvider).builder(tag).add(logItem, strippedLogItem, woodItem, strippedWoodItem);
        ((FabricTagProviderAccessor) tagProvider).builder(ItemTags.LOGS).addTag(tag);
        if(!log.getDefaultState().isBurnable()) ((FabricTagProviderAccessor) tagProvider).builder(ItemTags.NON_FLAMMABLE_WOOD).add(logItem, strippedLogItem, woodItem, strippedWoodItem);
        else ((FabricTagProviderAccessor) tagProvider).builder(ItemTags.LOGS_THAT_BURN).addTag(tag);
    }
    public void registerBlockTags(FabricTagProvider.BlockTagProvider tagProvider){
        final TagKey<Block> tag = getBlockTag();
        ((FabricTagProviderAccessor) tagProvider).builder(tag).add(log, strippedLog, wood, strippedWood);
        ((FabricTagProviderAccessor) tagProvider).builder(BlockTags.LOGS).addTag(tag);
        if (log.getDefaultState().isBurnable()) ((FabricTagProviderAccessor) tagProvider).builder(BlockTags.LOGS_THAT_BURN).addTag(tag);
    }

    public static LogGroup of(Identifier identifier, Block log, Block strippedLog, Block wood, Block strippedWood){
        return new LogGroup(identifier, (PillarBlock) log,(PillarBlock) strippedLog,(PillarBlock) wood,(PillarBlock) strippedWood);
    }
    public TagKey<Item>  getItemTag (){return TagKey.of(RegistryKeys.ITEM,  identifier);}
    public TagKey<Block> getBlockTag(){return TagKey.of(RegistryKeys.BLOCK, identifier);}

    public PillarBlock getLog() {return log;}
    public PillarBlock getWood() {return wood;}
    public PillarBlock getStrippedLog() {return strippedLog;}
    public PillarBlock getStrippedWood() {return strippedWood;}
    public Identifier  getIdentifier() {return identifier;}

    public static AbstractBlock.Settings createCustomSettings(boolean burnable, MapColor topColor, MapColor sideColor, BlockSoundGroup soundGroup){
        final AbstractBlock.Settings settings = AbstractBlock.Settings.create()
                .mapColor(state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sounds(soundGroup);
        return burnable?settings.burnable():settings;
    }

    public static LogGroup of(ModDataRegisterer<Block, AbstractBlock.Settings,?> BLOCKS, String name, MapColor lightColor, MapColor mediumColor,MapColor darkColor, Boolean burnable, BlockSoundGroup soundGroup){
        final Block log = BLOCKS.register(name + "_log",
                PillarBlock::new,
                createCustomSettings(burnable, lightColor, darkColor, soundGroup)
        );
        final Block wood = BLOCKS.register(name + "_wood",
                PillarBlock::new,
                createCustomSettings(burnable, darkColor, darkColor, soundGroup)
        );
        final Block strippedLog = BLOCKS.register("stripped_" +name + "_log",
                PillarBlock::new,
                createCustomSettings(burnable, lightColor, mediumColor, soundGroup)
        );
        final Block strippedWood = BLOCKS.register("stripped_" +name + "_wood",
                PillarBlock::new,
                createCustomSettings(burnable, mediumColor, mediumColor, soundGroup)
        );
        return LogGroup.of(Identifier.of(BLOCKS.getNamespace(), name + "_logs"),log,strippedLog,wood,strippedWood);
    }
}
