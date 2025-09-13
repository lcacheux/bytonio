package net.cacheux.bytonio.demo

import bytonio.fromByteArray
import bytonio.getBinarySize
import bytonio.toByteArray
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ComposedObjectTest {
    companion object {
        val testObjectBinary = byteArrayOf(
            0, 0, 0x05, 0x39, // index value = 1337
            0, 0x2a,    // type value = 42
            0, 0, 0, 4, // size of data
            0xff, 0xfe, 0xfb, 0x44  // data content
        )

        val composedObjectBinary = byteArrayOf(
            0, 0, 0, 12, // index
            0, 0, 0, 14, // size of obj
            0, 0, 0x05, 0x39, 0, 0x2a, 0, 0, 0, 4, 0xff, 0xfe, 0xfb, 0x44 // obj content
        )
    }

    @Test
    fun testComposedObject() {
        val testObject = TestObject(1337, 42, byteArrayOf(0xff, 0xfe, 0xfb, 0x44))

        val composedObj = ComposedObject(12, testObject)

        assertEquals(14, testObject.getBinarySize())
        assertEquals(22, composedObj.getBinarySize())

        assertArrayEquals(
            testObjectBinary,
            testObject.toByteArray()
        )

        assertArrayEquals(
            composedObjectBinary,
            composedObj.toByteArray()
        )
    }

    @Test
    fun testComposedObjectDeserialization() {
        val testObject = fromByteArray<TestObject>(testObjectBinary)
        val composedObject = fromByteArray<ComposedObject>(composedObjectBinary)

        assertEquals(1337, testObject.index)
        assertEquals(42.toShort(), testObject.type)
        assertEquals(12.toShort(), composedObject.index)
    }
}
