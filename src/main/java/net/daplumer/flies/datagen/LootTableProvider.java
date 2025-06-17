package net.daplumer.flies.datagen;

import net.daplumer.flies.Flies;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LootTableProvider extends FabricBlockLootTableProvider {
    public LootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    /**
     * Implement this method to add block drops.
     *
     * <p>Use the range of {@link BlockLootTableGenerator#addDrop} methods to generate block drops.
     */
    @Override
    public void generate() {
        addDrop(Flies.FLY_TRAP_MAW, dropsWithShears(Flies.FLY_TRAP_MAW));
    }
}
