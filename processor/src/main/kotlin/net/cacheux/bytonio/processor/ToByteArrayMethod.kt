package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.utils.ByteArrayWriter

const val TO_BYTE_ARRAY_METHOD_NAME = "toByteArray"

@OptIn(KspExperimental::class)
fun buildToByteArrayMethod(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    val dataObjectAnnotation = clazz.getAnnotationsByType(DataObject::class).first()

    return clazz.parseConstructor { constructor ->
        val builder = FunSpec.builder(TO_BYTE_ARRAY_METHOD_NAME)
            .addParameter(ParameterSpec.builder("data", clazz.toClassName()).build())
            .addModifiers(KModifier.OVERRIDE)
            .returns(ByteArray::class)
            .addStatement("val writer = %T(ByteArray(getBinarySize(data)))", ByteArrayWriter::class)

        constructor.parameters.forEach { param ->
            if (!param.checkIfIgnored()) {
                val byteOrder = param.byteOrder(dataObjectAnnotation)
                when (param.encodedType()) {
                    EncodedType.INT ->
                        builder.addStatement(
                            "writer.writeInt(data.${param.stringName()}.toInt(), %T.%L)",
                            Order::class,
                            byteOrder
                        )

                    EncodedType.SHORT ->
                        builder.addStatement(
                            "writer.writeShort(data.${param.stringName()}.toShort(), %T.%L)",
                            Order::class,
                            byteOrder
                        )

                    EncodedType.BYTE ->
                        builder.addStatement("writer.writeByte(data.${param.stringName()})")

                    EncodedType.BYTE_ARRAY -> {
                        if (param.dataFormat() == DataFormat.DYNAMIC) {
                            val dataSizeFormat =
                                param.effectiveByteArraySizeFormat(dataObjectAnnotation)

                            val paramSize = if (param.isNullable()) {
                                "data.${param.stringName()}?.size ?: 0"
                            } else {
                                "data.${param.stringName()}.size"
                            }

                            when (dataSizeFormat) {
                                DataSizeFormat.INT ->
                                    builder.addStatement(
                                        "writer.writeInt($paramSize, %T.%L)",
                                        Order::class,
                                        byteOrder
                                    )

                                DataSizeFormat.SHORT ->
                                    builder.addStatement(
                                        "writer.writeShort(($paramSize).toShort(), %T.%L)",
                                        Order::class,
                                        byteOrder
                                    )

                                DataSizeFormat.BYTE ->
                                    builder.addStatement("writer.writeByte((${paramSize}).toByte())")

                                DataSizeFormat.INHERIT -> {}
                            }
                        }

                        if (param.isNullable()) {
                            builder.addStatement("data.${param.stringName()}?.let { writer.writeByteArray(it) }")
                        } else {
                            builder.addStatement("writer.writeByteArray(data.${param.stringName()})")
                        }

                    }

                    EncodedType.OBJECT -> {
                        if (param.dataFormat() == DataFormat.DYNAMIC) {
                            val dataSizeFormat =
                                param.effectiveByteArraySizeFormat(dataObjectAnnotation)

                            val paramSize = if (param.isNullable()) {
                                "data.${param.stringName()}?.getBinarySize() ?: 0"
                            } else {
                                "data.${param.stringName()}.getBinarySize()"
                            }

                            when (dataSizeFormat) {
                                DataSizeFormat.INT -> builder.addStatement("writer.writeInt(${paramSize})")
                                DataSizeFormat.SHORT -> builder.addStatement("writer.writeShort((${paramSize}).toShort())")
                                DataSizeFormat.BYTE -> builder.addStatement("writer.writeByte((${paramSize}).toByte())")
                                DataSizeFormat.INHERIT -> {}
                            }
                        }

                        if (param.isNullable()) {
                            builder.addStatement("data.${param.stringName()}?.let { writer.writeByteArray(it.toByteArray()) }")
                        } else {
                            builder.addStatement("writer.writeByteArray(data.${param.stringName()}.toByteArray())")
                        }
                    }
                }
            }
        }

        builder.addStatement("return writer.byteArray")
        builder.build()
    }
}
