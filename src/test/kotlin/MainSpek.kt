import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MainSpek : Spek({
    describe("main") {
        context("call main") {
           it("returns hello world") {
               assertThat(main(), equalTo("Hello World"))
           }
        }
    }
})

