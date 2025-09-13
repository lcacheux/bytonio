package net.cacheux.bytonio.demo

import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsData
import net.cacheux.bytonio.annotations.EncodeAsShort

@DataObject
class TestStandardClass(
    val index: Int,
    @EncodeAsShort val type: Int,
    @EncodeAsData(format = DataFormat.FIXED, size = 10) val data: ByteArray
)
