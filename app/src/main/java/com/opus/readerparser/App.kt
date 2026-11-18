package com.opus.readerparser

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.opus.readerparser.core.di.ImageOkHttpClient
import com.opus.readerparser.core.di.NetworkModule
import com.opus.readerparser.data.local.search.SamsungSearchClient
import com.opus.readerparser.data.local.search.SearchIndexSyncer
import com.opus.readerparser.workers.CatalogSyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider, SingletonImageLoader.Factory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var searchClient: SamsungSearchClient

    @Inject
    lateinit var searchSyncer: SearchIndexSyncer

    @Inject
    @ImageOkHttpClient
    lateinit var imageOkHttpClient: OkHttpClient

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun newImageLoader(context: Context): ImageLoader =
        NetworkModule.createImageLoader(
            context,
            if (::imageOkHttpClient.isInitialized) imageOkHttpClient else NetworkModule.createImageOkHttpClient()
        )

    override fun onCreate() {
        super.onCreate()
        initSamsungSearch()
        scheduleCatalogSync()
    }

    private fun initSamsungSearch() {
        appScope.launch(Dispatchers.IO) {
            try {
                if (!searchClient.isAvailable()) {
                    Log.i(TAG, "Samsung Search not available — search integration disabled")
                    return@launch
                }
                if (!searchClient.registerSchema()) {
                    Log.w(TAG, "Samsung Search schema registration failed — sync disabled")
                    return@launch
                }
                searchSyncer.startObserving(appScope)
            } catch (e: Exception) {
                Log.w(TAG, "Samsung Search init failed — continuing without search", e)
            }
        }
    }

    private fun scheduleCatalogSync() {
        appScope.launch(Dispatchers.IO) {
            try {
                CatalogSyncWorker.schedule(this@App)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to schedule catalog sync worker", e)
            }
        }
    }

    companion object {
        private const val TAG = "App"
    }
}
