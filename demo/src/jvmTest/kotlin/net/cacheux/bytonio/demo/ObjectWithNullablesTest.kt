package net.cacheux.bytonio.demo

import bytonio.debugTree
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Test

class ObjectWithNullablesTest {
    @Test
    fun testObjectWithNullablesNonNullValues() {
        val obj = ObjectWithNullables(
            index = 12,
            data = TestStandardClass(4, 5, byteArrayOf(0x11, 0x12, 0x13)),
            content = byteArrayOf(0x88, 0x99, 0xaa, 0xbb),
            optionalData = TestObject(13, 37, byteArrayOf(0x23, 0x34)),
            optionalContent = byteArrayOf(0xff, 0xfe, 0xfd, 0xfc, 0xfb, 0xfa)
        )
    }

    @Test
    fun testObjectWithNullablesNullValues() {
        val obj = ObjectWithNullables(
            index = 12,
            data = TestStandardClass(4, 5, byteArrayOf(0x11, 0x12, 0x13)),
            content = byteArrayOf(0x88, 0x99, 0xaa, 0xbb)
        )

        println(obj.debugTree())
    }
}