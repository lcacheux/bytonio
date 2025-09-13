package net.cacheux.bytonio.processor

import net.cacheux.bytonio.DataSizeFormat
import net.cacheux.bytonio.Order

class BytonioOptions {
    var packageName: String = BYTONIO_PACKAGE_NAME
    var defaultDataSizeFormat: DataSizeFormat = DataSizeFormat.INT
    var defaultByteOrder: Order? = null
}

fun Map<String, String>.toBytonioOptions() =
    BytonioOptions().also { options ->
        this["bytonio.packageName"]?.let { options.packageName = it }
        this["bytonio.dataSizeFormat"]?.let {
            options.defaultDataSizeFormat = when {
                "int".equals(it, ignoreCase = true) -> DataSizeFormat.INT
                "short".equals(it, ignoreCase = true) -> DataSizeFormat.SHORT
                "byte".equals(it, ignoreCase = true) -> DataSizeFormat.BYTE
                else -> DataSizeFormat.INT
            }
        }
        this["bytonio.byteOrder"]?.let {
            options.defaultByteOrder = when {
                it.startsWith("little", ignoreCase = true) -> Order.LITTLE_ENDIAN
                it.startsWith("big", ignoreCase = true) -> Order.BIG_ENDIAN
                else -> null
            }
        }
    }

var bytonioOptions = BytonioOptions()
