package net.daplumer.flies;

import net.daplumer.flies.datagen.BlockTagRegistration;
import net.daplumer.flies.datagen.LootTableProvider;
import net.daplumer.flies.datagen.ModelRegistration;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;

public class FliesDataGen implements DataGeneratorEntrypoint {
    /**
     * Register {@link DataProvider} with the {@link FabricDataGenerator} during this entrypoint.
     *
     * @param fabricDataGenerator The {@link FabricDataGenerator} instance
     */
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockTagRegistration::new);
        pack.addProvider(ModelRegistration::new);
        pack.addProvider(LootTableProvider::new);
    }
}
