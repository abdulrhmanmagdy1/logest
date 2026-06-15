package com.edham.logistics.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor to measure API latency (Heartbeat) for the Supervisor Dashboard.
 */
@Singleton
class LatencyInterceptor @Inject constructor() : Interceptor {

    private val lastLatency = AtomicLong(0)

    fun getLastLatency(): Long = lastLatency.get()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        val response = chain.proceed(request)
        
        val endTime = System.currentTimeMillis()
        lastLatency.set(endTime - startTime)
        
        return response
    }
}
