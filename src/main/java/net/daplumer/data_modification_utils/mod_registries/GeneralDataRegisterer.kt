package net.daplumer.data_modification_utils.mod_registries

import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import java.util.*

open class GeneralDataRegisterer<T,S>(
    val registry: Registry<T>,
    namespace: String,
    private val registryKeyApplier: ((S, RegistryKey<T>) -> S)?,
    private val defaultInstanceFactory: (S)->T,
    val defaultSettingsFactory: ()->S
): ModDataRegisterer<T, S, T>(namespace){

    override fun getRegistryKey(identifier: Identifier): RegistryKey<T> = RegistryKey.of(registry.key, identifier)
    override fun getInstance(identifier: Identifier): T? = registry.get(identifier)
    override fun <U : T> register(name: String, instanceSettings: S?, instanceFactory: ((S) -> T)?): U {
        val key:RegistryKey<T> = getRegistryKey(name)
        val settings:S =
            Objects.requireNonNullElse(registryKeyApplier, { settings: S, _: RegistryKey<T> -> settings })
                ?.let { it(Objects.requireNonNullElse(instanceSettings, defaultSettingsFactory())!!,key) }!!
        val factory:((S) -> T) = Objects.requireNonNullElse(instanceFactory,defaultInstanceFactory)!!
        @Suppress("UNCHECKED_CAST")
        return Registry.register(registry,key,factory(settings)) as U
    }

}
