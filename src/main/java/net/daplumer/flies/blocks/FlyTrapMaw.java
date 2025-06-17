package net.daplumer.flies.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import net.daplumer.flies.Flies;
import net.daplumer.flies.FliesClient;
import net.daplumer.flies.datagen.BlockTagRegistration;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentEntrypoint;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Attachment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FlyTrapMaw extends Block implements Fertilizable {
    public static final MapCodec<FlyTrapMaw> CODEC = createCodec(FlyTrapMaw::new);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    public static final EnumProperty<Open> OPEN = EnumProperty.of("open", Open.class);
    public static final BooleanProperty OPENING = BooleanProperty.of("opening");
    private static final ImmutableMap<Direction.Axis, VoxelShape> CULLING_SHAPES =
            ImmutableMap.<Direction.Axis, VoxelShape>builderWithExpectedSize(2)
                    .put(Direction.Axis.X, Block.createColumnShape(4,8,0,2))
                    .put(Direction.Axis.Z, Block.createColumnShape(8,4,0,2))
                    .build();
    private static final ImmutableMap<Open, VoxelShape> OPEN_TO_SHAPE_Z =
            ImmutableMap.<Open,VoxelShape>builderWithExpectedSize(3)
                    .put(Open.OPEN, Block.createColumnShape(10,8,0,2))
                    .put(Open.CLOSING, Block.createColumnShape(9,8,0,6))
                    .put(Open.CLOSED, Block.createColumnShape(5,8,0,7))
                    .build();
    private static final ImmutableMap<Open, VoxelShape> OPEN_TO_SHAPE_X =
            ImmutableMap.<Open,VoxelShape>builderWithExpectedSize(3)
                    .put(Open.OPEN, Block.createColumnShape(8,10,0,2))
                    .put(Open.CLOSING, Block.createColumnShape(8,9,0,6))
                    .put(Open.CLOSED, Block.createColumnShape(8,5,0,7))
                    .build();
    public static final ImmutableMap<Direction.Axis, ImmutableMap<Open,VoxelShape>> OPEN_TO_SHAPE =
            ImmutableMap.of(Direction.Axis.X,OPEN_TO_SHAPE_X, Direction.Axis.Z, OPEN_TO_SHAPE_Z);
    public static final ImmutableMap<Direction.Axis, VoxelShape> TRAP_SHAPE = ImmutableMap.of(
            Direction.Axis.X, Block.createColumnShape(8,5,2,7),
            Direction.Axis.Z, Block.createColumnShape(5,8,2,7)
    );

    @Override public MapCodec<FlyTrapMaw> getCodec() {return CODEC;}

    public FlyTrapMaw(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN,Open.OPEN).with(AXIS, Direction.Axis.X).with(OPENING, true));
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        //return TRAP_SHAPE.get(state.get(AXIS));
        return OPEN_TO_SHAPE.get(state.get(AXIS)).get(state.get(OPEN));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(state.get(OPEN).equals(Open.CLOSING))
            return OPEN_TO_SHAPE.get(state.get(AXIS)).get(Open.OPEN);
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state) {return CULLING_SHAPES.get(state.get(AXIS));}

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {return world.getBlockState(pos.up()).isAir();}

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir() && random.nextBoolean();
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos.up(), state);
        world.setBlockState(pos, Flies.FLY_TRAP_STEM.getDefaultState());
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(AXIS,ctx.getHorizontalPlayerFacing().getAxis());
    }


    public enum Open implements StringIdentifiable {
        OPEN("open"),
        CLOSING("closing"),
        CLOSED("closed");
        private final String string;

        Open(String string) {
            this.string = string;
        }
        @Override
        public String asString() {
            return string;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS,OPEN,OPENING);
    }
    public static VoxelShape rotate(VoxelShape shape, boolean rotate){
        if(!rotate) return shape;
        return VoxelShapes.transform(shape, DirectionTransformation.ROT_90_REF_Z_POS);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if(isEntityAbove(pos,entity, state.get(AXIS))) {
            world.setBlockState(pos, state.with(OPENING, false));
            entity.setVelocity(entity.getVelocity().multiply(0.25,0.0525,0.25));
            if(state.get(OPEN) == Open.CLOSED){
                if(entity.getPos().subtract(pos.toBottomCenterPos().add(0,0.125,0)).lengthSquared() < 0.25 ) entity.setPosition(pos.toBottomCenterPos().add(0,0.125,0));
                Registry<DamageType> registry = world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE);
                if(world instanceof ServerWorld serverWorld)
                    entity.damage(serverWorld,new DamageSource(registry.getEntry(Flies.FLY_TRAP_DIGEST.getValue()).get()) ,2);
            }
            scheduleTick(pos,world);
        }
    }


    public static boolean isEntityAbove(BlockPos pos, Entity entity, Direction.Axis axis) {
        if(entity.getY() - pos.getY() < 0.120 || entity.getY() - pos.getY() > 0.375) return false;



        if(axis.equals(Direction.Axis.X)) return Math.abs(entity.getX() - pos.getX() - .5) < 0.25 + entity.getBoundingBox().getLengthX()/2 &&
                Math.abs(entity.getZ() - pos.getZ() - .5) < 0.3125 + entity.getBoundingBox().getLengthZ()/2;
        return Math.abs(entity.getZ() - pos.getZ() - .5) < 0.25 + entity.getBoundingBox().getLengthZ()/2 &&
                Math.abs(entity.getX() - pos.getX() - .5) < 0.3125 + entity.getBoundingBox().getLengthX()/2;
    }
    private void scheduleTick(BlockPos pos, World world){
        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        Open open = state.get(OPEN);
        if(state.get(OPENING)){
            switch (open){
                case OPEN -> {}
                case CLOSING -> {
                    world.setBlockState(pos, state.with(OPEN, Open.OPEN));
                    playTiltSound(world,pos,SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
                }
                case CLOSED -> {
                    world.setBlockState(pos, state.with(OPEN, Open.CLOSING));
                    scheduleTick(pos,world);
                    playTiltSound(world,pos,SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
                }
            }
        } else{
            switch (open){
                case OPEN -> {
                    world.setBlockState(pos, state.with(OPEN, Open.CLOSING));
                    playTiltSound(world,pos,SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
                }
                case CLOSING -> {
                    world.setBlockState(pos, state.with(OPEN, Open.CLOSED));
                    playTiltSound(world,pos,SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
                }
                case CLOSED -> world.setBlockState(pos,state.with(OPENING, true));
            }
            scheduleTick(pos,world);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        if(world.getBlockState(pos.down()).isOf(Flies.FLY_TRAP_STEM)){
            world.breakBlock(pos.down(), false);
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if(world.getBlockState(pos.down()).isOf(Flies.FLY_TRAP_MAW)){
            world.setBlockState(pos.down(), Flies.FLY_TRAP_STEM.getDefaultState());
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
    @Nullable WireOrientation wireOrientation, boolean notify) {
        if(!world.getBlockState(pos.down()).isIn(BlockTagRegistration.FLY_TRAP_PLANTABLE)) world.breakBlock(pos, false);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos) && world.getBlockState(pos.down()).isIn(BlockTagRegistration.FLY_TRAP_PLANTABLE);
    }
    private static void playTiltSound(World world, BlockPos pos, SoundEvent soundEvent) {
        float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, f);
    }
    @Override
    public Item asItem() {
        return Flies.FLY_TRAP;
    }
}
