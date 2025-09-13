package utils

import net.cacheux.bytonio.Order
import net.cacheux.bytonio.utils.asByteArray
import net.cacheux.bytonio.utils.byteArrayOf
import net.cacheux.bytonio.utils.readInt
import net.cacheux.bytonio.utils.readShort
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import kotlin.test.Test

class ByteUtilsTest {
    @Test
    fun testIntAsByteArray() {
        assertArrayEquals(byteArrayOf(0, 0, 0, 54), 54.asByteArray())
        assertArrayEquals(byteArrayOf(54, 0, 0, 0), 54.asByteArray(Order.LITTLE_ENDIAN))
        assertArrayEquals(byteArrayOf(0x02, 0x29, 0xdf, 0xc7), 36298695.asByteArray())
        assertArrayEquals(byteArrayOf(0xc7, 0xdf, 0x29, 0x02), 36298695.asByteArray(Order.LITTLE_ENDIAN))
    }

    @Test
    fun testShortAsByteArray() {
        assertArrayEquals(byteArrayOf(0, 54), 54.toShort().asByteArray())
        assertArrayEquals(byteArrayOf(54, 0), 54.toShort().asByteArray(Order.LITTLE_ENDIAN))
        assertArrayEquals(byteArrayOf(0x0b, 0x4f), 2895.toShort().asByteArray())
        assertArrayEquals(byteArrayOf(0x4f, 0x0b), 2895.toShort().asByteArray(Order.LITTLE_ENDIAN))
    }

    @Test
    fun testReadInt() {
        assertEquals(54, byteArrayOf(0, 0, 0, 54).readInt())
        assertEquals(54, byteArrayOf(54, 0, 0, 0).readInt(order = Order.LITTLE_ENDIAN))
        assertEquals(54, byteArrayOf(1, 1, 0, 0, 0, 54).readInt(2))
        assertEquals(36298695, byteArrayOf(0x02, 0x29, 0xdf, 0xc7).readInt())
        assertEquals(36298695, byteArrayOf(0xc7, 0xdf, 0x29, 0x02).readInt(order = Order.LITTLE_ENDIAN))
    }

    @Test
    fun testReadShort() {
        assertEquals(54, byteArrayOf(0, 54).readShort())
        assertEquals(54, byteArrayOf(54, 0).readShort(order = Order.LITTLE_ENDIAN))
        assertEquals(54, byteArrayOf(1, 1, 0, 54).readShort(2))
        assertEquals(2895, byteArrayOf(0x0b, 0x4f).readShort())
        assertEquals(2895, byteArrayOf(0x4f, 0x0b).readShort(order = Order.LITTLE_ENDIAN))
    }
}
