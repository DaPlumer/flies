package net.daplumer.data_modification_utils.mod_registries;

import kotlin.jvm.functions.Function1;
import net.minecraft.item.equipment.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ModArmorMaterialRegisterer extends ModDataRegisterer<ArmorMaterial, ArmorMaterialSettings,EquipmentAsset>{
    public static final Map<RegistryKey<EquipmentAsset>, ArmorMaterial> armorMaterials = new HashMap<>(Map.of(
            ArmorMaterials.LEATHER.assetId(),ArmorMaterials.LEATHER,
            ArmorMaterials.CHAIN.assetId(),ArmorMaterials.CHAIN,
            ArmorMaterials.IRON.assetId(),ArmorMaterials.IRON,
            ArmorMaterials.GOLD.assetId(),ArmorMaterials.GOLD,
            ArmorMaterials.DIAMOND.assetId(),ArmorMaterials.DIAMOND,
            ArmorMaterials.NETHERITE.assetId(),ArmorMaterials.NETHERITE,
            ArmorMaterials.TURTLE_SCUTE.assetId(),ArmorMaterials.TURTLE_SCUTE,
            ArmorMaterials.ARMADILLO_SCUTE.assetId(),ArmorMaterials.ARMADILLO_SCUTE
        )
    );

    public ModArmorMaterialRegisterer(@NotNull String namespace) {
        super(namespace);
    }


    @Override
    public <U extends ArmorMaterial> U register(@NotNull String name, @Nullable ArmorMaterialSettings instanceSettings, @Nullable Function1<? super ArmorMaterialSettings,? extends ArmorMaterial> instanceFactory) {
        Function1<? super ArmorMaterialSettings, ? extends ArmorMaterial> factory = Objects.requireNonNullElse(instanceFactory,(Function1<? super ArmorMaterialSettings, ? extends ArmorMaterial>) ArmorMaterialSettings::build);
        ArmorMaterialSettings settings = Objects.requireNonNullElse(instanceSettings, ArmorMaterialSettings.copy(ArmorMaterials.LEATHER));
        settings.assetId(getRegistryKey(name));
        @SuppressWarnings("unchecked") U instance = (U) factory.invoke(settings);
        armorMaterials.put(getRegistryKey(name),instance);
        return instance;
    }

    @Override
    public ArmorMaterial getInstance(@NotNull Identifier identifier) {
        return armorMaterials.get(getRegistryKey(identifier));
    }

    @Override
    public @NotNull RegistryKey<EquipmentAsset> getRegistryKey(@NotNull Identifier identifier) {
        return RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, identifier);
    }

}
