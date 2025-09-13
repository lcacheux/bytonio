@file:OptIn(KspExperimental::class)

package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.BinarySerializer
import net.cacheux.bytonio.annotations.Serializer

fun KSClassDeclaration.serializerClass() = "${this.simpleName.asString()}Serializer"
fun KSClassDeclaration.serializerClassName() = ClassName(
    bytonioOptions.packageName,
    "${this.simpleName.asString()}Serializer"
)

fun buildSerializers(
    types: Sequence<KSClassDeclaration>,
    logger: KSPLogger
): FileSpec {
    val fileSpec = FileSpec
        .builder(bytonioOptions.packageName, "BytonioSerializers")
        .addTypes(types.mapNotNull {
            if (it.isAnnotationPresent(Serializer::class))
                null
            else
                buildBinarySerializerImplementation(it, logger)
        }.toList())

    return fileSpec.build()
}

fun buildBinarySerializerImplementation(clazz: KSClassDeclaration, logger: KSPLogger): TypeSpec {
    return TypeSpec.objectBuilder(clazz.serializerClass())
        .addSuperinterface(BinarySerializer::class.java.asClassName().parameterizedBy(clazz.toClassName()))
        .addFunction(buildGetBinarySizeMethod(clazz, logger))
        .addFunction(buildToByteArrayMethod(clazz, logger))
        .build()
}
