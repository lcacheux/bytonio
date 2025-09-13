package net.cacheux

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import io.mockk.every
import io.mockk.mockk
import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsData
import net.cacheux.bytonio.processor.BytonioOptions
import net.cacheux.bytonio.processor.bytonioOptions
import net.cacheux.bytonio.processor.effectiveByteArraySizeFormat
import net.cacheux.bytonio.processor.getDataSizeFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.reflect.full.memberProperties

class UtilsTest {
    @Test
    fun testGetDataSizeFormat() {
        val annotation = DataObject(dataSizeFormat = DataSizeFormat.SHORT)

        assertEquals(DataSizeFormat.SHORT, annotation.getDataSizeFormat())
        assertEquals(DataSizeFormat.INT, DataObject().getDataSizeFormat())

        bytonioOptions = BytonioOptions().apply { defaultDataSizeFormat = DataSizeFormat.BYTE }

        assertEquals(DataSizeFormat.BYTE, DataObject().getDataSizeFormat())
    }

    @OptIn(KspExperimental::class)
    @Test
    fun testEffectiveByteArraySizeFormat() {
        val encodeAsData = EncodeAsData(
            sizeFormat = DataSizeFormat.SHORT
        )
        val ksAnnotation = mockkKSAnnotation(encodeAsData)
        val valueParameter = mockk<KSValueParameter> {
            every { annotations } returns sequenceOf(ksAnnotation)
        }

        assertEquals(
            DataSizeFormat.SHORT,
            valueParameter.effectiveByteArraySizeFormat(DataObject(dataSizeFormat = DataSizeFormat.BYTE))
        )
    }

    fun mockkKSName(name: String) = mockk<KSName>(relaxed = true) {
        every { asString() } returns name
        every { getQualifier() } returns name.split(".").dropLast(1).joinToString(".")
        every { getShortName() } returns name.split(".").last()
    }

    fun mockkKSValueArgument(argName: String, argValue: Any?) = mockk<KSValueArgument>(relaxed = true) {
        every { name } returns mockkKSName(argName)
        every { value } returns argValue
    }

    inline fun <reified T: Annotation> mockkKSAnnotation(annotation: T) = mockk<KSAnnotation>(relaxed = true) {
        every { shortName } returns mockkKSName(T::class.simpleName!!)
        every { arguments } returns T::class.memberProperties.map {
            mockkKSValueArgument(it.name, it.call(annotation))
        }
        every { annotationType } returns mockk<KSTypeReference>(relaxed = true) {
            every { resolve() } returns mockk<KSType>(relaxed = true) {
                every { declaration } returns mockk<KSDeclaration>(relaxed = true) {
                    every { qualifiedName } returns mockkKSName(T::class.qualifiedName!!)
                }
            }
        }
    }
}
