package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface SurveyApi {

    @POST("surveys")
    suspend fun submitSurvey(
        @Body request: SurveySubmitRequest
    ): Response<SurveyResponse>

    @GET("surveys/driver/{driverId}")
    suspend fun getDriverSurveys(
        @Path("driverId") driverId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<List<SurveyResponse>>

    @GET("surveys/shipment/{shipmentId}")
    suspend fun getShipmentSurvey(
        @Path("shipmentId") shipmentId: String
    ): Response<SurveyResponse>

    @GET("surveys/stats/driver/{driverId}")
    suspend fun getDriverSurveyStats(
        @Path("driverId") driverId: String
    ): Response<SurveyStatsResponse>

    @GET("surveys/stats")
    suspend fun getAllSurveyStats(): Response<SurveyStatsResponse>
}
