package net.daplumer.data_modification_utils.block_set_generation.wood;

import net.daplumer.data_modification_utils.block_set_generation.Shift;
import net.daplumer.data_modification_utils.mixin.RecipeGeneratorExporterAccessor;
import net.daplumer.data_modification_utils.mod_registries.ModDataRegisterer;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.WoodType;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

import static net.daplumer.data_modification_utils.block_set_generation.ModelGenerationKt.generateSlab;
import static net.daplumer.data_modification_utils.block_set_generation.ModelGenerationKt.generateStairs;

@SuppressWarnings("unused")
public class WoodBlockGroup {
    private static final Logger log = LoggerFactory.getLogger(WoodBlockGroup.class);
    private final String name;
    private final DoorBlocks doors;
    private final LogGroup logs;
    private final PlankBasedBlocks planks;
    private final String namespace;

    private WoodBlockGroup(String name, DoorBlocks doors, LogGroup logs, PlankBasedBlocks planks, String namespace) {
        this.name = name;
        this.doors = doors;
        this.logs = logs;
        this.planks = planks;
        this.namespace = namespace;
    }
    public DoorBlocks getDoors() {return doors;}
    public LogGroup getLogs() {return logs;}
    public PlankBasedBlocks getPlanks() {return planks;}
    public void registerBlockTags(FabricTagProvider.BlockTagProvider blockTagProvider) {
        doors.registerBlockTags(blockTagProvider);
        logs.registerBlockTags(blockTagProvider);
        planks.registerBlockTags(blockTagProvider);
    }
    public void registerItemTags(FabricTagProvider.ItemTagProvider itemTagProvider) {
        doors.registerItemTags(itemTagProvider);
        logs.registerItemTags(itemTagProvider);
        planks.registerItemTags(itemTagProvider);
    }
    public static WoodBlockGroup of(DoorBlocks doors, LogGroup logs, PlankBasedBlocks planks, String name, String namespace) {
        return new WoodBlockGroup(name, doors,logs,planks,namespace);
    }

    public void addToItemGroups(FabricItemGroupEntries entries, Shift shift, ItemConvertible item) {
        logs.addToItemGroups(entries, shift, item);
        planks.addToItemGroups(entries, Shift.AFTER, logs.getStrippedWood());
        doors.addToItemGroups(entries, Shift.BEFORE, planks.pressurePlate());
    }
    public static WoodBlockGroup of(ModDataRegisterer<Block, AbstractBlock.Settings,?> BLOCKS, WoodType type, MapColor lightColor, MapColor mediumColor, MapColor darkColor, Boolean burnable, BlockSoundGroup soundGroup){
        return WoodBlockGroup.of(
                DoorBlocks.of(BLOCKS,lightColor,type,burnable),
                LogGroup.of(BLOCKS,type.name(),lightColor,mediumColor,darkColor,burnable,type.soundType()),
                PlankBasedBlocks.of(BLOCKS,lightColor,burnable,type.soundType(),type),type.name(),
        BLOCKS.getNamespace());
    }
    public static WoodBlockGroup of(ModDataRegisterer<Block ,AbstractBlock.Settings, ?> BLOCKS, String name, Boolean burnable,
                                    MapColor lightColor, MapColor mediumColor, MapColor darkColor, WoodTypeSounds soundType){
        return WoodBlockGroup.of(BLOCKS, WoodTypeCreator.createWoodType(name,soundType), lightColor,darkColor,mediumColor,burnable, soundType.defaultSounds());
    }




