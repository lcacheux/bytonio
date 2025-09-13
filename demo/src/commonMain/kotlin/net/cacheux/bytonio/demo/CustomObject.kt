package net.cacheux.bytonio.demo

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.BinarySerializer
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.Serializer
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.ByteArrayWriter

@DataObject
@Serializer(CustomSerializer::class)
@Deserializer(CustomDeserializer::class)
data class CustomObject(
    val index: Int,
    val content: String
) {
    //fun getBinarySize() = CustomSerializer.getBinarySize(this)
    fun toByteArray() = CustomSerializer.toByteArray(this)
}

object CustomSerializer: BinarySerializer<CustomObject> {
    override fun getBinarySize(data: CustomObject) = 2 + 1 + data.content.length

    override fun toByteArray(data: CustomObject): ByteArray {
        return ByteArrayWriter(ByteArray(getBinarySize(data))).apply {
            writeShort(data.index.toShort())
            writeByte(data.content.length.toByte())
            writeByteArray(data.content.encodeToByteArray())
        }.byteArray
    }
}

object CustomDeserializer: BinaryDeserializer<CustomObject> {
    override fun fromByteArray(byteArray: ByteArray): CustomObject {
        return fromByteArrayReader(ByteArrayReader(byteArray))
    }

    override fun fromByteArrayReader(reader: ByteArrayReader): CustomObject {
        return CustomObject(
            index = reader.readShort(),
            content = reader.readByteArray(reader.readByte().toInt()).decodeToString()
        )
    }
}
