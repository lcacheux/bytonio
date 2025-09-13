package net.cacheux.bytonio.utils

import net.cacheux.bytonio.Order

/**
 * Utility class to read content of a ByteArray sequentially.
 */
class ByteArrayReader(
    val byteArray: ByteArray
) {
    private var position: Int = 0

    fun readInt(order: Order = Order.BIG_ENDIAN): Int {
        checkRemainingData(4)
        val value = byteArray.readInt(position, order)
        position += 4
        return value
    }

    fun readShort(order: Order = Order.BIG_ENDIAN): Int {
        checkRemainingData(2)
        val value = byteArray.readShort(position, order)
        position += 2
        return value
    }

    fun readByte(): Byte {
        checkRemainingData(1)
        val value = byteArray[position]
        position++
        return value
    }

    fun readByteArray(length: Int): ByteArray {
        checkRemainingData(length)
        val value = byteArray.copyOfRange(position, position + length)
        position += length
        return value
    }

    fun hasRemaining() = position < byteArray.size

    fun remaining(): Int {
        val result = byteArray.size - position
        return if (result > 0) result else 0
    }

    @Throws(IllegalStateException::class)
    private fun checkRemainingData(required: Int) {
        if (position + required > byteArray.size)
            throw IllegalStateException("Not enough data in ByteArray")
    }
}

fun ByteArray.reader() = ByteArrayReader(this)