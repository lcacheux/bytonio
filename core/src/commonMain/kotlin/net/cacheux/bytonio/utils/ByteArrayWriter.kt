package net.cacheux.bytonio.utils

import net.cacheux.bytonio.Order

/**
 * Utility class to write to a ByteArray sequentially.
 */
class ByteArrayWriter(
    val byteArray: ByteArray
) {
    private var position: Int = 0

    fun writeInt(value: Int, order: Order = Order.BIG_ENDIAN) {
        checkRemainingSpace(4)
        value.asByteArray(order).copyInto(byteArray, position)
        position += 4
    }

    fun writeShort(value: Short, order: Order = Order.BIG_ENDIAN) {
        checkRemainingSpace(2)
        value.asByteArray(order).copyInto(byteArray, position)
        position += 2
    }

    fun writeByte(value: Byte) {
        checkRemainingSpace(1)
        byteArray[position] = value
        position++
    }

    fun writeByteArray(value: ByteArray) {
        checkRemainingSpace(value.size)
        value.copyInto(byteArray, position)
        position += value.size
    }

    fun hasRemaining() = position < byteArray.size

    fun remaining(): Int {
        val result = byteArray.size - position
        return if (result > 0) result else 0
    }

    @Throws(IllegalStateException::class)
    private fun checkRemainingSpace(required: Int) {
        if (position + required > byteArray.size)
            throw IllegalStateException("Not enough space in ByteArray")
    }
}

fun ByteArray.writer() = ByteArrayWriter(this)
