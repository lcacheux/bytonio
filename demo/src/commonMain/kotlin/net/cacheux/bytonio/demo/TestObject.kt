package net.cacheux.bytonio.demo

import net.cacheux.bytonio.annotations.DataObject

@DataObject
data class TestObject(
    val index: Int,
    val type: Short,
    val data: ByteArray
)
