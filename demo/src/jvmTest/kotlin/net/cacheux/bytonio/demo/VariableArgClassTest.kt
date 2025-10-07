package net.cacheux.bytonio.demo

import bytonio.debugTree
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Assert.assertTrue
import org.junit.Test

class VariableArgClassTest {
    @Test
    fun testVariableArgClassDebugTree() {
        val var1 = VariableArgClass(12, FirstSerializable(229, byteArrayOf(0xaa, 0xbb, 0xcc)))

        assertTrue(var1.debugTree().contains("""
VariableArgClass:
  index: 0000000C
  content: FirstSerializable:
    count: 000000E5
    value: AABBCC
""".trimIndent()))

        val var2 = VariableArgClass(13, SecondSerializable(997, 780))

        assertTrue(var2.debugTree().contains("""
VariableArgClass:
  index: 0000000D
  content: SecondSerializable:
    size: 000003E5
    data: 0000030C
        """.trimIndent()))
    }
}