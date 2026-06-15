package com.edham.logistics.core.network

import retrofit2.Retrofit

/**
 * RetrofitClient provides a singleton access point to the Retrofit instance.
 * This is a convenience wrapper around the Hilt-provided Retrofit instance.
 */
object RetrofitClient {
    
    private var retrofitInstance: Retrofit? = null
    
    /**
     * Initialize the RetrofitClient with a Retrofit instance.
     * This should be called from the Application class or DI module.
     */
    fun initialize(retrofit: Retrofit) {
        retrofitInstance = retrofit
    }
    
    /**
     * Get the Retrofit instance.
     * Throws IllegalStateException if not initialized.
     */
    fun getInstance(): Retrofit {
        return retrofitInstance ?: throw IllegalStateException(
            "RetrofitClient not initialized. Call initialize() first."
        )
    }
    
    /**
     * Create an API service implementation.
     */
    inline fun <reified T> createApi(): T {
        return getInstance().create(T::class.java)
    }
    
    /**
     * Check if the client is initialized.
     */
    fun isInitialized(): Boolean {
        return retrofitInstance != null
    }
}
