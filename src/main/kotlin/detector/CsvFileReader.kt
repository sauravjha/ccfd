package detector

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import detector.exception.InvalidAmountFormat
import detector.exception.InvalidCSVException
import detector.exception.InvalidHashedFormat
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class CsvFileReader(
    private val fileName: File,
    private val hashedCreditCardNumberSize: Int = 27,
) {
    @Suppress("ThrowsCount")
    fun readCSVFile(): Map<String, List<Transaction>> {
        val transactions = try {
            csvReader().readAll(fileName).map {
                Transaction(
                    it[0].trim().isHashedStringValid(hashedCreditCardNumberSize),
                    LocalDateTime.parse(it[1].trim()),
                    it[2].trim().isAmountInValidFormat().toDouble()
                )
            }.toList()
        } catch (e: CSVFieldNumDifferentException) {
            println(
                "CSV file is in wrong format it should be(10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00) \n" +
                    "Error message: ${e.message}"
            )
            throw InvalidCSVException("CSV file was invalid: ${e.message}")
        } catch (e: DateTimeParseException) {
            println(
                "Date format is wrong it should format year-month-dayThour:minute:second (e.g 2014-04-29T13:15:54)\n" +
                    "Error message: ${e.message}"
            )
            throw InvalidCSVException("CSV data is invalid: ${e.message}")
        } catch (e: InvalidHashedFormat) {
            println(
                "Hashed Credit Card Number is Wrong format\n" +
                    "Error message: ${e.message}"
            )
            throw InvalidCSVException("CSV data is invalid: ${e.message}")
        } catch (e: NumberFormatException) {
            println(
                "Amount is in wrong format it should be dollars.cents(e.g 10.00)\n" +
                    "Error message: ${e.message}"
            )
            throw InvalidCSVException("CSV data is invalid: ${e.message}")
        } catch (e: InvalidAmountFormat) {
            println(
                "Amount is in wrong format it should be dollars.cents(e.g 10.00)\n" +
                    "Error message: ${e.message}"
            )
            throw InvalidCSVException("CSV data is invalid: ${e.message}")
        }
        return transactions.groupBy { it.cardId }.toMap()
    }
}
