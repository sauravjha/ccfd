package validation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import detector.CreditCardFraudDetector

val CSV_REGEX = "([a-zA-Z0-9/\\s_\\\\.\\-\\(\\):])+(.csv)\$".toRegex()

class ArgumentValidator : CliktCommand() {
    private val priceThreshold: Double by argument(help = "Enter the price threshold argument").double()

    private val fileName by argument(help =
    "Enter file with complete location( if the file exist in current dir just enter the name)")
        .file(mustExist = true).validate { require(CSV_REGEX.matches(it.toString())) { "File must be CSV file" } }

    override fun run() {
        CreditCardFraudDetector(priceThreshold, fileName).detect()
    }
}
