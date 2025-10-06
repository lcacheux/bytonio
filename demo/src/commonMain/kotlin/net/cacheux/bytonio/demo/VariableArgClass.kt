package net.cacheux.bytonio.demo

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.IgnoreEncoding

/**
 * An example of data class with a parameter that can be any type of BinarySerializable.
 */
@DataObject
data class VariableArgClass(
    val index: Int,
    @IgnoreEncoding val content: BinarySerializable? = null
)
