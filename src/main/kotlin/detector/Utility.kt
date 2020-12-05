package detector

import detector.exception.InvalidAmountFormat

private val DOLLARS_CENTS_REGEX = "[0-9]*.[0-9]{2}".toRegex()

fun String.isAmountInValidFormat(): String {
    return when (DOLLARS_CENTS_REGEX.matches(this)) {
        true -> this
        false -> throw InvalidAmountFormat("$this is not is dollars.cents format")
    }
}
