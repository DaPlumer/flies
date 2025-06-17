package net.daplumer.flies.datagen;

import net.daplumer.flies.Flies;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class BlockTagRegistration extends FabricTagProvider.BlockTagProvider {
    /**
     * Implement this method and then use {@link FabricTagProvider#getOrCreateTagBuilder} to get and register new tag builders.
     *
     * @param wrapperLookup
     */
    public static final TagKey<Block> FLY_TRAP_PLANTABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Flies.MOD_ID, "fly_trap_plantable"));

    public BlockTagRegistration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(FLY_TRAP_PLANTABLE).add(Flies.FLY_TRAP_MAW, Flies.FLY_TRAP_STEM, Blocks.CLAY, Blocks.FARMLAND);
        getOrCreateTagBuilder(FLY_TRAP_PLANTABLE).addOptionalTags(BlockTags.DIRT, ConventionalBlockTags.GRAVELS, BlockTags.TERRACOTTA);
    }
}
