package com.edham.logistics.feature.coldchain.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.coldchain.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ColdChainRepository {
    // Cold Chain Monitoring
    suspend fun startColdChainMonitoring(request: StartColdChainMonitoringRequest): Result<ColdChainShipment>
    suspend fun stopColdChainMonitoring(shipmentId: String): Result<Unit>
    suspend fun pauseColdChainMonitoring(shipmentId: String): Result<Unit>
    suspend fun resumeColdChainMonitoring(shipmentId: String): Result<Unit>
    
    // Temperature Updates
    suspend fun updateTemperature(request: UpdateTemperatureRequest): Result<TemperatureReading>
    suspend fun batchUpdateTemperatures(requests: List<UpdateTemperatureRequest>): Result<List<TemperatureReading>>
    
    // Cold Chain Status
    suspend fun getColdChainStatus(request: GetColdChainStatusRequest): Result<List<ColdChainShipment>>
    suspend fun getShipmentColdChainStatus(shipmentId: String): Result<ColdChainShipment>
    suspend fun observeShipmentColdChainStatus(shipmentId: String): Flow<Result<ColdChainShipment>>
    suspend fun getCustomerColdChainStatus(customerId: String): Result<List<ColdChainShipment>>
    suspend fun getDriverColdChainStatus(driverId: String): Result<List<ColdChainShipment>>
    
    // Temperature History
    suspend fun getTemperatureHistory(request: GetTemperatureHistoryRequest): Result<TemperatureHistory>
    suspend fun observeTemperatureHistory(shipmentId: String): Flow<Result<TemperatureHistory>>
    suspend fun getCustomerTemperatureHistory(customerId: String, limit: Int): Result<List<TemperatureHistory>>
    suspend fun getShipmentTemperatureSummary(shipmentId: String): Result<TemperatureSummary>
    
    // Temperature Alerts
    suspend fun createTemperatureAlert(request: CreateAlertRequest): Result<TemperatureAlert>
    suspend fun resolveTemperatureAlert(request: ResolveAlertRequest): Result<TemperatureAlert>
    suspend fun getActiveAlerts(shipmentId: String?): Result<List<TemperatureAlert>>
    suspend fun getAlertHistory(shipmentId: String, limit: Int = 50): Result<List<TemperatureAlert>>
    suspend fun observeAlerts(shipmentId: String): Flow<Result<List<TemperatureAlert>>>
    
    // Compliance and Reports
    suspend fun getComplianceReport(shipmentId: String): Result<ComplianceReport>
    suspend fun getCustomerComplianceReports(customerId: String, period: String): Result<List<ComplianceReport>>
    suspend fun getFleetComplianceReports(period: String): Result<List<ComplianceReport>>
    suspend fun generateColdChainReport(shipmentId: String, format: ReportFormat): Result<String>
    
    // Predictive Analysis
    suspend fun getPredictiveAnalysis(shipmentId: String): Result<PredictiveAnalysis>
    suspend fun observePredictiveAlerts(): Flow<Result<PredictiveAnalysis>>
    
    // Sensor Management
    suspend fun getSensorInfo(sensorId: String): Result<SensorInfo>
    suspend fun updateSensorCalibration(sensorId: String, calibrationData: Map<String, Any>): Result<SensorInfo>
    suspend fun getSensorStatus(sensorId: String): Result<SensorStatus>
    suspend fun observeSensorStatus(sensorId: String): Flow<Result<SensorStatus>>
    
    // Settings and Configuration
    suspend fun getColdChainSettings(shipmentId: String): Result<ColdChainSettings>
    suspend fun updateColdChainSettings(request: UpdateColdChainSettingsRequest): Result<ColdChainSettings>
    suspend fun getDefaultColdChainSettings(): Result<ColdChainSettings>
    
    // WebSocket/Real-time
    suspend fun connectToColdChainWebSocket(): Result<Unit>
    suspend fun disconnectFromColdChainWebSocket(): Result<Unit>
    suspend fun observeColdChainMessages(): Flow<Result<ColdChainWebSocketMessage>>
    suspend fun sendColdChainMessage(message: ColdChainWebSocketMessage): Result<Unit>
    
    // Analytics and Statistics
    suspend fun getColdChainStatistics(shipmentId: String): Result<Map<String, Any>>
    suspend fun getFleetColdChainStatistics(period: String): Result<Map<String, Any>>
    suspend fun getCustomerColdChainStatistics(customerId: String, period: String): Result<Map<String, Any>>
    
    // Threshold Management
    suspend fun getTemperatureThreshold(cargoType: CargoType): Result<TemperatureThreshold>
    suspend fun updateTemperatureThreshold(cargoType: CargoType, threshold: TemperatureThreshold): Result<TemperatureThreshold>
    suspend fun getCustomThreshold(shipmentId: String): Result<TemperatureThreshold>
    suspend fun setCustomThreshold(shipmentId: String, threshold: TemperatureThreshold): Result<TemperatureThreshold>
    
    // Alert Management
    suspend fun acknowledgeAlert(alertId: String, userId: String): Result<Unit>
    suspend fun escalateAlert(alertId: String, reason: String): Result<Unit>
    suspend fun getAlertTrends(period: String): Result<Map<String, Any>>
    
    // Data Export
    suspend fun exportTemperatureData(shipmentId: String, format: ExportFormat, dateRange: DateRange): Result<String>
    suspend fun exportComplianceData(customerId: String, format: ExportFormat, dateRange: DateRange): Result<String>
    suspend fun exportAlertData(format: ExportFormat, dateRange: DateRange): Result<String>
}

enum class ReportFormat {
    PDF,
    EXCEL,
    CSV,
    JSON
}

enum class ExportFormat {
    CSV,
    EXCEL,
    PDF,
    JSON
}

data class DateRange(
    val startTime: Long,
    val endTime: Long
)
