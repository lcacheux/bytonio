package net.cacheux.bytonio.demo

import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsInt

@DataObject
data class ComposedObject(
    @EncodeAsInt val index: Short,
    val obj: TestObject
)
