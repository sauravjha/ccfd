package detector

import detector.exception.InvalidCSVException
import kotlin.system.exitProcess

class CreditCardFraudDetector(
    private val csvCsvFileReader: CsvFileReader,
    private val priceThreshold: Double,
    private val numOfHrSlidingWindow: Long = 24
) {
    fun detect() {
        val cardResult = try {
            csvCsvFileReader.readCSVFile().map {
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

    fun isCardTransactionsFraud(transaction: List<Transaction>): Boolean {
        transaction.forEachIndexed { index, eachTransaction ->
            transaction.drop(index).asSequence().filter {
                it.dataTime <= eachTransaction.dataTime.plusHours(numOfHrSlidingWindow)
            }.map { it.amount }.sum().takeIf { it >= priceThreshold }?.let {
                return true
            }
        }
        return false
    }
}
