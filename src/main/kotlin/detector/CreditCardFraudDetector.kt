package detector

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.CSVFieldNumDifferentException
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.system.exitProcess

class CreditCardFraudDetector(
    private val priceThreshold: Double,
    private val fileName: File,
    private val numOfHr: Long = 24
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
                Transaction(it[0].trim(), LocalDateTime.parse(it[1].trim()), it[2].toDouble())
            }.toList()
        } catch (e: CSVFieldNumDifferentException) {
            throw InvalidCSVException("CSV file was invalid: ${e.message}")
        } catch (e: DateTimeParseException) {
            throw InvalidCSVException("CSV data is invalid: ${e.message}")
        } catch (e: NumberFormatException) {
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
            val interestedWindow = transaction.drop(index).filter {
                it.dataTime <= eachTransaction.dataTime.plusHours(numOfHr) }
            //println("$index ->${interestedWindow.map { it.amount }.sum()} $interestedWindow")
            if (interestedWindow.map { it.amount }.sum() >= priceThreshold) return true
        }
        return false
    }
}
