package net.cacheux.bytonio.demo

import bytonio.FirstSerializableSerializer
import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject

@DataObject
data class FirstSerializable(
    val count: Int,
    val value: ByteArray
): BinarySerializable {
    override fun getBinarySize() = FirstSerializableSerializer.getBinarySize(this)

    override fun toByteArray() = FirstSerializableSerializer.toByteArray(this)
}
