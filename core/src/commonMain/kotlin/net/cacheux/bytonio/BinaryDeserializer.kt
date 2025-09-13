package net.cacheux.bytonio

import net.cacheux.bytonio.utils.ByteArrayReader

interface BinaryDeserializer<T> {
    fun fromByteArray(byteArray: ByteArray): T
    fun fromByteArrayReader(reader: ByteArrayReader): T
}
