package com.edham.logistics.core.network

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enterprise Performance Monitor: Logs API latency and size for fleet intelligence.
 */
@Singleton
class PerformanceInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        
        Timber.v("📡 Outgoing: ${request.method} ${request.url}")

        val response = chain.proceed(request)
        val t2 = System.nanoTime()
        
        val durationMs = (t2 - t1) / 1e6
        val responseSize = response.body?.contentLength() ?: -1

        if (durationMs > 1000) {
            Timber.w("⚠️ SLOW API: ${request.url} took ${durationMs.toLong()}ms")
            // In a real app, send this to Firebase Performance or local Analytics
        }

        Timber.v("↩️ Incoming: ${response.code} (Size: $responseSize bytes, Time: ${durationMs.toLong()}ms)")

        return response
    }
}
