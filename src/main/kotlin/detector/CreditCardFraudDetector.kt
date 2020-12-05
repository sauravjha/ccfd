package detector

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import detector.exception.InvalidAmountFormat
import detector.exception.InvalidCSVException
import detector.exception.InvalidHashedFormat
import mu.KotlinLogging
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

class CreditCardFraudDetector(
    private val priceThreshold: Double,
    private val fileName: File,
    private val hashedCreditCardNumberSize: Int = 27,
    private val numOfHrSlidingWindow: Long = 24
) {
    fun detect() {
        val cardResult = try {
            readCSVFile().map {
                it.key to isCardTransactionsFraud(it.value)
            }
        } catch (e: InvalidCSVException) {
            println("Please! provide valid file :)")
            exitProcess(0)
        }
        println("List of Fraud Card")
        cardResult.filter { it.second }.forEach { (fraudCardId, _) ->
            println(fraudCardId)
        }
    }
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

//    fun isTranactionFraud(tranaction: List<Transaction>, index: Int = 0): Boolean {
//        if (tranaction.isEmpty()) {
//            return false
//        }
//        val intrestedTrans = tranaction.drop(index).filter { it.dataTime <= it.dataTime.plusHours(numOfHr) }
//        println("$index in")
//        if (intrestedTrans.map { it.amount }.sum() >= priceThreshold) return true
//        return isTranactionFraud(intrestedTrans, index+1)
//    }

    fun isCardTransactionsFraud(transaction: List<Transaction>): Boolean {
        transaction.forEachIndexed { index, eachTransaction ->
            transaction.drop(index).filter {
                it.dataTime <= eachTransaction.dataTime.plusHours(numOfHrSlidingWindow)
            }.map { it.amount }.sum().let {
                if (it >= priceThreshold) return true
            }
        }
        return false
    }
}
