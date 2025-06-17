package net.daplumer.data_modification_utils.mod_registries.registering_functions

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.ChestBoatEntity
import net.minecraft.item.Item
import net.minecraft.world.World

fun BOAT(item: ()->Item) =
    EntityType.Builder.create({ type: EntityType<BoatEntity?>?, world: World? ->
        BoatEntity(
            type,
            world,
            item
        )
    }, SpawnGroup.MISC
    )
fun CHEST_BOAT(item: ()->Item) =
    EntityType.Builder.create({ type: EntityType<ChestBoatEntity?>?, world: World? ->
        ChestBoatEntity(
            type,
            world,
            item
        )
    }, SpawnGroup.MISC
    )