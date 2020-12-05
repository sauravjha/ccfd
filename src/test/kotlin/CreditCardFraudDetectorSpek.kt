import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import detector.CreditCardFraudDetector
import detector.InvalidCSVException
import detector.Transaction
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.time.LocalDateTime

data class Scenario(
    val description: String,
    val file: File,
    val expectedErrorMessage: String
)

object CreditCardFraudDetectorSpek : Spek({
    describe("creditCardDetector") {
        context("readCSVFile") {

            context("when the valid sample file is passed") {
                val valid = File("src/test/resources/valid-sample.csv")
                val result = CreditCardFraudDetector(150.0, valid).readCSVFile()
                val expected = mapOf(
                    "10d7ce2f43e35fa57d1bbf8b1e2" to listOf(
                        Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                        Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:54"), 100.0),
                        Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:56"), 100.0)
                    ),
                    "10d7ce2f43e35fa57d1bbf8b1e1" to listOf(
                        Transaction("10d7ce2f43e35fa57d1bbf8b1e1", LocalDateTime.parse("2014-04-29T13:15:54"), 10.0)
                    ),
                    "10d7ce2f43e35fa57d1bbf8b1e3" to listOf(
                        Transaction("10d7ce2f43e35fa57d1bbf8b1e3", LocalDateTime.parse("2014-04-29T13:15:54"), 30.0)
                    ),
                )

                result.keys.forEachIndexed { index, creditCardId ->
                    it("returns correct credit card for index ${index + 1}") {
                        assertThat(creditCardId, equalTo(expected.keys.toList()[index]))
                    }
                    it("returns correct Transaction list for each credit card of index $index") {
                        assertThat(result[creditCardId], equalTo(expected[creditCardId]))
                    }
                }
            }

            context("when the csvfile is valid and data is invalid") {
                val basePath = "src/test/resources/valid-csv-invalid-data"
                val prefixMessage = "CSV data is invalid:"
                listOf(
                    Scenario(
                        "Invalid date entry in the first column",
                        File("$basePath/invalid-date-entry-in-first-column.csv"),
                        "$prefixMessage Text '2014-04-2913:15:20' could not be parsed at index 10"
                    ),
                    Scenario(
                        "Invalid amount entry in the 3rd column",
                        File("$basePath/invalid-amount-entry-in-3rd-column.csv"),
                        "$prefixMessage For input string: \"invalidAmount\""
                    )

                ).forEach { (description, file, expectedErrorMessage) ->
                    it(description) {
                        assertThat({ CreditCardFraudDetector(150.0, file).readCSVFile() }, throws<InvalidCSVException>(withMessage(expectedErrorMessage)))
                    }
                }
            }

            context("when the csvfile is invalid data") {
                val basePath = "src/test/resources/invalid-csv"
                val prefixMessage = "CSV file was invalid:"
                listOf(
                    Scenario(
                        "file corrupted at the end i.e is empty column",
                        File("$basePath/empty-end.csv"),
                        "$prefixMessage Fields num seems to be 3 on each row, but on 5th csv row, fields num is 1."
                    ),
                    Scenario(
                        "file corrupted in the beginning i.e is empty column",
                        File("$basePath/empty-beginning.csv"),
                        "$prefixMessage Fields num seems to be 1 on each row, but on 2th csv row, fields num is 3."
                    ),
                    Scenario(
                        "file corrupted in the middle i.e is empty column",
                        File("$basePath/empty-middle.csv"),
                        "$prefixMessage Fields num seems to be 3 on each row, but on 2th csv row, fields num is 1."
                    ),
                    Scenario(
                        "file corrupted in the beginning i.e number of row is invalid",
                        File("$basePath/corrupt-entry-beginning.csv"),
                        "$prefixMessage Fields num seems to be 5 on each row, but on 2th csv row, fields num is 3."
                    ),
                    Scenario(
                        "file corrupted in the middle i.e number of row is invalid",
                        File("$basePath/corrupt-entry-middle.csv"),
                        "$prefixMessage Fields num seems to be 3 on each row, but on 2th csv row, fields num is 5."
                    ),
                    Scenario(
                        "file corrupted at the end i.e number of row is invalid",
                        File("$basePath/corrupt-entry-end.csv"),
                        "$prefixMessage Fields num seems to be 3 on each row, but on 5th csv row, fields num is 6."
                    )

                ).forEach { (description, file, expectedErrorMessage) ->
                    it(description) {
                        assertThat({ CreditCardFraudDetector(150.0, file).readCSVFile() }, throws<InvalidCSVException>(withMessage(expectedErrorMessage)))
                    }
                }
            }
        }

        context("isTranactionFraud") {
            val priceThreshold = 150.0
            val creditCardFraudDetector = CreditCardFraudDetector(priceThreshold, File("dontcare"))
            context("when the transaction is not fraud") {

                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0)
                )

                val result = creditCardFraudDetector.isCardTransactionsFraud(input)
                it("returns false") {
                    assertThat(result, equalTo(false))
                }
            }

            context("when the transaction is not fraud") {

                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T15:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T16:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T17:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T18:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T19:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T20:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T21:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T22:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T23:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T00:00:00"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T04:15:54"), 10.0), // Total amount here is 140
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T05:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0)
                )

                val result = CreditCardFraudDetector(priceThreshold, File("dontcare")).isCardTransactionsFraud(input)

                it("returns false") {
                    assertThat(result, equalTo(false))
                }
            }

            context("when the transaction is  fraud when total amout is more then 150") {

                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T15:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T16:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T17:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T18:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T19:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T20:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T21:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T22:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T23:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T00:00:00"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T04:15:54"), 40.0), // Total amout here is 170
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T05:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0)
                )

                val result = CreditCardFraudDetector(priceThreshold, File("dontcare")).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when first window exceed the limit") {

                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 150.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T15:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T16:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T17:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T18:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T19:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T20:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T21:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T22:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T23:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T00:00:00"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T04:15:54"), 40.0), // Total amout here is 170
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T05:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0)
                )

                val result = CreditCardFraudDetector(priceThreshold, File("dontcare")).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when second window exceed the limit") {
                println("*****************")
                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T15:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T16:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T17:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T18:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T19:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T20:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T21:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T22:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T23:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T00:00:00"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T04:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T14:15:54"), 100.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T05:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0)
                )

                val result = CreditCardFraudDetector(priceThreshold, File("dontcare")).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when last window for one transaction") {

                val input = listOf(
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T13:15:20"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T15:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T16:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T17:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T18:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T19:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T20:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T21:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T22:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-29T23:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T00:00:00"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T04:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T14:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-04-30T05:15:54"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:30"), 130.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2014-05-29T13:15:56"), 10.0),
                    Transaction("10d7ce2f43e35fa57d1bbf8b1e2", LocalDateTime.parse("2020-05-29T13:15:56"), 150.0)
                )

                val result = CreditCardFraudDetector(priceThreshold, File("dontcare")).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }
        }
    }
})

fun withMessage(message: String): Matcher<InvalidCSVException> = has(InvalidCSVException::message, equalTo(message))
