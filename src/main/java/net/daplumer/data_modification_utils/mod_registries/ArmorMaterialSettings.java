package net.daplumer.data_modification_utils.mod_registries;

import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public final class ArmorMaterialSettings {
    private Float toughness = null;
    private Float knockbackResistance = null;
    private Integer enchantmentValue = null;
    private RegistryEntry<SoundEvent> equipSound = null;
    private Integer durability = null;
    private TagKey<Item> repairIngredient = null;
    private RegistryKey<EquipmentAsset> assetId = null;
    private Map<EquipmentType, Integer> defenceValues = null;


    private ArmorMaterialSettings(){}
    public static @NotNull ArmorMaterialSettings create(){return new ArmorMaterialSettings();}
    public static @NotNull ArmorMaterialSettings copy(@NotNull ArmorMaterial instance){
        ArmorMaterialSettings settings = ArmorMaterialSettings.create()
                .durability(instance.durability())
                .equipSound(instance.equipSound())
                .knockbackResistance(instance.knockbackResistance())
                .enchantmentValue(instance.enchantmentValue())
                .repairIngredient(instance.repairIngredient())
                .toughness(instance.toughness());
        settings.defenceValues = instance.defense();
        return settings;
    }
    public ArmorMaterialSettings durability(int durability) {
        this.durability = durability;
        return this;
    }

    public ArmorMaterialSettings helmetDefence(int defence){
        return setDefenceValue(defence, EquipmentType.HELMET);
    }
    public ArmorMaterialSettings chestplateDefence(int defence){
        return setDefenceValue(defence, EquipmentType.CHESTPLATE);
    }
    public ArmorMaterialSettings leggingsDefence(int defence){
        return setDefenceValue(defence, EquipmentType.LEGGINGS);
    }
    public ArmorMaterialSettings bootsDefence(int defence){
        return setDefenceValue(defence, EquipmentType.BOOTS);
    }
    public ArmorMaterialSettings bodyDefence(int defence){
        return setDefenceValue(defence, EquipmentType.BODY);
    }

    private ArmorMaterialSettings setDefenceValue(int defenceValue, EquipmentType type){
        if (defenceValues == null) {
            defenceValues = new EnumMap<>(EquipmentType.class);
        }
        defenceValues.put(type, defenceValue);
        return this;

    }

    public ArmorMaterialSettings defenceValues(int helmetDefence, int chestplateDefence, int leggingsDefence, int bootsDefence, int bodyDefence){
        return this
                .helmetDefence(helmetDefence)
                .chestplateDefence(chestplateDefence)
                .leggingsDefence(leggingsDefence)
                .bootsDefence(bootsDefence)
                .bodyDefence(bodyDefence);
    }
    public ArmorMaterialSettings equipSound(RegistryEntry<SoundEvent> sound){
        this.equipSound = sound;
        return this;
    }

    public ArmorMaterialSettings toughness(float toughness){
        this.toughness = toughness;
        return this;
    }

    public ArmorMaterialSettings knockbackResistance(float knockbackResistance){
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ArmorMaterialSettings assetId(RegistryKey<EquipmentAsset> assetId){
        this.assetId = assetId;
        return this;
    }
    public ArmorMaterialSettings enchantmentValue(int enchantmentValue){
        this.enchantmentValue = enchantmentValue;
        return this;
    }

    public ArmorMaterial build(){
        return new ArmorMaterial(getDurability(), getDefenceValues(),getEnchantmentValue(),getEquipSound(),getToughness(),getKnockbackResistance(),getRepairIngredient(),getAssetId());
    }

    public ArmorMaterialSettings repairIngredient(TagKey<Item> ingredient){
        this.repairIngredient = ingredient;
        return this;
    }

    public Float getKnockbackResistance() {
        return Objects.requireNonNullElse(knockbackResistance,0f);
    }

    public Float getToughness() {
        return Objects.requireNonNullElse(toughness,0f);
    }

    public Integer getDurability() {
        return Objects.requireNonNullElse(durability,0);
    }

    public Integer getEnchantmentValue() {
        return Objects.requireNonNullElse( enchantmentValue,0);
    }

    public Map<EquipmentType, Integer> getDefenceValues() {
        return defenceValues;
    }

    public RegistryKey<EquipmentAsset> getAssetId() {
        return assetId;
    }

    public RegistryEntry<SoundEvent> getEquipSound() {
        return equipSound;
    }

    public TagKey<Item> getRepairIngredient() {
        return repairIngredient;
    }
}