package net.cacheux.bytonio.annotations

import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order
import kotlin.reflect.KClass

/**
 * Classes with the DataObject annotation will have serializers and deserializers generated, as
 * well as extension methods getBinarySize and toByteArray.
 *
 * @param dataSizeFormat What format should be used to store size of arrays and objects.
 * @param order Short and Int values of this object will be converted to big or little endian format.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DataObject(
    val dataSizeFormat: DataSizeFormat = DataSizeFormat.INHERIT,
    val order: Order = Order.BIG_ENDIAN,
)

/**
 * If this annotation is used in combination with DataObject, a custom serializer will be used
 * instead of generating one.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Serializer(
    val serializer: KClass<*>
)

/**
 * If this annotation is used in combination with DataObject, a custom deserializer will be used
 * instead of generating one.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Deserializer(
    val deserializer: KClass<*>
)
