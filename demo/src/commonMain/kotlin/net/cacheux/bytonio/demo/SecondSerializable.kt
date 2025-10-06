package net.cacheux.bytonio.demo

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.utils.ByteArrayWriter

@DataObject
data class SecondSerializable(
    val size: Int,
    val data: Int
): BinarySerializable {
    override fun getBinarySize() = 8

    override fun toByteArray(): ByteArray {
        return ByteArrayWriter(ByteArray(getBinarySize())).apply {
            writeInt(size)
            writeInt(data)
        }.byteArray
    }
}
