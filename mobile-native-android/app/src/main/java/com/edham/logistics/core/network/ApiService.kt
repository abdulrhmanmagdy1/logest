package com.edham.logistics.core.network

import com.edham.logistics.data.remote.dto.ShipmentDto
import com.edham.logistics.data.remote.dto.DriverDto
import com.edham.logistics.data.remote.dto.VehicleDto
import com.edham.logistics.data.remote.dto.InvoiceDto
import com.edham.logistics.data.remote.dto.LoginRequest
import com.edham.logistics.data.remote.dto.LoginResponse
import com.edham.logistics.data.remote.dto.request.LocationUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: String): Response<LoginResponse>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
    
    // Shipments
    @GET("shipments")
    suspend fun getShipments(): Response<List<ShipmentDto>>
    
    @GET("shipments/{id}")
    suspend fun getShipment(@Path("id") id: String): Response<ShipmentDto>
    
    @POST("shipments")
    suspend fun createShipment(@Body shipment: ShipmentDto): Response<ShipmentDto>
    
    @PUT("shipments/{id}")
    suspend fun updateShipment(@Path("id") id: String, @Body shipment: ShipmentDto): Response<ShipmentDto>
    
    @DELETE("shipments/{id}")
    suspend fun deleteShipment(@Path("id") id: String): Response<Unit>
    
    // Drivers
    @GET("drivers")
    suspend fun getDrivers(): Response<List<DriverDto>>
    
    @GET("drivers/{id}")
    suspend fun getDriver(@Path("id") id: String): Response<DriverDto>
    
    @POST("drivers")
    suspend fun createDriver(@Body driver: DriverDto): Response<DriverDto>
    
    @PUT("drivers/{id}")
    suspend fun updateDriver(@Path("id") id: String, @Body driver: DriverDto): Response<DriverDto>
    
    // Vehicles
    @GET("vehicles")
    suspend fun getVehicles(): Response<List<VehicleDto>>
    
    @GET("vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): Response<VehicleDto>
    
    @POST("vehicles")
    suspend fun createVehicle(@Body vehicle: VehicleDto): Response<VehicleDto>
    
    @PUT("vehicles/{id}")
    suspend fun updateVehicle(@Path("id") id: String, @Body vehicle: VehicleDto): Response<VehicleDto>
    
    // Invoices
    @GET("invoices")
    suspend fun getInvoices(): Response<List<InvoiceDto>>
    
    @GET("invoices/{id}")
    suspend fun getInvoice(@Path("id") id: String): Response<InvoiceDto>
    
    @POST("invoices")
    suspend fun createInvoice(@Body invoice: InvoiceDto): Response<InvoiceDto>
    
    @PUT("invoices/{id}")
    suspend fun updateInvoice(@Path("id") id: String, @Body invoice: InvoiceDto): Response<InvoiceDto>
    
    // Locations
    @POST("locations/update")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): Response<Unit>
    
    @GET("locations/current/{driverId}")
    suspend fun getCurrentLocation(@Path("driverId") driverId: String): Response<LocationUpdateRequest>
    
    @GET("locations/history/{driverId}")
    suspend fun getLocationHistory(@Path("driverId") driverId: String): Response<List<LocationUpdateRequest>>
    
    @POST("locations/batch-update")
    suspend fun batchUpdateLocations(@Body requests: List<LocationUpdateRequest>): Response<Unit>
}
