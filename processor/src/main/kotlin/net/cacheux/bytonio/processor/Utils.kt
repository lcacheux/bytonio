@file:OptIn(KspExperimental::class)

package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsByte
import net.cacheux.bytonio.annotations.EncodeAsData
import net.cacheux.bytonio.annotations.EncodeAsInt
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.annotations.IgnoreEncoding
import net.cacheux.bytonio.annotations.Ordered

const val BYTONIO_PACKAGE_NAME = "bytonio"


fun KSClassDeclaration.haveMethod(name: String) =
    getDeclaredFunctions().any { it.simpleName.asString() == name }

@Throws(IllegalStateException::class)
fun KSClassDeclaration.parseConstructor(lambda: (KSFunctionDeclaration) -> FunSpec) =
    this.primaryConstructor?.let(lambda)
        ?: throw IllegalStateException("Class must have a constructor with parameters: ${toClassName().simpleName}")

enum class EncodedType {
    INT, SHORT, BYTE, BYTE_ARRAY, OBJECT
}

fun KSValueParameter.encodedType(): EncodedType {
    return if (type.resolve().makeNotNullable().toClassName() == ByteArray::class.asClassName()) {
        EncodedType.BYTE_ARRAY
    } else when {
        encodedAsInt() -> EncodedType.INT
        encodedAsShort() -> EncodedType.SHORT
        encodedAsByte() -> EncodedType.BYTE
        else -> EncodedType.OBJECT
    }
}

fun KSValueParameter.dataFormat() = if (isAnnotationPresent(EncodeAsData::class)) {
    getAnnotationsByType(EncodeAsData::class).first().format
} else DataFormat.DYNAMIC

fun KSValueParameter.byteArrayFixedSize() = if (isAnnotationPresent(EncodeAsData::class)) {
    getAnnotationsByType(EncodeAsData::class).first().size
} else -1

fun KSValueParameter.byteArraySizeFormat() = if (isAnnotationPresent(EncodeAsData::class)) {
    getAnnotationsByType(EncodeAsData::class).first().sizeFormat
} else DataSizeFormat.INHERIT

/**
 * Return the byteArraySizeFormat from the parameter or from the parent class.
 */
fun KSValueParameter.effectiveByteArraySizeFormat(annotation: DataObject) = if (byteArraySizeFormat() == DataSizeFormat.INHERIT) {
    annotation.getDataSizeFormat()
} else byteArraySizeFormat()

fun KSValueParameter.stringName() = name?.asString() ?: ""

fun KSValueParameter.isNullable() = type.resolve().isMarkedNullable

fun KSValueParameter.encodedAsInt(): Boolean {
    return if (type.resolve().toClassName() == Int::class.asClassName()) {
        !isAnnotationPresent(EncodeAsShort::class) and !isAnnotationPresent(EncodeAsByte::class)
    } else isAnnotationPresent(EncodeAsInt::class)
}

fun KSValueParameter.encodedAsShort(): Boolean {
    return (
            (type.resolve().toClassName() == Short::class.asClassName()) and
                    !isAnnotationPresent(EncodeAsByte::class) and
                    !isAnnotationPresent(EncodeAsInt::class)
            ) or isAnnotationPresent(EncodeAsShort::class)
}

fun KSValueParameter.encodedAsByte(): Boolean {
    return (
            ((type.resolve().toClassName() == Byte::class.asClassName()) and
                    !isAnnotationPresent(EncodeAsInt::class) and
                    !isAnnotationPresent(EncodeAsShort::class))
            ) or isAnnotationPresent(EncodeAsByte::class)
}

fun KSValueParameter.byteOrder(dataObjectAnnotation: DataObject): Order {
    return if (isAnnotationPresent(Ordered::class)) {
        getAnnotationsByType(Ordered::class).first().order
    } else dataObjectAnnotation.order
}

fun KSValueParameter.checkIfIgnored(): Boolean {
    return if (isAnnotationPresent(IgnoreEncoding::class)) {
        if (!hasDefault)
            throw IllegalArgumentException(
                "Parameter ${name?.asString()} with IgnoreEncoding annotation must have a default value"
            )
        true
    } else false
}

fun KSClassDeclaration.getDeserializerKsType(): KSType? {
    val annotation = annotations
        .firstOrNull { it.shortName.asString() == "Deserializer" }

    return annotation?.arguments
        ?.firstOrNull { it.name?.asString() == "deserializer" }
        ?.value as? KSType
}

fun KSClassDeclaration.getSerializerKsType(): KSType? {
    val annotation = annotations
        .firstOrNull { it.shortName.asString() == "Serializer" }

    return annotation?.arguments
        ?.firstOrNull { it.name?.asString() == "serializer" }
        ?.value as? KSType
}

fun DataSizeFormat.size() = when {
    this == DataSizeFormat.INT -> 4
    this == DataSizeFormat.SHORT -> 2
    this == DataSizeFormat.BYTE -> 1
    else -> 0
}

fun DataObject.getDataSizeFormat() =
    if (dataSizeFormat == DataSizeFormat.INHERIT) {
        bytonioOptions.defaultDataSizeFormat
    } else {
        dataSizeFormat
    }
