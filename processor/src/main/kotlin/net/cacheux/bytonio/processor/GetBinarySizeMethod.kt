@file:OptIn(KspExperimental::class)

package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.annotations.DataObject

const val GET_BINARY_SIZE_METHOD_NAME = "getBinarySize"

fun buildGetBinarySizeMethod(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    val dataObjectAnnotation = clazz.getAnnotationsByType(DataObject::class).first()

    val dataSize = dataObjectAnnotation.getDataSizeFormat().size()

    return clazz.parseConstructor { constructor ->
        val builder = FunSpec.builder(GET_BINARY_SIZE_METHOD_NAME)
            .addParameter(ParameterSpec.builder("data", clazz.toClassName()).build())
            .addModifiers(KModifier.OVERRIDE)
            .returns(Int::class)

        var staticSize = 0
        val byteArrayList = mutableListOf<KSValueParameter>()
        val objectsList = mutableListOf<KSValueParameter>()
        constructor.parameters.forEach { param ->
            if (!param.checkIfIgnored()) {
                logger.info(
                    "[${clazz.simpleName.asString()}] Parameter ${param.name?.asString()}: ${
                        param.type.resolve().toClassName()
                    } ${param.encodedType()} ${
                        param.annotations.toList().map { it.shortName.asString() }
                    }"
                )
                when (param.encodedType()) {
                    EncodedType.INT -> staticSize += 4
                    EncodedType.SHORT -> staticSize += 2
                    EncodedType.BYTE -> staticSize += 1
                    EncodedType.BYTE_ARRAY -> {
                        if (param.dataFormat() == DataFormat.FIXED) {
                            staticSize += param.byteArrayFixedSize()
                        } else {
                            byteArrayList.add(param)
                        }
                    }

                    EncodedType.OBJECT -> {
                        if (param.dataFormat() == DataFormat.FIXED) {
                            staticSize += param.byteArrayFixedSize()
                        } else {
                            objectsList.add(param)
                        }
                    }
                }
            } else logger.info("[${clazz.simpleName.asString()}] Parameter ${param.name?.asString()} ignored")
        }

        builder.addStatement("var totalSize = $staticSize")
        byteArrayList.forEach {
            if (it.type.resolve().isMarkedNullable) {
                builder.addStatement("data.${it.name?.asString()}?.let { totalSize += data.${it.name?.asString()}.size }")
            } else {
                builder.addStatement("totalSize += data.${it.name?.asString()}.size + $dataSize")
            }
        }
        objectsList.forEach {
            if (it.type.resolve().isMarkedNullable) {
                builder.addStatement("data.${it.name?.asString()}?.let { totalSize += data.${it.name?.asString()}.getBinarySize() }")
            } else {
                builder.addStatement("totalSize += data.${it.name?.asString()}.getBinarySize() + $dataSize")
            }
        }
        builder.addStatement("return totalSize")

        builder.build()
    }
}
