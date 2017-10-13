import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.krouton.*
import com.natpryce.krouton.http4k.resources
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URI


val genericPath = +string + "genericPath" + string

fun server(): HttpHandler {
    return resources {
        genericPath methods {
            GET { (x, y) -> Response(OK).body(x + " " + y) }
        }
    }
}

class KotlinCompilerIssueTest {

    @Test
    fun `compiler issue`() {
        assertThat(http(GET, "hello/genericPath/world"), equalTo("hello world"))
    }

    private fun http(method: Method, path: String): String {
        return (serverUri.resolve(path).toURL().openConnection() as HttpURLConnection)
            .run {
                requestMethod = method.name
                inputStream.reader().readText().trim()
            }
    }

    companion object {
        val port = 8954
        val server = server().asServer(SunHttp(port))
        val serverUri = URI("http://127.0.0.1:$port/")

        @BeforeClass
        @JvmStatic
        fun startServer() {
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun stopServer() {
            server.stop()
        }
    }

}