package net.bsm

import net.bsm.asm.JarRemapper
import net.bsm.map.*
import org.apache.commons.io.FileUtils.copyURLToFile
import org.apache.commons.io.FileUtils.writeByteArrayToFile
import java.io.File
import java.net.URL

class DebugMode {
    companion object Static {
        @JvmStatic fun main(args: Array<String>) {
            println("Generating BSM mappings...")
            val classMappings: Array<BinaryClass> = listOf(BinaryClass("org/example/Example", "net/pizzacrust/example/PackageExampleChange")).toTypedArray()
            val methodMappings: Array<BinaryMethod> = listOf(BinaryMethod("org/example/Example", "test", "()Z", "TRUE")).toTypedArray()
            val fieldMappings: Array<BinaryField> = listOf(BinaryField("org/example/Test", "fieldUpdates", "doesFieldUpd")).toTypedArray()
            val mappingCentral: BinaryMapping = BinaryMapping(classMappings, methodMappings, fieldMappings)
            val outputBSMFile = File(System.getProperty("user.dir"), "hw.bsm")
            val compiler = MappingCompiler(outputBSMFile, mappingCentral)
            compiler.compile()
            println("Copying test assets...")
            val url: URL = DebugMode::class.java.classLoader.getResource("HelloWorld.jar")
            val outputJar: File = File(System.getProperty("user.dir"), "HelloWorld.jar")
            copyURLToFile(url, outputJar)
            println("Applying generated binary mappings...")
            val jarRemapper = JarRemapper(outputJar, outputBSMFile)
            val map = jarRemapper.remap()
            println("Outputting results...")
            var currentIndex = 1
            for (arrays in map.values) {
                val outputFile = File(System.getProperty("user.dir"), "$currentIndex.class")
                writeByteArrayToFile(outputFile, arrays)
                currentIndex++
            }
        }
    }
}