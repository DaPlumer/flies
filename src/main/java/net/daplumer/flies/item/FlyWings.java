package net.daplumer.flies.item;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class FlyWings extends Item {
    public FlyWings(Settings settings) {
        super(settings);
    }	public static final ComponentType<Unit> HAS_FLOWN = register("has_flown", builder -> builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC));
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if(entity instanceof LivingEntity livingEntity){
             if(slot == EquipmentSlot.CHEST) {
                 if (livingEntity.isGliding()) stack.set(HAS_FLOWN, Unit.INSTANCE);
                 else if (stack.contains(HAS_FLOWN)) {
                     Item item = stack.getItem();
                     stack.decrement(1);
                     livingEntity.sendEquipmentBreakStatus(item, EquipmentSlot.CHEST);
                 }
             }
        }
    }

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
    }
}
