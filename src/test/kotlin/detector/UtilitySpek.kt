package detector

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import detector.exception.InvalidAmountFormat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UtilitySpek : Spek({
    describe("isAmountInValidFormat") {
        context("when amount is in valid format and dollar with 2 digit") {
            val result = "10.01".isAmountInValidFormat()
            it("returns same value") {
                assertThat(result, equalTo("10.01"))
            }
        }
        context("when amount is in valid format and dollar with 3 digit") {
            val result = "110.01".isAmountInValidFormat()
            it("returns same value") {
                assertThat(result, equalTo("110.01"))
            }
        }
        context("when amount is in not in valid format with cent only is first decimal place") {
            it("throws InvalidAmountFormat") {
                assertThat({ "10.1".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("10.1 is not is dollars.cents format")))
            }
        }
        context("when amount is in not in valid format when the cent is represented with 3 digit") {
            it("throws InvalidAmountFormat") {
                assertThat({ "10.100".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("10.100 is not is dollars.cents format")))
            }
        }
        context("when amount is in not in valid format and no cent value is not present") {
            it("throws InvalidAmountFormat") {
                assertThat({ "10.".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("10. is not is dollars.cents format")))
            }
        }
        context("when amount is in not in valid format and no cent value is not present") {
            it("throws InvalidAmountFormat") {
                assertThat({ "10.".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("10. is not is dollars.cents format")))
            }
        }
        context("when amount is string of letter") {
            it("throws InvalidAmountFormat") {
                assertThat({ "AA.BB".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("AA.BB is not is dollars.cents format")))
            }
        }
        context("when amount is string of special characters") {
            it("throws InvalidAmountFormat") {
                assertThat({ "@#.^&".isAmountInValidFormat() }, throws<InvalidAmountFormat>(withMessage("@#.^& is not is dollars.cents format")))
            }
        }
    }
})
private fun withMessage(message: String): Matcher<InvalidAmountFormat> = has(InvalidAmountFormat::message, equalTo(message))
