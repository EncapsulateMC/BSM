package net.bsm

import com.google.common.collect.BiMap
import net.bsm.map.*
import org.spongepowered.asm.obfuscation.SrgContainer
import org.spongepowered.asm.obfuscation.SrgField
import org.spongepowered.asm.obfuscation.SrgMethod
import java.io.File

class SRGConverter(val srgFile: File) {
    fun convertTo(outputFile: File) {
        val container = SrgContainer()
        container.readSrg(srgFile)
        val classMap = getClassMap(container)
        val classList: MutableList<BinaryClass> = mutableListOf()
        for (entry in classMap.entries) {
            println("Processing SRG mapping: CLASS ${entry.key} to CLASS ${entry.value}...")
            classList.add(BinaryClass(entry.key, entry.value))
        }
        val fieldMap = getFieldMap(container)
        val fieldList: MutableList<BinaryField> = mutableListOf()
        for (entry in fieldMap.entries) {
            println("Processing SRG mapping: FIELD ${entry.key.owner}/${entry.key.name} to FIELD ${entry.value.owner}/${entry.value.name}...")
            fieldList.add(BinaryField(entry.key.owner, entry.key.name, entry.value.name))
        }
        val methodMap = getMethodMap(container)
        val methodList: MutableList<BinaryMethod> = mutableListOf()
        for (entry in methodMap.entries) {
            println("Processing SRG mapping: METHOD ${entry.key.owner}/${entry.key.simpleName} to METHOD ${entry.value.owner}/${entry.value.simpleName}...")
            methodList.add(BinaryMethod(entry.key.owner, entry.key.simpleName, entry.key.desc, entry.value.simpleName))
        }
        val mapping = BinaryMapping(classList.toTypedArray(), methodList.toTypedArray(), fieldList.toTypedArray())
        val compiler = MappingCompiler(outputFile, mapping)
        compiler.compile()
    }

    private fun getClassMap(container: SrgContainer) : BiMap<String, String> {
        val srgContainerClass = SrgContainer::class.java
        val field = srgContainerClass.getDeclaredField("classMap");
        field.isAccessible = true
        return field.get(container) as BiMap<String, String>
    }

    private fun getFieldMap(container: SrgContainer) : BiMap<SrgField, SrgField> {
        val srgContainerClass = SrgContainer::class.java
        val field = srgContainerClass.getDeclaredField("fieldMap");
        field.isAccessible = true
        return field.get(container) as BiMap<SrgField, SrgField>
    }

    private fun getMethodMap(container: SrgContainer) : BiMap<SrgMethod, SrgMethod> {
        val srgContainerClass = SrgContainer::class.java
        val field = srgContainerClass.getDeclaredField("methodMap")
        field.isAccessible =  true
        return field.get(container) as BiMap<SrgMethod, SrgMethod>
    }
}