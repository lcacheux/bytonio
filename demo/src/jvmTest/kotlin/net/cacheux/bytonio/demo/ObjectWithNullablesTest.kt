package net.cacheux.bytonio.demo

import bytonio.debugTree
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Assert.assertTrue
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

        assertTrue(obj.debugTree().contains("""
ObjectWithNullables:
  index: 0000000C
  data: TestStandardClass:
    index: 00000004
    type: 0005
    data: 111213

  content: 8899AABB
  optionalData: TestObject:
    index: 0000000D
    type: 0025
    data: 2334

  optionalContent: FFFEFDFCFBFA
        """.trimIndent()))
    }

    @Test
    fun testObjectWithNullablesNullValues() {
        val obj = ObjectWithNullables(
            index = 12,
            data = TestStandardClass(4, 5, byteArrayOf(0x11, 0x12, 0x13)),
            content = byteArrayOf(0x88, 0x99, 0xaa, 0xbb)
        )

        assertTrue(obj.debugTree().contains("""
ObjectWithNullables:
  index: 0000000C
  data: TestStandardClass:
    index: 00000004
    type: 0005
    data: 111213

  content: 8899AABB
  optionalData: null
  optionalContent: null
        """.trimIndent()))
    }
}