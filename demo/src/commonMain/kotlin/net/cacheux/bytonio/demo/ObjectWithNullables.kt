package net.cacheux.bytonio.demo

import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.annotations.DataObject

@DataObject(dataSizeFormat = DataSizeFormat.SHORT)
data class ObjectWithNullables(
    val index: Int,
    val data: TestStandardClass,
    val content: ByteArray,
    val optionalData: TestObject? = null,
    val optionalContent: ByteArray? = null
)
