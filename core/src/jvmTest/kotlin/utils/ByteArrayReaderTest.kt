package utils

import net.cacheux.bytonio.Order
import net.cacheux.bytonio.utils.byteArrayOf
import net.cacheux.bytonio.utils.reader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.test.Test

class ByteArrayReaderTest {
    @Test
    fun testByteArrayReader() {
        val byteArray = byteArrayOf(0x00, 0x00, 0x04, 0xd2, 0x1a, 0x85, 0xff, 0xff, 0xff, 0xff)
        val reader = byteArray.reader()

        assertTrue(reader.hasRemaining())
        assertEquals(10, reader.remaining())

        val intValue = reader.readInt(Order.BIG_ENDIAN)
        val shortValue = reader.readShort(Order.BIG_ENDIAN)

        assertEquals(4, reader.remaining())

        val byteArrayValue = reader.readByteArray(4)

        assertEquals(1234, intValue)
        assertEquals(6789, shortValue)
        assertEquals(byteArrayOf(0xff, 0xff, 0xff, 0xff).toList(), byteArrayValue.toList())

        assertFalse(reader.hasRemaining())
        assertEquals(0, reader.remaining())
    }

    @Test(expected = IllegalStateException::class)
    fun testByteArrayReaderOverflow() {
        val reader = byteArrayOf(0x01, 0x02, 0x03).reader()
        reader.readInt()
    }

}