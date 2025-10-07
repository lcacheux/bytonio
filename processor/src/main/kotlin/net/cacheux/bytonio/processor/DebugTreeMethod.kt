package net.cacheux.bytonio.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.reflect.KClass

const val DEBUG_TREE_METHOD_NAME = "debugTree"

fun buildDebugTreeSource(types: Sequence<KSClassDeclaration>, logger: KSPLogger) =
    FileSpec
        .builder(bytonioOptions.packageName, "BytonioDebugTree")
        .addImport("net.cacheux.bytonio.utils", "asByteArray")
        .addImport("net.cacheux.bytonio.utils", "toHexString")
        .addFunctions(
            types.mapNotNull {
                if (it.haveMethod(DEBUG_TREE_METHOD_NAME))
                    null
                else
                    buildDebugTreeMethod(it, logger)
            }.toList()
        )
        .addProperty(buildDebugTreeMap(types, logger))
        .addFunction(buildDebugTreeGenericMethod())
        .build()

fun buildDebugTreeMethod(clazz: KSClassDeclaration, logger: KSPLogger) =
    clazz.parseConstructor { constructor ->
        FunSpec.builder(DEBUG_TREE_METHOD_NAME)
            .addParameter(
                ParameterSpec.builder("depth", Int::class)
                    .defaultValue("0")
                    .build()
            )
            .receiver(clazz.toClassName())
            .returns(String::class)
            .apply {
                addStatement("val stringResult = buildString {")
                addStatement("  append(\"${clazz.simpleName.asString()}:\\n\")")
                constructor.parameters.forEach { param ->
                    addStatement("  repeat(depth + 1) { append(\"  \") }")
                    addStatement("  append(\"${param.stringName()}: \")")
                    addStatement("  append(${paramToByteArray(param)})")
                    addStatement("  append(\"\\n\")")
                }
                addStatement("}")
                addStatement("return stringResult")
            }
            .build()
    }

fun paramToByteArray(param: KSValueParameter): String {
    return when (param.encodedType()) {
        EncodedType.INT -> "${param.stringName()}?.toInt()?.asByteArray()?.toHexString()"
        EncodedType.SHORT -> "${param.stringName()}?.toShort()?.asByteArray()?.toHexString()"
        EncodedType.BYTE -> "byteArrayOf(${param.stringName()})?.toHexString()"
        EncodedType.BYTE_ARRAY -> "${param.stringName()}?.toHexString()"
        EncodedType.OBJECT -> "${param.stringName()}?.let { debugTreeForClass(it, depth + 1) } ?: \"null\""
    }
}

fun buildDebugTreeMap(types: Sequence<KSClassDeclaration>, logger: KSPLogger): PropertySpec {
    val mapEntries = types.map { "%T::class to " +
            "{ instance, depth -> (instance as %T).${DEBUG_TREE_METHOD_NAME}(depth) }"
        }.joinToString(", ")
    val mapContent = types.flatMap {
        listOf(it.toClassName(), it.toClassName())
    }.toList().toTypedArray()

    return PropertySpec.builder(
        "debugTreeMap",
        MAP.parameterizedBy(
            KClass::class.asClassName().parameterizedBy(STAR),
            Function2::class.asClassName().parameterizedBy(ANY, INT, STRING)
        )
    )
        .initializer("mapOf($mapEntries)", *mapContent)
        .addModifiers(KModifier.PUBLIC)
        .build()
}

fun buildDebugTreeGenericMethod(): FunSpec {
    return FunSpec.builder("debugTreeForClass")
        .addModifiers(KModifier.INLINE)
        //.addTypeVariable(TypeVariableName("T").copy(reified = true))
        .addParameter("instance", Any::class)
        .addParameter("depth", Int::class)
        .returns(String::class)
        .addCode(
            "return debugTreeMap[instance::class]?.invoke(instance as Any, depth) ?: " +
                    "throw IllegalArgumentException(\"Class doesn't have a debug tree method: \" + instance::class)",
        )
        .build()
}
