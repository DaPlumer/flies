package net.daplumer.data_modification_utils.block_set_generation.stone

import net.daplumer.data_modification_utils.block_set_generation.Shift
import net.daplumer.data_modification_utils.block_set_generation.generateSlab
import net.daplumer.data_modification_utils.block_set_generation.generateStairs
import net.daplumer.data_modification_utils.block_set_generation.generateWall
import net.daplumer.data_modification_utils.mixin.FabricTagProviderAccessor
import net.daplumer.data_modification_utils.mixin.RecipeGeneratorExporterAccessor
import net.daplumer.data_modification_utils.mod_registries.GeneralDataRegisterer
import net.daplumer.data_modification_utils.mod_registries.Registerer.registerBlockItem
import net.daplumer.data_modification_utils.mod_registries.Registerer.registerBlockItems
import net.daplumer.data_modification_utils.mod_registries.registering_functions.STAIRS
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.*
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.AbstractBlock.Settings.copyShallow
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models
import net.minecraft.client.data.TextureMap
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.stream.Stream

class KStoneSet(
    private val variants:Map<Variant, BlockSubSet>,
    private val setsWithoutDefault:ArrayList<BlockSubSet>,
    val name: String,
    private val defaultBlockSet: BlockSubSet
){
    @Suppress("DEPRECATION")
    class BlockSubSet(val defaultBlock:Block, val variant: Variant,val name: String, registerer: GeneralDataRegisterer<Block, Settings>, hasCrackedVariant: Boolean = false, hasChiseledVariant: Boolean = false) {
        val crackedBlock:Block? = if (hasCrackedVariant)
            registerer.register(
                "cracked_" + variant.createName(name, true),
                copyShallow(defaultBlock)
            ) else null
        val chiseledBlock:Block? = if (hasChiseledVariant)
            registerer.register(
                "chiseled_"+ variant.createName(name, true),
                copyShallow(defaultBlock)
            ) else null
        val stairsBlock:StairsBlock = registerer.register(
            variant.createName(name, false) + "_stairs",
            copyShallow(defaultBlock),
            STAIRS(defaultBlock.defaultState)
        )
        val slabBlock:SlabBlock = registerer.register(
            variant.createName(name, false) + "_slab",
            copyShallow(defaultBlock)
        ) { SlabBlock(it) }
        val wallBlock:WallBlock = registerer.register(
            variant.createName(name, false) + "_wall",
            copyShallow(defaultBlock)
        ) { WallBlock(it) }

        init {

            crackedBlock?.let { registerBlockItem(it) }
            chiseledBlock?.let { registerBlockItem(it) }
            registerBlockItems(defaultBlock, stairsBlock, slabBlock, wallBlock)
        }



        fun registerItemTags(tagProvider: FabricTagProvider.ItemTagProvider) {
            val accessor = tagProvider as FabricTagProviderAccessor
            accessor.builder(ItemTags.SLABS).add(slabBlock.asItem())
            accessor.builder(ItemTags.STAIRS).add(stairsBlock.asItem())
            accessor.builder(ItemTags.WALLS).add(wallBlock.asItem())
        }

        fun registerBlockTags(tagProvider: FabricTagProvider.BlockTagProvider) {
            val accessor = tagProvider as FabricTagProviderAccessor
            accessor.builder(BlockTags.PICKAXE_MINEABLE).add(defaultBlock, slabBlock, stairsBlock, wallBlock)
            accessor.builder(BlockTags.SLABS).add(slabBlock)
            accessor.builder(BlockTags.STAIRS).add(stairsBlock)
            accessor.builder(BlockTags.WALLS).add(wallBlock)
            crackedBlock?.let { accessor.builder(BlockTags.PICKAXE_MINEABLE).add(it) }
            chiseledBlock?.let { accessor.builder(BlockTags.PICKAXE_MINEABLE).add(it) }
        }

        fun list(): List<Block> {
            return listOfNotNull(defaultBlock, crackedBlock, chiseledBlock, stairsBlock, slabBlock, wallBlock)
        }

        fun registerRecipesFrom(otherBlock: Block?, generator: RecipeGenerator) {
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, slabBlock, otherBlock, 2)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, stairsBlock, otherBlock, 1)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, wallBlock, otherBlock, 1)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, defaultBlock, otherBlock, 1)
            chiseledBlock?.let{generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, it, otherBlock, 1)}

        }

        fun registerSpecificRecipes(generator: RecipeGenerator) {
            generator.offerSlabRecipe(RecipeCategory.BUILDING_BLOCKS, slabBlock, defaultBlock)
            generator.createStairsRecipe(stairsBlock, Ingredient.ofItem(defaultBlock))
                .criterion("get", InventoryChangedCriterion.Conditions.items(defaultBlock))
                .offerTo((generator as RecipeGeneratorExporterAccessor).exporter())
            generator.offerWallRecipe(RecipeCategory.BUILDING_BLOCKS, wallBlock, defaultBlock)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, slabBlock, defaultBlock, 2)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, stairsBlock, defaultBlock, 1)
            generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, wallBlock, defaultBlock, 1)
            chiseledBlock?.let { generator.offerChiseledBlockRecipe(RecipeCategory.BUILDING_BLOCKS,it, slabBlock) }
            crackedBlock?.let  { generator.offerCrackingRecipe(it,defaultBlock) }
        }

        fun registerModels(modelGenerator: BlockStateModelGenerator) {
            val texture: TextureMap = TextureMap.all(defaultBlock)
            modelGenerator.registerSimpleCubeAll(defaultBlock)
            generateSlab(modelGenerator, slabBlock, texture)
            generateStairs(modelGenerator, stairsBlock, texture)
            generateWall(modelGenerator, wallBlock, texture)
            chiseledBlock?.let{
                val model:Identifier = Models.CUBE_COLUMN.upload(it, TextureMap.sideAndEndForTop(it), modelGenerator.modelCollector)
                modelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(it, BlockStateModelGenerator.createWeightedVariant(model)))
            }
            crackedBlock?.let {modelGenerator.registerSimpleCubeAll(it)}
        }
        companion object{

            fun createDefault(
                settings: Settings,
                name: String,
                registerer: GeneralDataRegisterer<Block, Settings>,
                hasCrackedVariant: Boolean = false,
                hasChiseledVariant: Boolean = false
            ) = BlockSubSet(
                defaultBlock = registerer.register(name, settings),
                variant = Variant.DEFAULT,
                name = name,
                registerer = registerer,
                hasCrackedVariant = hasCrackedVariant,
                hasChiseledVariant = hasChiseledVariant
            )
            fun create(
                variant: Variant,
                settings: Settings,
                name: String,
                registerer: GeneralDataRegisterer<Block, Settings>,
                hasCrackedVariant: Boolean = false,
                hasChiseledVariant: Boolean = false
            ) = BlockSubSet(
                defaultBlock = registerer.register(variant.createName(name,true), settings),
                variant = variant,
                name = name,
                registerer = registerer,
                hasCrackedVariant = hasCrackedVariant,
                hasChiseledVariant = hasChiseledVariant
            )
        }


    }
    class StoneSetBuilder(private val name: String, private val registerer: GeneralDataRegisterer<Block, Settings>){
        private var defaultBlockSettings: Settings = Settings.create()
        private val variants:ArrayList<Variant> = ArrayList()
        private var hasCobbledVariant = false
        private val crackableVariants:ArrayList<Variant> = ArrayList()
        private val chiselableVariants:ArrayList<Variant> = ArrayList()
        fun hasCobbledVariant(isTrue:Boolean = true):StoneSetBuilder{hasCobbledVariant = isTrue; return this}
        fun addVariants(vararg variantsAdded: Variant):StoneSetBuilder {
            variants.ensureCapacity(variants.size + variantsAdded.size)
            variantsAdded.forEach {addVariant(it)}
            return this
        }
        fun addVariant(variantAdded: Variant):StoneSetBuilder{
            if(variantAdded == Variant.DEFAULT)
                throw IllegalStateException("The Default Variant is registered during the building process")
            if(variantAdded == Variant.COBBLED)
                throw IllegalStateException("The Cobbled Variant is only registered based on the hasCobbledVariant property")
            if(variants.contains(variantAdded))
                throw IllegalStateException("This Variant has Already been Added")
            variants.add(variantAdded)
            return this
        }
        fun addCrackableVariant(variant: Variant):StoneSetBuilder{
            if(!(variants.contains(variant) || (variant == Variant.COBBLED && hasCobbledVariant) || variant == Variant.DEFAULT ))
                throw IllegalStateException("Non-Existant Variant Registered")
            if (crackableVariants.contains(variant))
                throw IllegalStateException("Crackability already registered for variant in question")
            crackableVariants.add(variant)
            return this
        }
        fun addCrackableVariants(vararg variantsAdded: Variant):StoneSetBuilder{
            crackableVariants.ensureCapacity(crackableVariants.size + variantsAdded.size)
            variantsAdded.forEach {addCrackableVariant(it)}
            return this
        }
        fun addChiselableVariant(variant: Variant):StoneSetBuilder{
            if(!(variants.contains(variant) || (variant == Variant.COBBLED && hasCobbledVariant) || variant == Variant.DEFAULT ))
                throw IllegalStateException("Non-Existant Variant Registered")
            if (chiselableVariants.contains(variant))
                throw IllegalStateException("Chiselability already registered for variant in question")
            chiselableVariants.add(variant)
            return this
        }
        fun addChiselableVariants(vararg variantsAdded: Variant):StoneSetBuilder{
            chiselableVariants.ensureCapacity(chiselableVariants.size + variantsAdded.size)
            variantsAdded.forEach {addChiselableVariant(it)}
            return this
        }
        fun setDefaultSettings(settings: Settings):StoneSetBuilder{
            this.defaultBlockSettings = settings
            return this
        }
        fun build():KStoneSet{
            val blockSubSets:ArrayList<BlockSubSet> = ArrayList(variants.size + 1 + if(hasCobbledVariant)1 else 0)
            val nonDefaultSubSets:ArrayList<BlockSubSet> = ArrayList(variants.size + if(hasCobbledVariant)1 else 0)
            val variantMap:EnumMap<Variant, BlockSubSet> = EnumMap(Variant::class.java)
            blockSubSets.add(
                BlockSubSet.createDefault(
                    name = name,
                    registerer = registerer,
                    settings = defaultBlockSettings,
                    hasCrackedVariant = crackableVariants.contains(Variant.DEFAULT),
                    hasChiseledVariant = chiselableVariants.contains(Variant.DEFAULT)
                )
            )
            val defaultBlock = blockSubSets[0].defaultBlock
            variantMap[Variant.DEFAULT] = blockSubSets[0]
            if(hasCobbledVariant){
                blockSubSets.add(
                    BlockSubSet.create(
                        name = name,
                        registerer = registerer,
                        variant = Variant.COBBLED,
                        hasCrackedVariant = crackableVariants.contains(Variant.COBBLED),
                        hasChiseledVariant = chiselableVariants.contains(Variant.COBBLED),
                        settings = Settings.copy(defaultBlock)
                    )
                )
                nonDefaultSubSets.add(blockSubSets[1])
                variantMap[Variant.COBBLED] = blockSubSets[1]
            }
            for (variant in variants){
                val subSet = BlockSubSet.create(
                    name = name,
                    registerer = registerer,
                    variant = variant,
                    hasCrackedVariant = crackableVariants.contains(variant),
                    hasChiseledVariant = chiselableVariants.contains(variant),
                    settings = Settings.copy(defaultBlock)
                )
                nonDefaultSubSets.add(subSet)
                variantMap[variant] = subSet
            }
            return KStoneSet(
                variants = variantMap,
                setsWithoutDefault = nonDefaultSubSets,
                name = name,
                defaultBlockSet = variantMap[Variant.DEFAULT]!!,
            )
        }

    }

    fun getDefaultBlockSet(): BlockSubSet {
        return defaultBlockSet
    }

    fun getDefaultBlock(): Block {
        return getDefaultBlockSet().defaultBlock
    }

    fun getVariant(variant: Variant?): BlockSubSet? {
        return variants[variant]
    }

    fun registerRecipes(generator: RecipeGenerator) {
        if (variants.containsKey(Variant.COBBLED)) {
            generator.offerSmelting(
                listOf<ItemConvertible>(getDefaultBlock()),
                RecipeCategory.BUILDING_BLOCKS,
                getVariant(Variant.COBBLED)!!.defaultBlock,
                0.1f, 200, "stone_smelting"
            )
        } else if (setsWithoutDefault.isNotEmpty()) {
            generator.createShaped(RecipeCategory.BUILDING_BLOCKS, setsWithoutDefault.first.defaultBlock)
                .pattern("##")
                .pattern("##")
                .input('#', getDefaultBlock())
                .criterion("get", InventoryChangedCriterion.Conditions.items(getDefaultBlock()))
                .offerTo((generator as RecipeGeneratorExporterAccessor).exporter())
        }
        getDefaultBlockSet().registerSpecificRecipes(generator)

        for (i in setsWithoutDefault.indices) {
            val set: BlockSubSet = setsWithoutDefault[i]
            set.registerSpecificRecipes(generator)
            set.registerRecipesFrom(getDefaultBlock(), generator)
            for (j in i + 1..<setsWithoutDefault.size) {
                setsWithoutDefault[j].registerRecipesFrom(set.defaultBlock, generator)
            }
            if (i > 0) {
                val previousVariantBlock: Block = setsWithoutDefault[i - 1].defaultBlock
                generator.createShaped(RecipeCategory.BUILDING_BLOCKS, set.defaultBlock, 4)
                    .pattern("##")
                    .pattern("##")
                    .input('#', previousVariantBlock)
                    .criterion("get", InventoryChangedCriterion.Conditions.items(previousVariantBlock))
                    .group(name + "_polishing")
                    .offerTo((generator as RecipeGeneratorExporterAccessor).exporter())
            }
        }
    }

    fun insertEntries(entries: FabricItemGroupEntries, injectionPoint: ItemConvertible?, shift: Shift) {
        val defaultSet = getDefaultBlockSet()
        if (shift == Shift.AFTER) {
            for (i in setsWithoutDefault.indices.reversed()) {
                val set: BlockSubSet = setsWithoutDefault[i]
                entries.addAfter(injectionPoint, set.list().map { it.asItem().defaultStack  })
            }
            entries.addAfter(injectionPoint, defaultSet.list().map { it.asItem().defaultStack })
        } else {
            for (i in setsWithoutDefault.indices.reversed()) {
                val set: BlockSubSet = setsWithoutDefault[i]
                entries.addBefore(injectionPoint, set.list().map { it.asItem().defaultStack })
            }
            entries.addBefore(injectionPoint, defaultSet.list().map { it.asItem().defaultStack })
        }
    }

    fun registerItemTags(tagProvider: FabricTagProvider.ItemTagProvider) {
        variants.values.forEach({ smallBlockSet: BlockSubSet ->
            smallBlockSet.registerItemTags(
                tagProvider
            )
        })
        val accessor = (tagProvider as FabricTagProviderAccessor)
        accessor.builder(ConventionalItemTags.STONES).add(getDefaultBlock().asItem())
        if (variants.containsKey(Variant.COBBLED)) {
            accessor.builder(ConventionalItemTags.COBBLESTONES).add(
                getVariant(
                    Variant.COBBLED
                )!!.defaultBlock.asItem()
            )
        }
    }

    enum class Variant(val prefix: String, val affix: String, val plural: Boolean) {
        DEFAULT("", "", false),
        COBBLED("cobbled_", "", false),
        BRICKS("", "_brick", true),
        TILES("", "_tile", true),
        POLISHED("polished_", "", false);

        fun createName(base: String, pluralized: Boolean): String {
            return prefix + base + affix + (if (pluralized && plural) "s" else "")
        }
    }

    fun registerBlockTags(tagProvider: FabricTagProvider.BlockTagProvider, mineabilityTag: TagKey<Block>?) {
        variants.values.forEach({ smallBlockSet: BlockSubSet ->
            smallBlockSet.registerBlockTags(
                tagProvider
            )
        })
        variants.values.forEach({ set: BlockSubSet ->
            set.list().forEach(
                ({ block: Block ->
                    (tagProvider as FabricTagProviderAccessor).builder(mineabilityTag).add(block)
                })
            )
        })
    }

    fun registerModels(modelGenerator: BlockStateModelGenerator) {
        variants.values.forEach({ set: BlockSubSet -> set.registerModels(modelGenerator) })
    }

    fun registerLootTables(lootTableProvider: FabricBlockLootTableProvider) {
        setsWithoutDefault.forEach(Consumer { set: BlockSubSet ->
            lootTableProvider.addDrop(set.stairsBlock)
            lootTableProvider.addDrop(set.defaultBlock)
            lootTableProvider.addDrop(set.wallBlock)
            lootTableProvider.slabDrops(set.slabBlock)
            set.crackedBlock?.let {lootTableProvider.addDrop(it)}
            set.chiseledBlock?.let {lootTableProvider.addDrop(it)}
        })
        val set = getDefaultBlockSet()
        if (variants.containsKey(Variant.COBBLED)) {
            lootTableProvider.addDrop(
                getDefaultBlock()
            ) { block: Block? ->
                lootTableProvider.drops(
                    block,
                    getVariant(Variant.COBBLED)!!.defaultBlock
                )
            }
        } else {
            lootTableProvider.addDrop(getDefaultBlock())
        }
        lootTableProvider.addDrop(set.stairsBlock)
        lootTableProvider.addDrop(set.wallBlock)
        lootTableProvider.slabDrops(set.slabBlock)
    }
    private inner class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
        override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator) {
            registerModels(blockStateModelGenerator)
        }
        override fun getName(): String = super.getName() + " For " + snakeCaseToNaturalCaseRegex(this@KStoneSet.name)
        override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {}
    }
    private inner class ItemTagProvider(output:FabricDataOutput, completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>):
        FabricTagProvider.ItemTagProvider(output, completableFuture, null) {
        override fun getName(): String = snakeCaseToNaturalCaseRegex(this@KStoneSet.name) + super.getName()

        override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
            registerItemTags(this)
        }
    }
    inner class BlockTagProvider(output:FabricDataOutput, completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,private val mineabilityTag: TagKey<Block>):
        FabricTagProvider.BlockTagProvider(output, completableFuture) {
        override fun getName(): String = snakeCaseToNaturalCaseRegex(this@KStoneSet.name) + super.getName()

        override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
            registerBlockTags(this, mineabilityTag)
        }
    }
    private inner class RecipeProvider(output:FabricDataOutput, completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>):
            FabricRecipeProvider(output,completableFuture){
        override fun getName(): String = snakeCaseToNaturalCaseRegex(this@KStoneSet.name + "_recipe_provider")
        override fun getRecipeGenerator(
            registryLookup: RegistryWrapper.WrapperLookup?,
            exporter: RecipeExporter?
        ) = Generator(registryLookup,exporter)
        private inner class Generator(
            registryLookup: RegistryWrapper.WrapperLookup?,
            exporter: RecipeExporter?): RecipeGenerator(registryLookup,exporter) {
            override fun generate() {
                registerRecipes(this)
            }
        }
    }
    private inner class LootTableProvider(output:FabricDataOutput, completableFuture: CompletableFuture<RegistryWrapper.WrapperLookup>):
    FabricBlockLootTableProvider(output,completableFuture){
        override fun getName(): String = super.getName() + " For " +snakeCaseToNaturalCaseRegex(this@KStoneSet.name)
        override fun generate() {
            registerLootTables(this)
        }
    }
    fun StreamBlocks(): Stream<Block> = variants.values.stream().flatMap{it.list().stream()}

    fun provideDataTo(pack:FabricDataGenerator.Pack, mineabilityTag: TagKey<Block>) {
        pack.addProvider {
            output:FabricDataOutput,
            completableFuture:CompletableFuture<RegistryWrapper.WrapperLookup> ->
            ItemTagProvider(output, completableFuture)
        }
        pack.addProvider {
                output:FabricDataOutput,
                completableFuture:CompletableFuture<RegistryWrapper.WrapperLookup> ->
            BlockTagProvider(output, completableFuture, mineabilityTag)
        }
        pack.addProvider {
                output:FabricDataOutput,
                completableFuture:CompletableFuture<RegistryWrapper.WrapperLookup> ->
            LootTableProvider(output, completableFuture)
        }
        pack.addProvider {
                output:FabricDataOutput,
                completableFuture:CompletableFuture<RegistryWrapper.WrapperLookup> ->
            RecipeProvider(output, completableFuture)
        }
        pack.addProvider {output:FabricDataOutput -> ModelProvider(output)}
        createLangProviders(this).forEach{pack.addProvider(it)}
    }
}
fun snakeCaseToNaturalCaseRegex(snakeCase: String): String {
    return "_[a-zA-Z]".toRegex().replace(snakeCase) {
        it.value.replace("_", " ").uppercase()
    }.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
