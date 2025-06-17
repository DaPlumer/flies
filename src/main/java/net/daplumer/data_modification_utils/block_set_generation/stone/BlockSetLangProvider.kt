package net.daplumer.data_modification_utils.block_set_generation.stone

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack.RegistryDependentFactory
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture
val languageFunctionMap by lazy{
    mapOf<String, (key: String, block: Block?) -> String>(
        Pair("en_us"){ key, _ -> snakeCaseToNaturalCaseRegex(key.substringAfterLast('.'))}
    )
}
fun createLangProviders(set: KStoneSet):List<BlockSetLangProviderFactory> = languageFunctionMap.entries.map {
    pair -> BlockSetLangProviderFactory(set,pair)
}
class BlockSetLangProvider(
    val set: KStoneSet,
    language: String,
    val function: (key: String, block: Block?) -> String,
    dataOutput:FabricDataOutput,
    registryLookup:CompletableFuture<RegistryWrapper.WrapperLookup>
    ): FabricLanguageProvider(dataOutput,language, registryLookup){
    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup?,
        translationBuilder: TranslationBuilder
    ) {
        set.StreamBlocks().forEach({
            translationBuilder.add(it, function.invoke(it.translationKey, it))
        })
    }

    override fun getName(): String {
        return super.getName() + " For " + snakeCaseToNaturalCaseRegex(set.name)
    }

}
class BlockSetLangProviderFactory(val set: KStoneSet, val entry: Map.Entry<String, (key: String, block: Block?) -> String>):RegistryDependentFactory<BlockSetLangProvider>{
    override fun create(
        output: FabricDataOutput,
        registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
    ) = BlockSetLangProvider(set,entry.key,entry.value,output, registriesFuture)

}