package com.opus.readerparser.core.di

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/** Qualifier for the image OkHttpClient to avoid conflicts with the base OkHttpClient binding. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val DEFAULT_BROWSER_USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"

    const val DEFAULT_ACCEPT_LANGUAGE = "en-US,en;q=0.9"

    const val DEFAULT_IMAGE_ACCEPT = "image/avif,image/webp,image/apng,image/*,*/*;q=0.8"

    private fun defaultHeadersInterceptor(defaultAccept: String? = null): Interceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()

        val userAgent = original.header("User-Agent")
        if (userAgent.isNullOrBlank() || userAgent == "ktor-client") {
            builder.header("User-Agent", DEFAULT_BROWSER_USER_AGENT)
        }
        if (original.header("Accept-Language") == null) {
            builder.header("Accept-Language", DEFAULT_ACCEPT_LANGUAGE)
        }
        if (defaultAccept != null && original.header("Accept") == null) {
            builder.header("Accept", defaultAccept)
        }

        chain.proceed(builder.build())
    }

    fun createBaseOkHttpClient(cache: Cache? = null): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .addInterceptor(defaultHeadersInterceptor())
            .apply {
                if (cache != null) {
                    cache(cache)
                }
            }
            .build()

    fun createImageOkHttpClient(baseClient: OkHttpClient? = null): OkHttpClient =
        (baseClient?.newBuilder() ?: createBaseOkHttpClient().newBuilder())
            .cache(null)
            .addInterceptor(defaultHeadersInterceptor(defaultAccept = DEFAULT_IMAGE_ACCEPT))
            .build()

    fun createHttpClient(okHttpClient: OkHttpClient, json: Json): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = true
            engine {
                preconfigured = okHttpClient
            }
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(ContentNegotiation) {
                json(json)
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                exponentialDelay()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
            }
        }

    fun createImageLoader(context: Context, okHttpClient: OkHttpClient): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
            }
            .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
        createBaseOkHttpClient(Cache(File(context.cacheDir, "http"), 50L * 1024 * 1024))

    @Provides
    @Singleton
    @ImageOkHttpClient
    fun provideImageOkHttpClient(baseClient: OkHttpClient): OkHttpClient =
        createImageOkHttpClient(baseClient)

    @Provides
    @Singleton
    fun provideHttpClient(okHttpClient: OkHttpClient, json: Json): HttpClient =
        createHttpClient(okHttpClient, json)
}
