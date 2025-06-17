package net.daplumer.flies.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public record FlyTrapFeatureConfig(IntProvider height) implements FeatureConfig {
    public static final Codec<FlyTrapFeatureConfig> CODEC = RecordCodecBuilder.create(
            (instance -> instance.group(
                    IntProvider.POSITIVE_CODEC.fieldOf("height").forGetter(FlyTrapFeatureConfig::height)
            ).apply(instance,FlyTrapFeatureConfig::new))
    );
}
