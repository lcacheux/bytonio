package net.cacheux.bytonio.utils

import net.cacheux.bytonio.Order

fun Int.asByteArray(order: Order = Order.BIG_ENDIAN): ByteArray {
    val result = ByteArray(4)
    when (order) {
        Order.BIG_ENDIAN -> {
            for (i in 0..3) result[i] = (this shr ((3-i)*8)).toByte()
        }
        Order.LITTLE_ENDIAN -> {
            for (i in 0..3) result[i] = (this shr (i*8)).toByte()
        }
    }

    return result
}

fun Short.asByteArray(order: Order = Order.BIG_ENDIAN): ByteArray {
    val result = ByteArray(2)

    when (order) {
        Order.BIG_ENDIAN -> {
            for (i in 0..1) result[i] = (this.toInt() shr ((1-i)*8)).toByte()
        }
        Order.LITTLE_ENDIAN -> {
            for (i in 0..1) result[i] = (this.toInt() shr (i*8)).toByte()
        }
    }
    return result
}

fun ByteArray.readInt(offset: Int = 0, order: Order = Order.BIG_ENDIAN): Int {
    return when (order) {
        Order.BIG_ENDIAN -> {
            (this[offset + 0].toInt() shl 24) or
                    (this[offset + 1].toInt() and 0xff shl 16) or
                    (this[offset + 2].toInt() and 0xff shl 8) or
                    (this[offset + 3].toInt() and 0xff)
        }
        Order.LITTLE_ENDIAN -> {
            (this[offset + 3].toInt() shl 24) or
                    (this[offset + 2].toInt() and 0xff shl 16) or
                    (this[offset + 1].toInt() and 0xff shl 8) or
                    (this[offset + 0].toInt() and 0xff)
        }
    }
}

fun ByteArray.readShort(offset: Int = 0, order: Order = Order.BIG_ENDIAN): Int {
    return when (order) {
        Order.BIG_ENDIAN -> {
            (this[offset].toInt() and 0xff shl 8) or
                    (this[offset + 1].toInt() and 0xff)
        }
        Order.LITTLE_ENDIAN -> {
            (this[offset + 1].toInt() and 0xff shl 8) or
                    (this[offset].toInt() and 0xff)
        }
    }
}

/**
 * Used to avoid writing toByte() on each element.
 */
fun byteArrayOf(vararg elements: Int) = intArrayOf(*elements).map { it.toByte() }.toByteArray()
