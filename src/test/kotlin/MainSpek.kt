import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MainSpek : Spek({
    describe("main") {
        context("call main") {
            it("returns hello world") {
                val arguments = arrayOf("150", "src/test/resources/valid-sample.csv")
                main(arguments)
            }
        }
    }
})
