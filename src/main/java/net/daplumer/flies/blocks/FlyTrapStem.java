package net.daplumer.flies.blocks;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import net.daplumer.flies.Flies;
import net.daplumer.flies.datagen.BlockTagRegistration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.WireOrientation;
import org.apache.commons.compress.archivers.zip.PKWareExtraHeader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class FlyTrapStem extends Block {
    public static final MapCodec<FlyTrapStem> CODEC = createCodec(FlyTrapStem::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final VoxelShape OUTLINE = VoxelShapes.union(
            Block.createColumnShape(2,0,7),
            Block.createColumnShape(2,9,16),
            VoxelShapes.cuboid(7,5,4,9,11,6),
            VoxelShapes.cuboid(7,9,6,9,11,7),
            VoxelShapes.cuboid(7,5,6,9,7,7)
    );
    public static final VoxelShape CULLING = Block.createColumnShape(2,-7,7);
    public FlyTrapStem(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {return CODEC;}

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        if(!(world.getBlockState(pos.up()).getBlock() == Flies.FLY_TRAP_MAW ||
                world.getBlockState(pos.up()).getBlock() == Flies.FLY_TRAP_STEM
        ) |!
                world.getBlockState(pos.down()).isIn(BlockTagRegistration.FLY_TRAP_PLANTABLE)) world.breakBlock(pos, false);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.setBlockState(pos,state.with(FACING,getDirection(pos)));
        super.onBlockAdded(state, world, pos, oldState, notify);
    }
    public static Direction getDirection(BlockPos pos){
        return switch ((Hashing.sha256().hashLong(pos.asLong()).asInt() ^ pos.hashCode()) & 3){
            case 0 -> Direction.NORTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> throw new IllegalStateException("What The Fuck did you do?");
        };
    }

    @Override
    public Item asItem() {
        return Flies.FLY_TRAP;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state) {
        return CULLING;
    }
}
