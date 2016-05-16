package net.bsm.models

import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class MappingCompiler(val outputBinaryFile: File, val mappingObj: BinaryMapping) {
    fun compile() {
        val fileStream: FileOutputStream = FileOutputStream(outputBinaryFile)
        val objectStream: ObjectOutputStream = ObjectOutputStream(fileStream)
        objectStream.writeObject(mappingObj)
        objectStream.close()
        fileStream.close()
    }
}