package net.bsm.models

import java.io.Serializable

class BinaryMapping(val classModels: Array<BinaryClass>, val methodModels: Array<BinaryMethod>, val fieldModels: Array<BinaryField>) : Serializable {}