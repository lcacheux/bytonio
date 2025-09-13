package net.cacheux.bytonio

interface BinarySerializable {
    fun getBinarySize(): Int
    fun toByteArray(): ByteArray
}
