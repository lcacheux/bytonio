@file:OptIn(KspExperimental::class)

package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.annotations.Serializer

fun buildGetBinarySizeExtension(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    val serializerClassName = if (clazz.isAnnotationPresent(Serializer::class)) {
        clazz.getSerializerKsType()!!.toClassName()
    } else {
        clazz.serializerClassName()
    }

    return FunSpec.builder(GET_BINARY_SIZE_METHOD_NAME)
        .receiver(clazz.toClassName())
        .returns(Int::class)
        .addStatement("return %T.$GET_BINARY_SIZE_METHOD_NAME(this)", serializerClassName)
        .build()
}

fun buildToByteArrayExtension(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    val serializerClassName = if (clazz.isAnnotationPresent(Serializer::class)) {
        clazz.getSerializerKsType()!!.toClassName()
    } else {
        clazz.serializerClassName()
    }

    return FunSpec.builder(TO_BYTE_ARRAY_METHOD_NAME)
        .receiver(clazz.toClassName())
        .returns(ByteArray::class)
        .addStatement("return %T.$TO_BYTE_ARRAY_METHOD_NAME(this)", serializerClassName)
        .build()
}

fun buildExtensions(types: Sequence<KSClassDeclaration>, logger: KSPLogger): FileSpec {
    val fileBuilder = FileSpec
        .builder(bytonioOptions.packageName, "BytonioExtensions")
        .addFunctions(
            types.mapNotNull {
                if (it.haveMethod(GET_BINARY_SIZE_METHOD_NAME))
                    null
                else
                    buildGetBinarySizeExtension(it, logger)
            }.toList()
        )
        .addFunctions(
            types.mapNotNull {
                if (it.haveMethod(TO_BYTE_ARRAY_METHOD_NAME))
                    null
                else
                    buildToByteArrayExtension(it, logger)
            }.toList()
        )

    return fileBuilder.build()
}
