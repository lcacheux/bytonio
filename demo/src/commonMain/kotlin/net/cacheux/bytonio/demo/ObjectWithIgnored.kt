package net.cacheux.bytonio.demo

import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsData
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.annotations.IgnoreEncoding

@DataObject
data class ObjectWithIgnored(
    @EncodeAsShort val index: Int,
    @IgnoreEncoding val data: Int = -1,
    @EncodeAsData(format = DataFormat.FIXED, size = 4) val content: ByteArray
)
