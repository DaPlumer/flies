package net.daplumer.flies.worldgen;

import com.mojang.serialization.Codec;
import net.daplumer.flies.Flies;
import net.daplumer.flies.blocks.FlyTrapMaw;
import net.daplumer.flies.blocks.FlyTrapStem;
import net.daplumer.flies.datagen.BlockTagRegistration;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import java.util.ArrayList;
import java.util.List;

public class FlyTrapFeature extends Feature<FlyTrapFeatureConfig> {
    public FlyTrapFeature(Codec<FlyTrapFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<FlyTrapFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos targetPos = context.getOrigin();
        return generateNearby(targetPos,context.getRandom(),30).stream().anyMatch(pos -> genSingle(world,pos,context.getConfig().height().get(context.getRandom())));
    }
    private boolean genSingle(StructureWorldAccess world, BlockPos targetPos, int height){
        try {
            targetPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, targetPos);
            if (!world.getBiome(targetPos).isIn(ConventionalBiomeTags.IS_SWAMP)) return false;
            if (!world.getBlockState(targetPos.down()).isIn(BlockTagRegistration.FLY_TRAP_PLANTABLE)) return false;
            for (int i = 0; i < height; i++) {
                if (!world.getBlockState(targetPos).isAir()) return i != 0;
                if (i > 0)
                    world.setBlockState(targetPos.down(), Flies.FLY_TRAP_STEM.getDefaultState().with(FlyTrapStem.FACING, FlyTrapStem.getDirection(targetPos.down())), 0);
                world.setBlockState(targetPos, Flies.FLY_TRAP_MAW.getDefaultState().with(FlyTrapMaw.AXIS,FlyTrapStem.getDirection(targetPos.down(1000)).getAxis()), Block.NOTIFY_LISTENERS);
                targetPos = targetPos.up();
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }

    }
    private static List<BlockPos> generateNearby(BlockPos origin, Random random, int length){
        ArrayList<BlockPos> positions = new ArrayList<>(length);
        for(int i = 0; i < length; i++){
            positions.add(origin.add( new BlockPos(random.nextBetween(-20,20),random.nextBetween(-20,20),random.nextBetween(-20,20))));
        }
        return positions;
    }
}
