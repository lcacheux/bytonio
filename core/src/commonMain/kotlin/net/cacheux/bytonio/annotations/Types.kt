package net.cacheux.bytonio.annotations

import net.cacheux.bytonio.DataFormat
import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order

/**
 * This annotation can be used on a parameter stored as Short or Int to specify the endian format.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Ordered(
    val order: Order = Order.BIG_ENDIAN
)

/**
 * The parameter will be stored as a single byte.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class EncodeAsByte

/**
 * The parameter will be stored as a short (2 bytes).
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class EncodeAsShort

/**
 * The parameter will be stored as an integer (4 bytes).
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class EncodeAsInt

/**
 * Annotation for byte arrays and objects parameters to specify how it should be encoded.
 *
 * @param format     If fixed, the size of the array or object should be always the same, and the
 *                   size won't be stored in the data.
 * @param size       The fixed size of the array or object, if format is set to FIXED.
 * @param sizeFormat For DYNAMIC format, what data type will be used to store the size
 *                   (int, short, byte)
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class EncodeAsData(
    val format: DataFormat = DataFormat.DYNAMIC,
    val size: Int = 0,
    val sizeFormat: DataSizeFormat = DataSizeFormat.INHERIT
)

/**
 * Parameter with this annotation won't be stored in the binary data. It should have a default
 * value which will be used for deserialization.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class IgnoreEncoding()
