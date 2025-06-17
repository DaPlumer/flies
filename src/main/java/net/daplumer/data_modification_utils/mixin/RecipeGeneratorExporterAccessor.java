package net.daplumer.data_modification_utils.mixin;

import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeGenerator.class)
public interface RecipeGeneratorExporterAccessor {
    @Accessor("exporter")
    RecipeExporter exporter();
}
