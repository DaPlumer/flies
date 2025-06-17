package net.daplumer.flies.entity;

import net.daplumer.flies.Flies;
import net.daplumer.flies.blocks.FlyTrapMaw;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class Fly extends MobEntity implements Flutterer{
    public Fly(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);

    }


    public static DefaultAttributeContainer.Builder createAttributes(){
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 1)
                .add(EntityAttributes.MOVEMENT_SPEED,1)
                .add(EntityAttributes.TEMPT_RANGE, 10)
                .add(EntityAttributes.FLYING_SPEED, 0.5);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    protected Text getDefaultName() {
        return super.getDefaultName();
    }
    protected boolean overFlyTrap(){
        return Stream.of(0,1,2,3,4,5).anyMatch(this::over);
    }
    protected boolean over(int i){
        BlockPos pos = getBlockPos().down(i);
        if(!getWorld().getBlockState(pos).isOf(Flies.FLY_TRAP_MAW)) return false;
        if(getY() - pos.getY() < 0.120) return false;
        if(getWorld().getBlockState(pos).get(FlyTrapMaw.AXIS).equals(Direction.Axis.X)) return Math.abs(getX() - pos.getX() - .5) < 0.25 + getBoundingBox().getLengthX()/2 &&
                Math.abs(getZ() - pos.getZ() - .5) < 0.3125 + getBoundingBox().getLengthZ()/2;
        return Math.abs(getZ() - pos.getZ() - .5) < 0.25 + getBoundingBox().getLengthZ()/2 &&
                Math.abs(getX() - pos.getX() - .5) < 0.3125 + getBoundingBox().getLengthX()/2;
    }

    @Override
    protected double getGravity() {
        if(tooHigh()) return super.getGravity()/2;
        return -super.getGravity()/2;
    }
    protected boolean isAir(BlockPos pos){
        return !getWorld().getBlockState(pos).isOpaque() && getWorld().getBlockState(pos).getFluidState().isEmpty();
    }
    protected boolean tooHigh(){
        return (isAir(getBlockPos()) & isAir(getBlockPos().down()) & isAir(getBlockPos().down(2)));
    }


    Vec3d lastVelocity;
    @Override
    public void tick() {
        lastVelocity = this.getVelocity();
        boidTick();
        if(overFlyTrap()){
            setVelocity(getVelocity().multiply(1).add(new Vec3d(0,-.5,0)));
        }

        super.tick();
    }

    @Override
    public Arm getMainArm() {
        return null;
    }

    @Override
    public boolean isInAir() {
        return !isOnGround();
    }


    public double getTargetDistance() {
        return .5;
    }

    public double getTargetSpeed() {
        return 3;
    }

    public double getCohesion() {
        return .03;
    }

    public double getSeparation() {
        return .025;
    }

    public double getAlignment() {
        return .003;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BEE_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BEE_LOOP;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BEE_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return super.getSoundVolume() * .05F;
    }


    public void boidTick() {
        double inverseTargetDistance = 1.0/(this.getTargetDistance() * this.getTargetDistance());
        Vec3d offset;
        Vec3d inverseOffset;
        double scale;
        List<Entity> nearbyMobs = getEntityWorld().getNonSpectatingEntities(Entity.class, Box.of(getPos(), 5,  5, 5));
        Vec3d cohesionVector = Vec3d.ZERO;
        Vec3d separationVector = Vec3d.ZERO;
        Vec3d alignmentVector = Vec3d.ZERO;
        assert !nearbyMobs.isEmpty();
        for (Entity neighbor : nearbyMobs) {
            offset = neighbor.getPos().subtract(getPos());
            if(offset.lengthSquared() != 0) {
                scale = 1/offset.lengthSquared();
                inverseOffset = offset.multiply(Math.min(1,scale));

                cohesionVector = cohesionVector.add(inverseOffset);

                if (scale < inverseTargetDistance) {
                    separationVector = separationVector.add(offset.negate().multiply(scale * scale));
                }

                alignmentVector = alignmentVector.add(neighbor.getVelocity().multiply(scale));
            }



        }
        alignmentVector = alignmentVector.normalize();
        double speedDistance = getTargetSpeed() - getVelocity().length();
        addVelocity(alignmentVector.subtract(getVelocity()).multiply(getAlignment() * (speedDistance * speedDistance)));
        addVelocity(randomVec(getRandom()).multiply(.05));
        addVelocity(cohesionVector.multiply(this.getCohesion()/nearbyMobs.size()));

        addVelocity(separationVector.multiply(this.getSeparation()/nearbyMobs.size()));
    }

    Vec3d randomVec(Random random){
        return Vec3d.fromPolar((random.nextFloat())*7200, (random.nextFloat())*7200);
    }

}