    public void generateModels(BlockStateModelGenerator modelGenerator){
        //door blocks first
        modelGenerator.registerDoor(doors.door());
        modelGenerator.registerOrientableTrapdoor(doors.trapdoor());
        // now log models
        makeLogModel(TextureMap.sideEnd(suffixedIdentifier("_log"), suffixedIdentifier("_log_top")),modelGenerator,logs.getLog());//log
        makeLogModel(TextureMap.sideEnd(strippedSuffixedIdentifier("_log"), strippedSuffixedIdentifier("_log_top")),modelGenerator,logs.getStrippedLog());
        makeLogModel(TextureMap.all(suffixedIdentifier("_log")),modelGenerator,logs.getWood());
        makeLogModel(TextureMap.all(strippedSuffixedIdentifier("_log")),modelGenerator,logs.getStrippedWood());
        // plank based models

        final TextureMap planksTexture = TextureMap.all(suffixedIdentifier("_planks"));
        modelGenerator.registerSimpleCubeAll(planks.planks());
        generateStairs(modelGenerator, planks.stairs(), planksTexture);
        generateSlab(modelGenerator, planks.slab(),planksTexture);
        final BiConsumer<Identifier, ModelSupplier> col = modelGenerator.modelCollector;
        final Identifier openFenceGate = Models.TEMPLATE_FENCE_GATE_OPEN.upload(planks.fenceGate(), planksTexture, col);
        final Identifier openFenceGateWall = Models.TEMPLATE_FENCE_GATE_WALL_OPEN.upload(planks.fenceGate(), planksTexture, col);
        final Identifier FenceGate = Models.TEMPLATE_FENCE_GATE.upload(planks.fenceGate(), planksTexture, col);
        final Identifier FenceGateWall = Models.TEMPLATE_FENCE_GATE_WALL.upload(planks.fenceGate(), planksTexture, col);
        modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createFenceGateBlockState(planks.fenceGate(),
                BlockStateModelGenerator.createWeightedVariant(openFenceGate),
                BlockStateModelGenerator.createWeightedVariant(FenceGate),
                BlockStateModelGenerator.createWeightedVariant(openFenceGateWall),
                BlockStateModelGenerator.createWeightedVariant(FenceGateWall),
                true)
        );
        modelGenerator.registerParentedItemModel(planks.fenceGate(),FenceGate);
        final Identifier fencePost = Models.FENCE_POST.upload(planks.fence(), planksTexture, col);
        final Identifier fenceSide = Models.FENCE_SIDE.upload(planks.fence(), planksTexture, col);
        final Identifier fenceInventory = Models.FENCE_INVENTORY.upload(planks.fence(), planksTexture, col);
        modelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createFenceBlockState(planks.fence(),
                        BlockStateModelGenerator.createWeightedVariant(fencePost),
                        BlockStateModelGenerator.createWeightedVariant(fenceSide))
        );
        modelGenerator.registerParentedItemModel(planks.fence(),fenceInventory);
        final Identifier plate = Models.PRESSURE_PLATE_UP.upload(planks.pressurePlate(), planksTexture, col);
        final Identifier plateDown = Models.PRESSURE_PLATE_DOWN.upload(planks.pressurePlate(), planksTexture, col);
        modelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createPressurePlateBlockState(planks.pressurePlate(),
                        BlockStateModelGenerator.createWeightedVariant(plate),
                        BlockStateModelGenerator.createWeightedVariant(plateDown))
        );
        modelGenerator.registerParentedItemModel(planks.pressurePlate(),plate);
        final Identifier button = Models.BUTTON.upload(planks.button(), planksTexture, col);
        final Identifier buttonPressed = Models.BUTTON_PRESSED.upload(planks.button(), planksTexture, col);
        final Identifier buttonInventory = Models.BUTTON_INVENTORY.upload(planks.button(), planksTexture, col);
        modelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createButtonBlockState(planks.button(),
                        BlockStateModelGenerator.createWeightedVariant(button),
                        BlockStateModelGenerator.createWeightedVariant(buttonPressed))
        );
        modelGenerator.registerParentedItemModel(planks.button(),buttonInventory);

    }
    private Identifier suffixedIdentifier(String suffix){
        return Identifier.of(this.namespace,"block/"+this.name+suffix);
    }
    public Identifier strippedSuffixedIdentifier(String suffix){
        return Identifier.of(this.namespace,"block/stripped_" + this.name + suffix);
    }

    private static void makeLogModel(TextureMap map, BlockStateModelGenerator modelGenerator, Block log){
        final Identifier logModel = Models.CUBE_COLUMN.upload(log, map,modelGenerator.modelCollector);
        modelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createAxisRotatedBlockState(log,
                        BlockStateModelGenerator.createWeightedVariant(logModel))
        );
        modelGenerator.registerParentedItemModel(log, logModel);
    }



    public void registerCustomRecipes(RecipeGenerator generator){
        RecipeExporter exporter = ((RecipeGeneratorExporterAccessor)(generator)).exporter();
        Ingredient plank = Ingredient.ofItem(planks.planks());
        generator.offerBarkBlockRecipe(logs.getStrippedLog(), logs.getLog());
        generator.offerBarkBlockRecipe(logs.getStrippedWood(), logs.getWood());
        generator.createPressurePlateRecipe(RecipeCategory.REDSTONE, planks.pressurePlate(), plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createButtonRecipe(planks.button(),plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createDoorRecipe(doors.door(),plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createTrapdoorRecipe(doors.trapdoor(), plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createStairsRecipe(planks.stairs(),plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createSlabRecipe(RecipeCategory.MISC, planks.slab(), plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createFenceRecipe(planks.fence(), plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.createFenceGateRecipe(planks.fenceGate(), plank).criterion("get_planks",
                InventoryChangedCriterion.Conditions.items(planks.planks())).offerTo(exporter);
        generator.offerPlanksRecipe(planks.planks(), logs.getItemTag(), 4);
    }


}
