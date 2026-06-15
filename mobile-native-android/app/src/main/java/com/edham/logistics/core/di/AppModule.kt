package com.edham.logistics.core.di

import android.content.Context
import androidx.room.Room
import com.edham.logistics.core.database.AppDatabase
import com.edham.logistics.core.network.ApiService
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.core.network.api.ShipmentApi
import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.core.utils.Constants
import com.edham.logistics.data.local.dao.DriverFeatureDao
import com.edham.logistics.data.local.dao.InvoiceDao
import com.edham.logistics.data.local.dao.ShipmentDao
import com.edham.logistics.data.local.dao.VehicleDao
import com.edham.logistics.data.local.database.dao.DocumentDao
import com.edham.logistics.data.local.database.dao.DriverDao
import com.edham.logistics.data.local.database.dao.LocationDao
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVoiceAssistant(@ApplicationContext context: Context): com.edham.logistics.core.voice.TacticalVoiceAssistant {
        return com.edham.logistics.core.voice.TacticalVoiceAssistant(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "edham_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): com.edham.logistics.core.network.NetworkMonitor {
        return com.edham.logistics.core.network.NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        latencyInterceptor: com.edham.logistics.core.network.LatencyInterceptor,
        performanceInterceptor: com.edham.logistics.core.network.PerformanceInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(latencyInterceptor)
            .addInterceptor(performanceInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDriverApi(retrofit: Retrofit): DriverApi {
        return retrofit.create(DriverApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupervisorApi(retrofit: Retrofit): SupervisorApi {
        return retrofit.create(SupervisorApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountantApi(retrofit: Retrofit): com.edham.logistics.core.network.api.AccountantApi {
        return retrofit.create(com.edham.logistics.core.network.api.AccountantApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkshopApi(retrofit: Retrofit): com.edham.logistics.core.network.api.WorkshopApi {
        return retrofit.create(com.edham.logistics.core.network.api.WorkshopApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShipmentApi(retrofit: Retrofit): ShipmentApi {
        return retrofit.create(ShipmentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDriverFeatureDao(database: AppDatabase): DriverFeatureDao {
        return database.driverFeatureDao()
    }

    @Provides
    @Singleton
    fun provideShipmentDao(database: AppDatabase): ShipmentDao {
        return database.shipmentDao()
    }

    @Provides
    @Singleton
    fun provideDriverDao(database: AppDatabase): DriverDao {
        return database.driverDao()
    }

    @Provides
    @Singleton
    fun provideVehicleDao(database: AppDatabase): VehicleDao {
        return database.vehicleDao()
    }

    @Provides
    @Singleton
    fun provideInvoiceDao(database: AppDatabase): InvoiceDao {
        return database.invoiceDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: AppDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideDocumentDao(database: AppDatabase): DocumentDao {
        return database.documentDao()
    }
}
