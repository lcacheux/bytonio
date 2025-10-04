package net.cacheux.bytonio.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName

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
                //addStatement("  repeat(depth) { append(\"  \") }")
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
        EncodedType.OBJECT -> "${param.stringName()}?.$DEBUG_TREE_METHOD_NAME(depth + 1)"
    }
}
