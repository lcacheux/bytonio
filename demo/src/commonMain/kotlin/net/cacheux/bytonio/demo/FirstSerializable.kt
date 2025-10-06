package net.cacheux.bytonio.demo

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject

@DataObject
data class FirstSerializable(
    val count: Int,
    val value: ByteArray
): BinarySerializable {
    override fun getBinarySize(): Int {
        TODO("Not yet implemented")
    }

    override fun toByteArray(): ByteArray {
        TODO("Not yet implemented")
    }
}
