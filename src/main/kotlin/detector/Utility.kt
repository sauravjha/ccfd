package detector

import detector.exception.InvalidAmountFormat
import detector.exception.InvalidHashedFormat

private val DOLLARS_CENTS_REGEX = "[0-9]*.[0-9]{2}".toRegex()

fun String.isAmountInValidFormat(): String {
    return when (DOLLARS_CENTS_REGEX.matches(this)) {
        true -> this
        false -> throw InvalidAmountFormat("$this is not is dollars.cents format")
    }
}

fun String.isHashedStringValid(size: Int): String {
    val regex = "^([a-z0-9]{$size})\$".toRegex()
    return when (regex.matches(this)) {
        true -> this
        false -> throw InvalidHashedFormat("$this should of of size $size and combination of digit and small alphabet")
    }
}
