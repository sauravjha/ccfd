package detector

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
        val csvFileReader = CsvFileReader(File("dontcare"))

        context("isTranactionFraud") {
            val priceThreshold = 150.0
            val creditCardFraudDetector = CreditCardFraudDetector(csvFileReader, priceThreshold)
            context("when the transaction is not fraud for single slide window") {

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

            context("when the transaction is not fraud form multiple slide window") {

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

                val result = CreditCardFraudDetector(csvFileReader, priceThreshold).isCardTransactionsFraud(input)

                it("returns false") {
                    assertThat(result, equalTo(false))
                }
            }

            context("when the transaction is  fraud when total amount is more then 150") {
                // println("*******13**********")
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

                val result = CreditCardFraudDetector(csvFileReader, priceThreshold).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when first window exceed the limit") {
                // println("*******first**********")
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

                val result = CreditCardFraudDetector(csvFileReader, priceThreshold).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when second window exceed the limit") {
                // println("*******second**********")
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

                val result = CreditCardFraudDetector(csvFileReader, priceThreshold).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }

            context("when the transaction is fraud when last window for one transaction") {
                // println("*******last**********")
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

                val result = CreditCardFraudDetector(csvFileReader, priceThreshold).isCardTransactionsFraud(input)

                it("returns true") {
                    assertThat(result, equalTo(true))
                }
            }
        }
    }
})
