package detector

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import detector.exception.InvalidCSVException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.time.LocalDateTime

object CsvFileReaderSpek : Spek({
    describe("CsvFileReader") {
        context("readCSVFile when the hashed credit card needed is 27") {

            context("when the valid sample file is passed") {
                val valid = File("src/test/resources/valid-sample.csv")
                val result = CsvFileReader(valid).readCSVFile()
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
                        "$prefixMessage invalidAmount is not is dollars.cents format"
                    ),
                    Scenario(
                        "Invalid amount in 2nd column",
                        File("$basePath/invalid-amount-format-2nd-column.csv"),
                        "$prefixMessage 100.2 is not is dollars.cents format"
                    ),
                    Scenario(
                        "Invalid hash card entry in 4th column",
                        File("$basePath/invalid-hashed-credit-card-entry-in-4th-column.csv"),
                        "$prefixMessage iaminvalid should of of size 27 and combination of digit and small alphabet"
                    )
                ).forEach { (description, file, expectedErrorMessage) ->
                    it(description) {
                        assertThat({ CsvFileReader(file).readCSVFile() }, throws<InvalidCSVException>(withMessage(expectedErrorMessage)))
                    }
                }
            }

            context("when the csvfile has invalid data") {
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
                        assertThat({ CsvFileReader(file).readCSVFile() }, throws<InvalidCSVException>(withMessage(expectedErrorMessage)))
                    }
                }
            }
        }
    }
})

private fun withMessage(message: String): Matcher<InvalidCSVException> = has(InvalidCSVException::message, equalTo(message))
