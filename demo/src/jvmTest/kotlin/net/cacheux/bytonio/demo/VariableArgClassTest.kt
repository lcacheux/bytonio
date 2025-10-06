package net.cacheux.bytonio.demo

import bytonio.debugTree
import net.cacheux.bytonio.utils.byteArrayOf
import org.junit.Test

class VariableArgClassTest {
    @Test
    fun testVariableArgClassDebugTree() {
        val var1 = VariableArgClass(12, FirstSerializable(229, byteArrayOf(0xaa, 0xbb, 0xcc)))

        println(var1.debugTree())

        val var2 = VariableArgClass(13, SecondSerializable(997, 780))
        println(var2.debugTree())
    }
}