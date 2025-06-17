package net.daplumer.flies.datagen;

import net.daplumer.flies.Flies;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;

public class ModelRegistration extends FabricModelProvider {
    public ModelRegistration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerItemModel(Flies.FLY_TRAP);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(Flies.FLY_SPAWN_EGG, Models.GENERATED);
        itemModelGenerator.register(Flies.FLY_WINGS, Models.GENERATED);
        itemModelGenerator.register(Flies.FLY_WING, Models.GENERATED);
    }
}
