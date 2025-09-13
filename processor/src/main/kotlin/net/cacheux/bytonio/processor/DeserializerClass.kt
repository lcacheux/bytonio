@file:OptIn(DelicateKotlinPoetApi::class, KspExperimental::class)

package net.cacheux.bytonio.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.utils.ByteArrayReader
import kotlin.reflect.KClass

fun KSClassDeclaration.deserializerClass() = "${this.simpleName.asString()}Deserializer"
fun KSClassDeclaration.deserializerClassName() = ClassName(
    bytonioOptions.packageName,
    "${this.simpleName.asString()}Deserializer"
)

fun buildDeserializers(types: Sequence<KSClassDeclaration>, logger: KSPLogger): FileSpec {
    val fileSpec = FileSpec
        .builder(bytonioOptions.packageName, "BytonioDeserializers")
        .addTypes(types.mapNotNull {
            if (it.isAnnotationPresent(Deserializer::class))
                null
            else
                buildBinaryDeserializerImplementation(it, logger)
        }.toList())
        .addProperty(buildBinaryDeserializersMap(types, logger))
        .addFunction(buildFromByteArrayGenericMethod())
        .addFunction(buildFromByteArrayReaderGenericMethod())

    return fileSpec.build()
}

fun buildFromByteArrayGenericMethod(): FunSpec {
    return FunSpec.builder("fromByteArray")
        .addModifiers(KModifier.INLINE)
        .addTypeVariable(TypeVariableName("T").copy(reified = true))
        .addParameter("byteArray", ByteArray::class)
        .returns(TypeVariableName("T"))
        .addCode(
            "return binaryDeserializersMap[T::class]?.fromByteArray(byteArray) as? T ?: " +
                    "throw IllegalArgumentException(\"Class doesn't have a deserializer: \" + T::class)",
        )
        .build()
}

fun buildFromByteArrayReaderGenericMethod(): FunSpec {
    return FunSpec.builder("fromByteArrayReader")
        .addModifiers(KModifier.INLINE)
        .addTypeVariable(TypeVariableName("T").copy(reified = true))
        .addParameter("reader", ByteArrayReader::class)
        .returns(TypeVariableName("T"))
        .addCode(
            "return binaryDeserializersMap[T::class]?.fromByteArrayReader(reader) as? T ?: " +
                    "throw IllegalArgumentException(\"Class doesn't have a deserializer: \" + T::class)",
        )
        .build()
}

fun buildBinaryDeserializersMap(types: Sequence<KSClassDeclaration>, logger: KSPLogger): PropertySpec {
    val mapEntries = types.map { "%T::class to %T" }.joinToString(", ")
    val mapContent = types.flatMap {
        if (it.isAnnotationPresent(Deserializer::class)) {
            listOf(
                it.toClassName(),
                it.getDeserializerKsType()?.toClassName()
            )
        } else {
            listOf(it.toClassName(), it.deserializerClassName())
        }
    }.toList().toTypedArray()

    return PropertySpec.builder(
        "binaryDeserializersMap",
        MAP.parameterizedBy(
            KClass::class.asClassName().parameterizedBy(STAR),
            BinaryDeserializer::class.java.asClassName().parameterizedBy(STAR)
        )
    )
        .initializer("mapOf($mapEntries)", *mapContent)
        .addModifiers(KModifier.PUBLIC)
        .build()
}

fun buildBinaryDeserializerImplementation(clazz: KSClassDeclaration, logger: KSPLogger): TypeSpec {
    return TypeSpec.objectBuilder(clazz.deserializerClass())
        .addSuperinterface(BinaryDeserializer::class.java.asClassName().parameterizedBy(clazz.toClassName()))
        .addFunction(buildFromByteArrayMethod(clazz, logger))
        .addFunction(buildFromByteArrayReaderMethod(clazz, logger))
        .build()
}

fun buildFromByteArrayMethod(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    return clazz.parseConstructor { constructor ->
        FunSpec.builder(BinaryDeserializer<*>::fromByteArray.name)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder("byteArray", ByteArray::class).build())
            .returns(clazz.toClassName())
            .addStatement("return ${BinaryDeserializer<*>::fromByteArrayReader.name}(%T(byteArray))", ByteArrayReader::class)
            .build()
    }
}

fun buildFromByteArrayReaderMethod(clazz: KSClassDeclaration, logger: KSPLogger): FunSpec {
    val dataObjectAnnotation = clazz.getAnnotationsByType(DataObject::class).first()

    return clazz.parseConstructor { constructor ->
        val builder = FunSpec.builder(BinaryDeserializer<*>::fromByteArrayReader.name)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec.builder("reader", ByteArrayReader::class).build())
            .returns(clazz.toClassName())

        val effectiveParameters = constructor.parameters.mapNotNull { param ->
            if (!param.checkIfIgnored()) {

                val order = param.byteOrder(dataObjectAnnotation)
                val typeName = param.type.resolve().declaration.qualifiedName?.asString()
                val typeModifier = when (typeName) {
                    "kotlin.Int" -> ".toInt()"
                    "kotlin.Short" -> ".toShort()"
                    "kotlin.Byte" -> ".toByte()"
                    else -> ""
                }
                when (param.encodedType()) {
                    EncodedType.INT -> builder.addStatement(
                        "val ${param.stringName()} = reader.readInt(%T.%L)$typeModifier",
                        Order::class,
                        order
                    )

                    EncodedType.SHORT -> builder.addStatement(
                        "val ${param.stringName()} = reader.readShort(%T.%L)$typeModifier",
                        Order::class,
                        order
                    )

                    EncodedType.BYTE -> builder.addStatement("val ${param.stringName()} = reader.readByte()$typeModifier")
                    EncodedType.BYTE_ARRAY -> {
                        builder.addStatement(
                            "val ${param.stringName()} = ${
                                param.byteArrayReading(
                                    dataObjectAnnotation
                                )
                            }", Order::class, order
                        )
                    }

                    EncodedType.OBJECT -> {
                        builder.addStatement(
                            param.sizeRead(dataObjectAnnotation),
                            Order::class,
                            order
                        )  // Read size of next object but ignore it
                        builder.addStatement(
                            "val ${param.stringName()} = %T.fromByteArrayReader(reader)",
                            (param.type.resolve().declaration as KSClassDeclaration).deserializerClassName()
                        )
                    }
                }
                param
            } else null
        }

        builder.addStatement("return %T(${effectiveParameters.joinToString(", ") { it.stringName() + " = " + it.stringName() }})", clazz.toClassName())

        builder.build()
    }
}

fun KSValueParameter.byteArrayReading(annotation: DataObject): String {
    return "reader.readByteArray(${sizeRead(annotation)})"
}

fun KSValueParameter.sizeRead(annotation: DataObject) = if (dataFormat() == DataFormat.DYNAMIC) {
    val dataSizeFormat = effectiveByteArraySizeFormat(annotation)

    when(dataSizeFormat) {
        DataSizeFormat.INT -> "reader.readInt(%T.%L)"
        DataSizeFormat.SHORT -> "reader.readShort(%T.%L)"
        DataSizeFormat.BYTE -> "reader.readByte()"
        else -> ""
    }
} else {
    byteArrayFixedSize().toString()
}
