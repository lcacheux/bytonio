package net.cacheux.bytonio.utils

private val HEX_DIGITS = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

fun ByteArray.toHexString(offset: Int = 0, length: Int = this.size): String {
    val buf = CharArray(length * 2)
    var bufIndex = 0
    for (i in offset until offset + length) {
        val b = this[i]
        buf[bufIndex++] = HEX_DIGITS[b.toInt() ushr 4 and 0x0F]
        buf[bufIndex++] = HEX_DIGITS[b.toInt() and 0x0F]
    }
    return buf.concatToString()
}
