package net.daplumer.data_modification_utils.block_set_generation

import net.minecraft.block.SlabBlock
import net.minecraft.block.StairsBlock
import net.minecraft.block.WallBlock
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.Models
import net.minecraft.client.data.TextureMap

fun generateSlab(modelGenerator: BlockStateModelGenerator, slab:SlabBlock, texture:TextureMap){

    val slabBottomModelId = Models.SLAB.upload(slab, texture, modelGenerator.modelCollector)
    val slabTopModelId = Models.SLAB_TOP.upload(slab, texture, modelGenerator.modelCollector)
    val both = Models.CUBE_ALL.upload(slab.asItem(), texture, modelGenerator.modelCollector)
    modelGenerator.blockStateCollector.accept(
        BlockStateModelGenerator.createSlabBlockState(
            slab,
            BlockStateModelGenerator.createWeightedVariant(slabBottomModelId),
            BlockStateModelGenerator.createWeightedVariant(slabTopModelId),
            BlockStateModelGenerator.createWeightedVariant(both)
        )
    )
    modelGenerator.registerParentedItemModel(slab, slabBottomModelId)
}

fun generateStairs(modelGenerator: BlockStateModelGenerator, stairs:StairsBlock, texture: TextureMap){

    val stairsModelId = Models.STAIRS.upload(stairs, texture, modelGenerator.modelCollector)
    val innerStairsModelId = Models.INNER_STAIRS.upload(stairs, texture, modelGenerator.modelCollector)
    val outerStairsModelId = Models.OUTER_STAIRS.upload(stairs, texture, modelGenerator.modelCollector)
    modelGenerator.blockStateCollector.accept(
        BlockStateModelGenerator.createStairsBlockState(
            stairs,
            BlockStateModelGenerator.createWeightedVariant(innerStairsModelId),
            BlockStateModelGenerator.createWeightedVariant(stairsModelId),
            BlockStateModelGenerator.createWeightedVariant(outerStairsModelId)
        )
    )
    modelGenerator.registerParentedItemModel(stairs, stairsModelId)
}
fun generateWall(modelGenerator: BlockStateModelGenerator, wall:WallBlock, texture: TextureMap){
    val postModel = Models.TEMPLATE_WALL_POST.upload(wall, texture, modelGenerator.modelCollector)
    val lowSide = Models.TEMPLATE_WALL_SIDE.upload(wall, texture, modelGenerator.modelCollector)
    val tallSide = Models.TEMPLATE_WALL_SIDE_TALL.upload(wall, texture, modelGenerator.modelCollector)
    val inventory = Models.WALL_INVENTORY.upload(wall, texture, modelGenerator.modelCollector)
    modelGenerator.blockStateCollector.accept(
        BlockStateModelGenerator.createWallBlockState(
            wall,
            BlockStateModelGenerator.createWeightedVariant(postModel),
            BlockStateModelGenerator.createWeightedVariant(lowSide),
            BlockStateModelGenerator.createWeightedVariant(tallSide)
        )
    )
    modelGenerator.registerParentedItemModel(wall, inventory)
}