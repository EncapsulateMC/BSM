package net.bsm.map

import java.io.*

class MappingCompiler(val outputBinaryFile: File, val mappingObj: BinaryMapping?) {
    fun compile() {
        val fileStream: FileOutputStream = FileOutputStream(outputBinaryFile)
        val objectStream: ObjectOutputStream = ObjectOutputStream(fileStream)
        objectStream.writeObject(mappingObj)
        objectStream.close()
        fileStream.close()
    }
    fun decompile() : BinaryMapping {
        val inputStream: FileInputStream = FileInputStream(outputBinaryFile)
        val objectStream: ObjectInputStream = ObjectInputStream(inputStream)
        val binaryMapping: BinaryMapping = objectStream.readObject() as BinaryMapping
        objectStream.close()
        inputStream.close()
        return binaryMapping
    }
}