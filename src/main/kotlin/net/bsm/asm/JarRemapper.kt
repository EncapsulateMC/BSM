package net.bsm.asm

import net.bsm.map.MappingCompiler
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.FileHeader
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarFile

class JarRemapper(val jar: File, val bsmFile: File) {
    private fun classes() : Array<String> {
        val list: MutableList<String> = mutableListOf()
        val zipFile: ZipFile = ZipFile(jar)
        val fileHeaderList: List<FileHeader> = zipFile.fileHeaders as List<FileHeader>
        for (fileHeader in fileHeaderList) {
            if (fileHeader.fileName.endsWith(".class")) {
                list.add(FilenameUtils.removeExtension(fileHeader.fileName))
            }
        }
        return list.toTypedArray()
    }

    private fun getClassBytes(className: String): ByteArray? {
        val jarFileObj = JarFile(jar)
        val input = jarFileObj.getInputStream(jarFileObj.getJarEntry("$className.class"))
        return IOUtils.toByteArray(input)
    }

    fun remap() : Map<String, ByteArray> {
        val map: MutableMap<String, ByteArray> = mutableMapOf()
        val classes = classes()
        for (className in classes) {
            var classReader = ClassReader(getClassBytes(className))
            var classNode = ClassNode()
            var mappingComplier = MappingCompiler(bsmFile, null)
            var classRemapper = ClassRemapper(classNode, BSMSpecificationRemapper(mappingComplier.decompile()))
            classReader.accept(classRemapper, 0)
            var classWriter = ClassWriter(0)
            classNode.accept(classWriter)
            map.put(className, classWriter.toByteArray())
        }
        return map
    }
}