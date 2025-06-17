package net.daplumer.flies.entity;

import net.daplumer.flies.Flies;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.entity.*;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.SpawnSettings;

public class ModEntitySpawns {
    public static void addEntitySpawns() {

        BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_SWAMP),
                SpawnGroup.AMBIENT, Flies.FLY, 300, 100, 200);
        SpawnRestriction.register(
                Flies.FLY,
                SpawnLocationTypes.UNRESTRICTED,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                (type, world, spawnReason, pos, random) -> true
        );
    }
}