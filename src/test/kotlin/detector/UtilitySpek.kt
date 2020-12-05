package detector

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.throws
import detector.exception.InvalidAmountFormat
import detector.exception.InvalidHashedFormat
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

    describe("isHashedStringValid") {
        context("when the valid size is 27") {
            val size = 27
            context("when the input is as expected") {
                val inputValue = "10d7ce2f43e35fa57d1bbf8b1e2"
                val result = inputValue.isHashedStringValid(size)
                it("returns the same string") {
                    assertThat(result, equalTo(inputValue))
                }
            }

            context("when the input size larger then 27") {
                it("throws InvalidHashedFormat") {
                    val inputValueLarger = "10d7ce2f43e35fa57d1bbf8b1e2a"
                    assertThat({ inputValueLarger.isHashedStringValid(size) }, throws<InvalidHashedFormat>(withMessage2("$inputValueLarger should of of size $size and combination of digit and small alphabet")))
                }
            }

            context("when the input size smaller then 27") {
                it("throws InvalidHashedFormat") {
                    val inputValueSmallerSize = "10d7ce2f43e35fa57d1bbf8b1e"
                    assertThat({ inputValueSmallerSize.isHashedStringValid(size) }, throws<InvalidHashedFormat>(withMessage2("$inputValueSmallerSize should of of size $size and combination of digit and small alphabet")))
                }
            }

            context("when the input has capital letter") {
                it("throws InvalidHashedFormat") {
                    val inputValueHasCapitalAlphabet = "10d7ce2f43E35fa57d1bbf8b1e"
                    assertThat({ inputValueHasCapitalAlphabet.isHashedStringValid(size) }, throws<InvalidHashedFormat>(withMessage2("$inputValueHasCapitalAlphabet should of of size $size and combination of digit and small alphabet")))
                }
            }

            context("when the input has special character") {
                it("throws InvalidHashedFormat") {
                    val inputValueHasSpecialCharacter = "10d7ce2f43%35fa57d1bbf8b1e"
                    assertThat({ inputValueHasSpecialCharacter.isHashedStringValid(size) }, throws<InvalidHashedFormat>(withMessage2("$inputValueHasSpecialCharacter should of of size $size and combination of digit and small alphabet")))
                }
            }

            context("when the input is empty String") {
                it("throws InvalidHashedFormat") {
                    val inputValueIsEmptySting = ""
                    assertThat({ inputValueIsEmptySting.isHashedStringValid(size) }, throws<InvalidHashedFormat>(withMessage2("$inputValueIsEmptySting should of of size $size and combination of digit and small alphabet")))
                }
            }
        }
        context("when the valid size is 32") {
            val size = 32
            context("when the input is as expected") {
                val inputValue = "a9046c73e00331af68917d3804f70655"
                val result = inputValue.isHashedStringValid(size)
                it("returns the same string") {
                    assertThat(result, equalTo(inputValue))
                }
            }
        }
    }
})
private fun withMessage(message: String): Matcher<InvalidAmountFormat> = has(InvalidAmountFormat::message, equalTo(message))
private fun withMessage2(message: String): Matcher<InvalidHashedFormat> = has(InvalidHashedFormat::message, equalTo(message))
