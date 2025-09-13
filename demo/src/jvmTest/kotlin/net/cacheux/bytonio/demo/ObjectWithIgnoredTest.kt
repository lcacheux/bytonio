package net.cacheux.bytonio.demo

import bytonio.fromByteArray
import bytonio.toByteArray
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectWithIgnoredTest {

    private val contentArray = byteArrayOf(
        0x00, 0x2a,
        0xaa, 0xbb, 0xcc, 0xdd
    )

    @Test
    fun testObjectWithIgnoredSerialization() {
        val obj = ObjectWithIgnored(
            index = 42,
            data = 4,
            content = byteArrayOf(0xaa, 0xbb, 0xcc, 0xdd)
        )

        assertArrayEquals(contentArray, obj.toByteArray())
    }

    @Test
    fun testObjectWithIgnoredDeserialization() {
        val obj = fromByteArray<ObjectWithIgnored>(contentArray)
        assertEquals(42, obj.index)
        assertEquals(-1, obj.data)
        assertArrayEquals(byteArrayOf(0xaa, 0xbb, 0xcc, 0xdd), obj.content)
    }
}