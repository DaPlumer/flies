package net.daplumer.flies;

import net.daplumer.data_modification_utils.mod_registries.GeneralDataRegisterer;
import net.daplumer.data_modification_utils.mod_registries.Registerer;
import net.daplumer.data_modification_utils.mod_registries.registering_functions.ItemsKt;
import net.daplumer.flies.blocks.FlyTrapMaw;
import net.daplumer.flies.blocks.FlyTrapStem;
import net.daplumer.flies.entity.Fly;
import net.daplumer.flies.entity.ModEntitySpawns;
import net.daplumer.flies.item.FlyWings;
import net.daplumer.flies.worldgen.FlyTrapFeature;
import net.daplumer.flies.worldgen.FlyTrapFeatureConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.potion.Potion;
import net.minecraft.registry.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.gen.GenerationStep;

public class Flies implements ModInitializer {
    public static final String MOD_ID = "flies";
    public static final Registerer REGISTRY = Registerer.of(MOD_ID);
    public static final Potion LEVITATION_POTION =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(MOD_ID, "levitation_potion"),
                    new Potion("levitation",
                            new StatusEffectInstance(
                                    StatusEffects.LEVITATION,
                                    600,
                                    0)));
    public static final Potion EXTENDED_LEVITATION_POTION =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(MOD_ID, "extended_levitation_potion"),
                    new Potion("levitation",
                            new StatusEffectInstance(
                                    StatusEffects.LEVITATION,
                                    1200,
                                    0)));
    public static final GeneralDataRegisterer<Block, AbstractBlock.Settings> BLOCKS = REGISTRY.BLOCKS;
    public static final Block FLY_TRAP_MAW = BLOCKS.register(
            "fly_trap_maw",
            FlyTrapMaw::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DARK_DULL_PINK)
                    .strength(4F)
                    .requiresTool()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .sounds(BlockSoundGroup.GRASS)
                    .nonOpaque()
    );
    public static final Block FLY_TRAP_STEM = BLOCKS.register(
            "fly_trap_stem",
            FlyTrapStem::new,
            AbstractBlock.Settings.create()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .strength(4F)
                    .nonOpaque()
                    .noCollision()
                    .sounds(BlockSoundGroup.GRASS)
    );
    public static final BlockItem FLY_TRAP = Registerer.registerBlockItem(FLY_TRAP_MAW);
    public static final RegistryKey<DamageType> FLY_TRAP_DIGEST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID,"fly_trap"));

    public static final EntityType<Fly> FLY = REGISTRY.ENTITY_TYPES.register(
            "fly",
            EntityType.Builder.create(Fly::new, SpawnGroup.CREATURE)
                    .dimensions(0.25F,0.25F)
    );

    public static final Identifier FLY_TRAP_FEATURE_ID = Identifier.of(MOD_ID, "fly_trap_feature");
    public static final FlyTrapFeature FLY_TRAP_FEATURE = new FlyTrapFeature(FlyTrapFeatureConfig.CODEC);
    public static final SpawnEggItem FLY_SPAWN_EGG = REGISTRY.ITEMS.register("fly_spawn_egg",
            new Item.Settings()
                    .maxCount(1),
            ItemsKt.SPAWN_EGG(FLY)
    );
    public static final Item FLY_WING = REGISTRY.ITEMS.register(
            "fly_wing",
            new Item.Settings()
                    .maxCount(16)
    );
    public static final Item FLY_WINGS = REGISTRY.ITEMS.register(
            "fly_wings",
            new Item.Settings()
                    .maxCount(1)
                    .component(DataComponentTypes.GLIDER, Unit.INSTANCE)
                    .component(
                            DataComponentTypes.EQUIPPABLE,
                            EquippableComponent.builder(EquipmentSlot.CHEST)
                                    .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA)
                                    .model(EquipmentAssetKeys.ELYTRA)
                                    .damageOnHurt(false)
                                    .build()
                    ),
            FlyWings::new
    );
    @Override public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((entries -> entries.addBefore(Blocks.BIG_DRIPLEAF,FLY_TRAP)));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addBefore(Items.FEATHER, FLY_WING));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> entries.addBefore(Items.FOX_SPAWN_EGG,FLY_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.addAfter(Items.ELYTRA,FLY_WINGS));
        FabricDefaultAttributeRegistry.register(FLY, Fly.createAttributes());
        ModEntitySpawns.addEntitySpawns();
        CompostingChanceRegistry.INSTANCE.add(FLY_TRAP, .85F);
        Registry.register(Registries.FEATURE, FLY_TRAP_FEATURE_ID, FLY_TRAP_FEATURE);
        BiomeModifications.addFeature(BiomeSelectors.tag(ConventionalBiomeTags.IS_SWAMP),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE,FLY_TRAP_FEATURE_ID));

        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder -> {
            builder.registerRecipes(
                    FLY_WING,
                    Registries.POTION.getEntry(LEVITATION_POTION)
            );
            builder.registerPotionRecipe(
                    Registries.POTION.getEntry(LEVITATION_POTION),
                    Items.REDSTONE,
                    Registries.POTION.getEntry(EXTENDED_LEVITATION_POTION)
            );
        }));
    }




}
