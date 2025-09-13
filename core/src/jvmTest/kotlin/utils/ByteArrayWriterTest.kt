package utils

import net.cacheux.bytonio.utils.byteArrayOf
import net.cacheux.bytonio.utils.writer
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ByteArrayWriterTest {
    @Test
    fun testByteArrayWriter() {
        val byteArray = ByteArray(10)
        val writer = byteArray.writer()

        assertTrue(writer.hasRemaining())
        assertEquals(10, writer.remaining())

        writer.writeInt(1234)
        writer.writeShort(6789)

        assertTrue(writer.hasRemaining())
        assertEquals(4, writer.remaining())

        writer.writeByteArray(byteArrayOf(0xff, 0xff, 0xff, 0xff))

        assertFalse(writer.hasRemaining())
        assertEquals(0, writer.remaining())

        assertArrayEquals(byteArrayOf(0x00, 0x00, 0x04, 0xd2, 0x1a, 0x85, 0xff, 0xff, 0xff, 0xff), byteArray)
    }

    @Test(expected = IllegalStateException::class)
    fun testByteArrayWriterOverwrite() {
        val writer = ByteArray(10).writer()

        writer.writeInt(1)
        writer.writeInt(2)
        writer.writeInt(3)
    }
}
