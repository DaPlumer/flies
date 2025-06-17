@file:Suppress("unused", "FunctionName")

package net.daplumer.data_modification_utils.mod_registries.registering_functions

import net.daplumer.data_modification_utils.mixin.BlockSetTypeMixin
import net.daplumer.data_modification_utils.mixin.WoodTypeMixin
import net.minecraft.block.*

fun BUTTON(type: BlockSetType, ticks: Int = 30): (AbstractBlock.Settings) -> ButtonBlock = {settings -> ButtonBlock(type,ticks,settings) }
fun STAIRS(baseBlockState:BlockState): (AbstractBlock.Settings) -> StairsBlock = {settings -> StairsBlock(baseBlockState,settings) }
fun DOOR(type:BlockSetType): (AbstractBlock.Settings) -> DoorBlock = {settings -> DoorBlock(type, settings)}
fun TRAPDOOR(type:BlockSetType): (AbstractBlock.Settings) -> TrapdoorBlock = {settings -> TrapdoorBlock(type, settings)}
fun FENCE_GATE(type:WoodType): (AbstractBlock.Settings) -> FenceGateBlock = {settings -> FenceGateBlock(type, settings)}
fun PRESSURE_PLATE(type: BlockSetType):(AbstractBlock.Settings) -> PressurePlateBlock = {settings -> PressurePlateBlock(type,settings)}
fun SIGN(type:WoodType):(AbstractBlock.Settings) -> SignBlock = {settings ->  SignBlock(type,settings)}
fun WALL_SIGN(type:WoodType):(AbstractBlock.Settings) -> WallSignBlock = {settings ->  WallSignBlock(type,settings)}
fun HANGING_SIGN(type:WoodType):(AbstractBlock.Settings) -> HangingSignBlock = {settings ->  HangingSignBlock(type,settings)}
fun WALL_HANGING_SIGN(type:WoodType):(AbstractBlock.Settings) -> WallHangingSignBlock = {settings ->  WallHangingSignBlock(type,settings) }

fun registerBlockSetType(type: BlockSetType, key:String):BlockSetType {
    BlockSetTypeMixin.getValues()[key] = type;
    return type
}
fun registerWoodType(type: WoodType, key:String):WoodType {
    WoodTypeMixin.getValues()[key] = type;
    return type;
}

fun copyLootTable(block: Block, copyTranslationKey: Boolean): AbstractBlock.Settings {
    var settings2 = AbstractBlock.Settings.create().lootTable(block.lootTableKey)
    if (copyTranslationKey) {
        settings2 = settings2.overrideTranslationKey(block.translationKey)
    }

    return settings2
}