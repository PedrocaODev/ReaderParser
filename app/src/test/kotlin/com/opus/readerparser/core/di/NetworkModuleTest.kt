package com.opus.readerparser.core.di

import coil3.SingletonImageLoader
import com.opus.readerparser.App
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.util.TreeMap
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NetworkModuleTest {

    @Test
    fun app_implements_singleton_image_loader_factory() {
        assertTrue(
            "App must implement coil3.SingletonImageLoader.Factory to provide application-wide Coil ImageLoader",
            SingletonImageLoader.Factory::class.java.isAssignableFrom(App::class.java)
        )
    }

    @Test
    fun image_okhttp_client_sends_browser_and_image_headers_and_has_no_cache() {
        val client = NetworkModule.createImageOkHttpClient()
        assertNull("Image OkHttpClient must not have HTTP disk cache", client.cache)

        try {
            val headers = captureRequestHeaders { url ->
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    assertEquals(200, response.code)
                }
            }

            assertEquals(
                NetworkModule.DEFAULT_BROWSER_USER_AGENT,
                headers["User-Agent"]
            )
            assertFalse(
                "User-Agent must not contain okhttp header",
                headers["User-Agent"]?.contains("okhttp", ignoreCase = true) == true
            )
            assertEquals(
                NetworkModule.DEFAULT_IMAGE_ACCEPT,
                headers["Accept"]
            )
            assertEquals(
                NetworkModule.DEFAULT_ACCEPT_LANGUAGE,
                headers["Accept-Language"]
            )
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }

    @Test
    fun http_client_sends_exact_user_agent_when_explicitly_set_without_reader_app_duplication() {
        val baseOkHttpClient = NetworkModule.createBaseOkHttpClient()
        val httpClient = NetworkModule.createHttpClient(baseOkHttpClient, NetworkModule.provideJson())
        val explicitUa = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/125.0.0.0 CustomBot/1.0"

        try {
            val headers = captureRequestHeaders { url ->
                runBlocking {
                    httpClient.get(url) {
                        header("User-Agent", explicitUa)
                        header("Accept-Language", "en-US,en;q=0.9")
                    }
                }
            }

            assertEquals(explicitUa, headers["User-Agent"])
            assertFalse(
                "User-Agent must not be duplicated or contain ReaderApp",
                headers["User-Agent"]?.contains("ReaderApp") == true
            )
        } finally {
            httpClient.close()
            baseOkHttpClient.dispatcher.executorService.shutdown()
            baseOkHttpClient.connectionPool.evictAll()
        }
    }

    @Test
    fun http_client_sends_default_browser_headers_when_request_headers_unset() {
        val baseOkHttpClient = NetworkModule.createBaseOkHttpClient()
        val httpClient = NetworkModule.createHttpClient(baseOkHttpClient, NetworkModule.provideJson())

        try {
            val headers = captureRequestHeaders { url ->
                runBlocking {
                    httpClient.get(url)
                }
            }

            assertEquals(
                NetworkModule.DEFAULT_BROWSER_USER_AGENT,
                headers["User-Agent"]
            )
            assertEquals(
                NetworkModule.DEFAULT_ACCEPT_LANGUAGE,
                headers["Accept-Language"]
            )
        } finally {
            httpClient.close()
            baseOkHttpClient.dispatcher.executorService.shutdown()
            baseOkHttpClient.connectionPool.evictAll()
        }
    }

    private fun captureRequestHeaders(block: (url: String) -> Unit): Map<String, String> {
        val serverSocket = ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))
        val executor = Executors.newSingleThreadExecutor()
        try {
            val future = executor.submit(Callable {
                val headers = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
                serverSocket.accept().use { socket ->
                    socket.soTimeout = 5000
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
                    val writer = socket.getOutputStream()

                    val requestLine = reader.readLine()
                    if (requestLine != null) {
                        while (true) {
                            val line = reader.readLine() ?: break
                            if (line.isEmpty()) break
                            val colonIndex = line.indexOf(':')
                            if (colonIndex != -1) {
                                val name = line.substring(0, colonIndex).trim()
                                val value = line.substring(colonIndex + 1).trim()
                                headers.merge(name, value) { existing, new -> "$existing, $new" }
                            }
                        }
                    }

                    val responseBody = "{}"
                    val response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: 2\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        responseBody
                    writer.write(response.toByteArray(Charsets.UTF_8))
                    writer.flush()
                }
                headers
            })

            block("http://127.0.0.1:${serverSocket.localPort}/")
            return future.get(5, TimeUnit.SECONDS)
        } finally {
            serverSocket.close()
            executor.shutdownNow()
        }
    }
}
