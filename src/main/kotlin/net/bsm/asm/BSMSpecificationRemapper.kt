package net.bsm.asm

import net.bsm.map.BinaryMapping
import org.objectweb.asm.commons.Remapper

class BSMSpecificationRemapper(val binaryMapping: BinaryMapping) : Remapper() {
    override fun map(typeName: String?): String? {
        var remappedTypeName = typeName
        for (classMap in binaryMapping.classModels) {
            remappedTypeName = remappedTypeName?.replace(classMap.obf, classMap.deobf)
        }
        return remappedTypeName
    }

    override fun mapMethodName(owner: String?, name: String?, desc: String?): String? {
        var remappedFieldName = name
        for (fieldMap in binaryMapping.methodModels) {
            if (fieldMap.originClass.equals(owner) and fieldMap.oriName.equals(name) and fieldMap.oriDesc.equals(desc)) {
                remappedFieldName = remappedFieldName?.replace(fieldMap.oriName, fieldMap.newName)
            }
        }
        return remappedFieldName
    }

    override fun mapFieldName(owner: String?, name: String?, desc: String?): String? {
        var remappedFieldName = name
        for (fieldMap in binaryMapping.fieldModels) {
            if (fieldMap.originClass.equals(owner) and fieldMap.oriName.equals(name)) {
                remappedFieldName = remappedFieldName?.replace(fieldMap.oriName, fieldMap.newName)
            }
        }
        return remappedFieldName
    }
}