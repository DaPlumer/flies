package net.daplumer.data_modification_utils.mod_registries;

import kotlin.jvm.functions.Function1;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import static net.minecraft.stat.Stats.CUSTOM;

public class ModStatTypeRegisterer extends ModDataRegisterer<Stat<Identifier>, StatFormatter, Identifier> {
    public ModStatTypeRegisterer(@NotNull String namespace) {
        super(namespace);
    }

    /**
     * @return an instance of the data identified by the Identifier given. In the instance of the {@link ModStatTypeRegisterer}, this returns a {@link Stat} with an identifier type
     * @see Registerer
     */
    @Override
    public Stat<Identifier> getInstance(@NotNull Identifier identifier) {
        return CUSTOM.getOrCreateStat(identifier);
    }

    /**
     * given an {@link Identifier}, return a registry key
     * @see ModStatTypeRegisterer
     */
    @Override
    public @NotNull RegistryKey<Identifier> getRegistryKey(@NotNull Identifier identifier) {
        return RegistryKey.of(RegistryKeys.CUSTOM_STAT,identifier);
    }

    /**
     * generate a statistic with a custom {@code name}.
     * for more information, see {@link ModDataRegisterer#register(String)}
     * in {@link ModDataRegisterer}.
     *
     * @see ModDataRegisterer
     * @see ModDataRegisterer#register(String)
     * @see Stat
     * @see Registries
     */
    @Override
    public <U extends Stat<Identifier>> U register(@NotNull String name, @Nullable StatFormatter instanceSettings, @Nullable Function1<? super StatFormatter, ? extends Stat<Identifier>> instanceFactory) {
        Identifier identifier = getIdentifier(name);
        Registry.register(Registries.CUSTOM_STAT, name, identifier);
        //noinspection unchecked
        return (U) CUSTOM.getOrCreateStat(identifier, Objects.requireNonNullElse(instanceSettings,StatFormatter.DEFAULT));
    }
}
