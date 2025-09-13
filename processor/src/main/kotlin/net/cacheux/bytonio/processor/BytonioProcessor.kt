package net.cacheux.bytonio.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.writeTo
import net.cacheux.bytonio.annotations.DataObject

class BytonioProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String> // KSP options passed from build script
) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            // KSP can run in multiple rounds, only process once for simplicity here
            // or handle incremental processing properly for more complex scenarios.
            logger.info("Already invoked, skipping.")
            return emptyList()
        }

        bytonioOptions = options.toBytonioOptions()

        val annotationName = DataObject::class.qualifiedName!!
        logger.info("BytonioProcessor: Starting processing for @DataObject")

        // Get symbols annotated with @DataObject
        val symbols = resolver.getSymbolsWithAnnotation(annotationName)

        logger.info("BytonioProcessor: symbols with annotation: ${symbols.toList().size}")

        val validSymbols = symbols.filterIsInstance<KSClassDeclaration>()

        if (!validSymbols.iterator().hasNext()) {
            logger.info("BytonioProcessor: No valid symbols found with @$annotationName")
            invoked = true
            return symbols.filterNot(KSNode::validate).toList() // Return invalid symbols for KSP to handle
        }

        logger.info("BytonioProcessor: Found ${validSymbols.count()} classes annotated with @DataObject")

        buildSerializers(validSymbols, logger).writeTo(
            codeGenerator = codeGenerator,
            dependencies = Dependencies(aggregating = true, *validSymbols.mapNotNull { it.containingFile }.toList().toTypedArray()),
        )

        buildDeserializers(validSymbols, logger).writeTo(
            codeGenerator = codeGenerator,
            dependencies = Dependencies(aggregating = true, *validSymbols.mapNotNull { it.containingFile }.toList().toTypedArray()),
        )

        buildExtensions(validSymbols, logger).writeTo(
            codeGenerator = codeGenerator,
            dependencies = Dependencies(aggregating = true, *validSymbols.mapNotNull { it.containingFile }.toList().toTypedArray()),
        )

        invoked = true // Mark as invoked to avoid reprocessing in this simple example

        // Return symbols that couldn't be processed or deferred for next round
        // For simple processors, often emptyList() if all processed, or invalid ones.
        return symbols.filterNot(KSNode::validate).toList()
    }

    override fun finish() {
        logger.info("BytonioProcessor: Processing finished.")
    }

    override fun onError() {
        logger.error("BytonioProcessor: An error occurred during processing.")
    }
}

class BytonioProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return BytonioProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
            options = environment.options
        )
    }
}
