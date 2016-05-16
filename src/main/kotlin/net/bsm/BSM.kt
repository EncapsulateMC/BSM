package net.bsm

import net.bsm.asm.JarRemapper
import net.bsm.map.*
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import org.apache.commons.io.FileUtils
import java.io.File

class BSM {
    companion object Static {
        @JvmStatic fun main(args: Array<String>) {
            // java -jar BSM.jar apply <originalJarLoc> <binaryMapLoc> <outputFileName>
            if (args.size < 1) {
                println("Insufficent arguments!")
                println("Usage: java -jar BSM.jar <option>")
                return
            }
            if (args[0].equals("apply", true)) {
                if (args.size < 4) {
                    println("Insufficent arguments!")
                    println("Usage: java -jar BSM.jar apply <originalJarLoc> <binaryMapLoc> <outputFileName>")
                    return
                }
                val jarLoc: File = File(args[1])
                val binaryMapLoc: File = File(args[2])
                val outputFileName: String = args[3]

                FileUtils.copyURLToFile(BSM::class.java.classLoader.getResource("Template.jar"), File(System.getProperty("user.dir"), outputFileName))

                val tempDir = File(System.getProperty("user.dir"), "temp")

                if (tempDir.exists()) FileUtils.deleteDirectory(tempDir)
                if (!tempDir.exists()) tempDir.mkdir()

                val remapper: JarRemapper = JarRemapper(jarLoc, binaryMapLoc)
                val byteMap = remapper.remap()
                val zipFile = ZipFile(File(outputFileName))
                for (entry in byteMap.entries) {
                    val outputFile = File(tempDir, "${entry.key}.class")
                    FileUtils.writeByteArrayToFile(outputFile, entry.value)
                }

                val zipParameters = ZipParameters()
                zipParameters.isIncludeRootFolder = false
                zipFile.addFolder(tempDir, zipParameters)
                return
            }
            if (args[0].equals("srg", true)) {
                // java -jar BSM.jar srg <srgLoc> <outputSrgName>
                if (args.size < 3) {
                    println("Insufficent arguments!")
                    println("Usage: java -jar BSM.jar srg <srgLoc> <outputSrgName>")
                    return
                }
                val srgLoc = File(args[1])
                val outputFileName = args[2]
                val outputFile = File(System.getProperty("user.dir"), outputFileName)
                val srgConverter = SRGConverter(srgLoc)
                srgConverter.convertTo(outputFile)
                return
            }
            if (args[0].equals("reverse", true)) {
                // java -jar BSM.jar reverse <oriBSM> <newBSMFileName>
                if (args.size < 3) {
                    println("Insufficent arguments!")
                    println("Usage: java -jar BSM.jar reverse <oriBSM> <newBSMFileName>")
                    return
                }
                val bsmFile = File(args[1])
                val compiler = MappingCompiler(bsmFile, null)
                val model = compiler.decompile()

                val classModels: MutableList<BinaryClass> = mutableListOf()
                for (classModel in model.classModels) {
                    classModels.add(BinaryClass(classModel.deobf, classModel.obf))
                }

                val fieldModels: MutableList<BinaryField> = mutableListOf()
                for (fieldModel in model.fieldModels) {
                    fieldModels.add(BinaryField(fieldModel.originClass, fieldModel.newName, fieldModel.oriName))
                }

                val methodModels: MutableList<BinaryMethod> = mutableListOf()
                for (methodModel in model.methodModels) {
                    methodModels.add(BinaryMethod(remapClassName(model.classModels, methodModel.originClass), methodModel.newName, remapDescriptor(model.classModels, methodModel.oriDesc), methodModel.oriName))
                }

                val outputBSM = File(System.getProperty("user.dir"), args[2])
                val resultCompiler = MappingCompiler(outputBSM, BinaryMapping(classModels.toTypedArray(), methodModels.toTypedArray(), fieldModels.toTypedArray()))
                resultCompiler.compile()
                return
            }
            println("Invalid option!")
            println("Available options: 'apply', 'srg', 'reverse'")
        }

        @JvmStatic fun remapDescriptor(classModels: Array<BinaryClass>, descriptor: String) : String {
            var mutableDescriptor = descriptor
            for (classModel in classModels) {
                mutableDescriptor = mutableDescriptor.replace(classModel.obf, classModel.deobf)
            }
            return mutableDescriptor
        }

        @JvmStatic fun remapClassName(classModels: Array<BinaryClass>, className: String) : String {
            var mutableName = className
            for (classModel in classModels) {
                mutableName = mutableName.replace(classModel.obf, classModel.deobf)
            }
            return mutableName
        }
    }
}