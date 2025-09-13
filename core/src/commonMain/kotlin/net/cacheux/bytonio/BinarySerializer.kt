package net.cacheux.bytonio

interface BinarySerializer<T> {
    fun getBinarySize(data: T): Int
    fun toByteArray(data: T): ByteArray
}
