package net.cacheux.bytonio.demo

import bytonio.fromByteArray
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomDeserializerTest {
    @Test
    fun testCustomDeserializer() {
        val customObject = CustomObject(index = 42, "abc")

        assertArrayEquals(
            byteArrayOf(0, 42, 3, 'a'.code.toByte(), 'b'.code.toByte(), 'c'.code.toByte()),
            customObject.toByteArray()
        )

        val deserialized = fromByteArray<CustomObject>(
            byteArrayOf(0x05, 0x39, 4, '1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte(), '4'.code.toByte())
        )

        assertEquals(1337, deserialized.index)
        assertEquals("1234", deserialized.content)
    }
}
