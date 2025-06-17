package net.daplumer.data_modification_utils.mod_registries

import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

/**
 * The base class for data registering. This was written in Kotlin to reduce boilerplate for this specific class
 * @param T The type of data being registered
 * @param S The settings class, for example, Item.Settings
 * @param V Rhe registry key type. For example, in the Item registerer, V is the class "Item"
 * @see Registerer
 * @see GeneralDataRegisterer
 * @see ModStatTypeRegisterer
 * @see ModArmorMaterialRegisterer
 * @see ModEntityTypeRegisterer
 */
@Suppress("unused")
abstract class ModDataRegisterer<T, S, V>(private val namespace:String) {
    fun getNamespace():String = namespace
    /**
     * @return A Registry key defined by an Identifier
     * @throws UnsupportedOperationException for specific cases when registry keys aren't used for registration
     * @see RegistryKey
     * @see Registerer
     * @see ModEntityTypeRegisterer
     * @see ModDataRegisterer
     */
    abstract fun getRegistryKey(identifier: Identifier): RegistryKey<V>

    /**
     * @return The data instance that was registered under the given Identifier
     * @see ModEntityTypeRegisterer An example of what a normal getInstance function would look like
     * @see ModStatTypeRegisterer A way that this function can be different from the normal implementation
     * @see ModArmorMaterialRegisterer Another way that this function can be different from the normal implementation
     * @see ModDataRegisterer
     */
    abstract fun getInstance(identifier: Identifier): T?

    /**
     * This is the function that registers data under a name, Settings, and the way to turn the settings into a data instance
     * @param name the name of the data instance being registered
     * @param instanceSettings the settings used for the data instance being registered, some implementations require non-null settings, like the Block entity type registerer
     * @param instanceFactory the way that settings are turned into the data instance. Again, nullable for most registerers, but the ModEntityTypeRegisterer is an exception
     * @see Registerer.BLOCK_ENTITY_TYPES
     * @see ModEntityTypeRegisterer
     * @see Registerer
     * @see ModDataRegisterer
     */
    abstract fun <U:T> register(name: String, instanceSettings: S?, instanceFactory: ((S)->T)?): U

    /**
     * @return the identifer for the given name assuming a specific namespace
     * @see Identifier
     * @see ModDataRegisterer
     */
    fun getIdentifier(name: String): Identifier = Identifier.of(namespace, name)


    /**
     * @return The data instance that was registered under the given Identifier
     * @see getInstance
     * @see ModEntityTypeRegisterer An example of what a normal getInstance function would look like
     * @see ModStatTypeRegisterer A way that this function can be different from the normal implementation
     * @see ModArmorMaterialRegisterer Another way that this function can be different from the normal implementation
     * @see ModDataRegisterer
     */
    fun getInstance(name: String): T? = getInstance(getIdentifier(name))
    fun getRegistryKey(name: String): RegistryKey<V> = getRegistryKey(getIdentifier(name))
    fun <U:T> register(name: String, instanceSettings: S?): U = this.register(name, instanceSettings, null)
    fun <U:T> register(name: String): U = this.register(name, null as S?, null)
    fun <U:T> register(name: String, instanceFactory: ((S) -> U)?): U = register(name, null, instanceFactory)
    fun <U:T> register(name: String, instanceFactory: ((S) -> U)?, instanceSettings: S): U = this.register(name, instanceSettings, instanceFactory)
}
