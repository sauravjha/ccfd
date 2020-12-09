package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import detector.CreditCardFraudDetector
import detector.CsvFileReader

private val CSV_REGEX = "([a-zA-Z0-9/\\s_\\\\.\\-\\(\\):])+(.csv)\$".toRegex()

class ArgumentGetterAndValidator : CliktCommand() {
    private val priceThreshold: Double by argument(help = "(MANDATORY) Enter the price threshold.").double()

    private val fileName by argument(
        help =
            "(MANDATORY) Enter *.csv file with complete location."
    )
        .file(mustExist = true).validate { require(CSV_REGEX.matches(it.toString())) { "File must be CSV file" } }
    private val hashedCreditCardNumberSize by argument(help = "(OPTIONAL) Size of the hashed credit card number by (default is is 27.)").int().default(27)

    override fun run() {
        CreditCardFraudDetector(CsvFileReader(fileName, hashedCreditCardNumberSize), priceThreshold).detect()
    }
}
