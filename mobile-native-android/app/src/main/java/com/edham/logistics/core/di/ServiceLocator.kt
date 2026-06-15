package com.edham.logistics.core.di

import android.content.Context
import com.edham.logistics.BuildConfig
import com.edham.logistics.core.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Lightweight Service Locator — replaces Hilt until DI is reintroduced.
 *
 * Provides singleton access to Retrofit and lazy-initialized API services.
 */
object ServiceLocator {

    private var _retrofit: Retrofit? = null
    private var _okHttp: OkHttpClient? = null

    /** Must be called once from [LogisticsApplication.onCreate] before any API call. */
    fun init(context: Context) {
        if (_retrofit != null) return

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        _okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .connectTimeout(BuildConfig.API_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(BuildConfig.API_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.API_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()

        _retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(_okHttp!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Also wire the legacy RetrofitClient so existing code keeps working.
        com.edham.logistics.core.network.RetrofitClient.initialize(_retrofit!!)
    }

    val retrofit: Retrofit
        get() = _retrofit ?: throw IllegalStateException("ServiceLocator.init() not called")

    val okHttp: OkHttpClient
        get() = _okHttp ?: throw IllegalStateException("ServiceLocator.init() not called")

    inline fun <reified T> api(): T = retrofit.create(T::class.java)
}
