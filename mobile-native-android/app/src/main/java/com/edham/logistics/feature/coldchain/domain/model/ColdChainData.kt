package com.edham.logistics.feature.coldchain.domain.model

data class TemperatureReading(
    val id: String,
    val shipmentId: String,
    val sensorId: String,
    val temperature: Double,
    val humidity: Double?,
    val timestamp: Long,
    val location: com.google.android.gms.maps.model.LatLng?,
    val isWithinRange: Boolean,
    val alertLevel: AlertLevel,
    val sensorStatus: SensorStatus,
    val batteryLevel: Float?,
    val signalStrength: Float?
)

data class ColdChainShipment(
    val id: String,
    val shipmentId: String,
    val cargoType: CargoType,
    val temperatureThreshold: TemperatureThreshold,
    val currentTemperature: Double,
    val currentHumidity: Double?,
    val lastUpdate: Long,
    val status: ColdChainStatus,
    val alertCount: Int,
    val totalReadings: Int,
    val complianceRate: Double, // percentage
    val sensorInfo: SensorInfo,
    val location: com.google.android.gms.maps.model.LatLng?,
    val estimatedDuration: Long?, // remaining time in minutes
    val timeInCurrentRange: Long, // minutes
    val totalAlertTime: Long // minutes
)

data class TemperatureThreshold(
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHumidity: Double?,
    val maxHumidity: Double?,
    val criticalMin: Double,
    val criticalMax: Double,
    val warningThreshold: Double, // degrees from threshold for warning
    val alertCooldown: Long // minutes between alerts
)

data class SensorInfo(
    val id: String,
    val model: String,
    val firmware: String,
    val lastCalibration: Long,
    val calibrationDue: Long,
    val batteryLevel: Float,
    val signalStrength: Float,
    val status: SensorStatus,
    val location: com.google.android.gms.maps.model.LatLng?,
    val installationDate: Long
)

data class TemperatureAlert(
    val id: String,
    val shipmentId: String,
    val sensorId: String,
    val alertType: AlertType,
    val alertLevel: AlertLevel,
    val temperature: Double,
    val threshold: Double,
    val humidity: Double?,
    val timestamp: Long,
    val location: com.google.android.gms.maps.model.LatLng?,
    val duration: Long, // minutes
    val resolved: Boolean,
    val resolvedAt: Long?,
    val resolvedBy: String?,
    val notes: String?,
    val impact: AlertImpact
)

data class TemperatureHistory(
    val shipmentId: String,
    val readings: List<TemperatureReading>,
    val alerts: List<TemperatureAlert>,
    val summary: TemperatureSummary,
    val complianceReport: ComplianceReport
)

data class TemperatureSummary(
    val averageTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val averageHumidity: Double?,
    val minHumidity: Double?,
    val maxHumidity: Double?,
    val totalReadings: Int,
    val complianceRate: Double,
    val timeInOptimalRange: Long, // minutes
    val timeInWarningRange: Long, // minutes
    val timeInCriticalRange: Long, // minutes
    val totalDuration: Long, // minutes
    val temperatureVariance: Double
)

data class ComplianceReport(
    val shipmentId: String,
    val cargoType: CargoType,
    val overallCompliance: Double, // percentage
    val temperatureCompliance: Double,
    val humidityCompliance: Double?,
    val alertCount: Int,
    val criticalAlerts: Int,
    val warningAlerts: Int,
    val violations: List<TemperatureViolation>,
    val recommendations: List<String>,
    val grade: ComplianceGrade
)

data class TemperatureViolation(
    val id: String,
    val startTime: Long,
    val endTime: Long?,
    val duration: Long, // minutes
    val minTemperature: Double,
    val maxTemperature: Double,
    val averageTemperature: Double,
    val violationType: ViolationType,
    val severity: AlertLevel,
    val impact: String
)

data class ColdChainSettings(
    val alertNotifications: Boolean,
    val emailAlerts: Boolean,
    val smsAlerts: Boolean,
    val pushNotifications: Boolean,
    val alertCooldown: Long,
    val autoResolveAlerts: Boolean,
    val reportFrequency: ReportFrequency,
    val complianceThreshold: Double,
    val enablePredictiveAlerts: Boolean
)

data class PredictiveAnalysis(
    val shipmentId: String,
    val currentTrend: TemperatureTrend,
    val predictedTemperature: Double,
    val timeToThreshold: Long?, // minutes
    val riskLevel: RiskLevel,
    val recommendations: List<String>,
    val confidence: Double // percentage
)

// Enums
enum class CargoType {
    FOOD,
    MEDICAL,
    FROZEN_GOODS,
    PHARMACEUTICAL,
    CHEMICAL,
    PERISHABLE,
    DAIRY,
    MEAT,
    VEGETABLES,
    FRUITS,
    BAKERY
}

enum class ColdChainStatus {
    NORMAL,
    WARNING,
    CRITICAL,
    OFFLINE,
    MAINTENANCE,
    CALIBRATION_REQUIRED,
    SENSOR_ERROR
}

enum class AlertLevel {
    INFO,
    WARNING,
    CRITICAL,
    EMERGENCY
}

enum class AlertType {
    TEMPERATURE_HIGH,
    TEMPERATURE_LOW,
    HUMIDITY_HIGH,
    HUMIDITY_LOW,
    SENSOR_OFFLINE,
    BATTERY_LOW,
    CALIBRATION_DUE,
    RAPID_CHANGE,
    PREDICTIVE_ALERT
}

enum class SensorStatus {
    ONLINE,
    OFFLINE,
    LOW_BATTERY,
    CALIBRATION_REQUIRED,
    ERROR,
    MAINTENANCE
}

enum class AlertImpact {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ComplianceGrade {
    EXCELLENT,
    GOOD,
    ACCEPTABLE,
    POOR,
    CRITICAL
}

enum class ReportFrequency {
    REAL_TIME,
    HOURLY,
    DAILY,
    WEEKLY
}

enum class TemperatureTrend {
    STABLE,
    RISING,
    FALLING,
    FLUCTUATING
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ViolationType {
    TEMPERATURE_HIGH,
    TEMPERATURE_LOW,
    HUMIDITY_HIGH,
    HUMIDITY_LOW,
    DURATION_EXCEEDED,
    RAPID_FLUCTUATION
}

// Request/Response models
data class StartColdChainMonitoringRequest(
    val shipmentId: String,
    val cargoType: CargoType,
    val sensorId: String,
    val customThreshold: TemperatureThreshold?
)

data class UpdateTemperatureRequest(
    val shipmentId: String,
    val sensorId: String,
    val temperature: Double,
    val humidity: Double?,
    val location: com.google.android.gms.maps.model.LatLng?,
    val batteryLevel: Float?,
    val signalStrength: Float?
)

data class GetTemperatureHistoryRequest(
    val shipmentId: String,
    val startTime: Long?,
    val endTime: Long?,
    val includeAlerts: Boolean
)

data class GetColdChainStatusRequest(
    val shipmentIds: List<String>?,
    val cargoTypes: List<CargoType>?,
    val alertLevels: List<AlertLevel>?,
    val status: List<ColdChainStatus>?
)

data class CreateAlertRequest(
    val shipmentId: String,
    val sensorId: String,
    val alertType: AlertType,
    val alertLevel: AlertLevel,
    val temperature: Double,
    val threshold: Double,
    val notes: String?
)

data class ResolveAlertRequest(
    val alertId: String,
    val resolvedBy: String,
    val notes: String?
)

data class UpdateColdChainSettingsRequest(
    val shipmentId: String,
    val settings: ColdChainSettings
)

// WebSocket/Real-time models
data class ColdChainWebSocketMessage(
    val type: ColdChainMessageType,
    val data: Any,
    val timestamp: Long,
    val shipmentId: String?,
    val sensorId: String?
)

enum class ColdChainMessageType {
    TEMPERATURE_UPDATE,
    ALERT_TRIGGERED,
    ALERT_RESOLVED,
    SENSOR_OFFLINE,
    BATTERY_LOW,
    COMPLIANCE_UPDATE,
    PREDICTIVE_ALERT,
    CALIBRATION_REQUIRED
}

// Predefined thresholds for different cargo types
object CargoTypeThresholds {
    val FOOD = TemperatureThreshold(
        minTemperature = 2.0,
        maxTemperature = 8.0,
        minHumidity = 50.0,
        maxHumidity = 70.0,
        criticalMin = 0.0,
        criticalMax = 12.0,
        warningThreshold = 1.0,
        alertCooldown = 15
    )
    
    val MEDICAL = TemperatureThreshold(
        minTemperature = 15.0,
        maxTemperature = 25.0,
        minHumidity = 30.0,
        maxHumidity = 60.0,
        criticalMin = 10.0,
        criticalMax = 30.0,
        warningThreshold = 2.0,
        alertCooldown = 10
    )
    
    val FROZEN_GOODS = TemperatureThreshold(
        minTemperature = -25.0,
        maxTemperature = -15.0,
        minHumidity = null,
        maxHumidity = null,
        criticalMin = -30.0,
        criticalMax = -10.0,
        warningThreshold = 2.0,
        alertCooldown = 20
    )
    
    val PHARMACEUTICAL = TemperatureThreshold(
        minTemperature = 2.0,
        maxTemperature = 8.0,
        minHumidity = 45.0,
        maxHumidity = 65.0,
        criticalMin = 0.0,
        criticalMax = 12.0,
        warningThreshold = 1.0,
        alertCooldown = 5
    )
    
    val DAIRY = TemperatureThreshold(
        minTemperature = 1.0,
        maxTemperature = 7.0,
        minHumidity = null,
        maxHumidity = null,
        criticalMin = -2.0,
        criticalMax = 10.0,
        warningThreshold = 1.5,
        alertCooldown = 15
    )
    
    val MEAT = TemperatureThreshold(
        minTemperature = -1.0,
        maxTemperature = 4.0,
        minHumidity = 85.0,
        maxHumidity = 95.0,
        criticalMin = -3.0,
        criticalMax = 7.0,
        warningThreshold = 1.0,
        alertCooldown = 10
    )
    
    val VEGETABLES = TemperatureThreshold(
        minTemperature = 4.0,
        maxTemperature = 10.0,
        minHumidity = 90.0,
        maxHumidity = 98.0,
        criticalMin = 2.0,
        criticalMax = 12.0,
        warningThreshold = 1.5,
        alertCooldown = 20
    )
    
    val FRUITS = TemperatureThreshold(
        minTemperature = 5.0,
        maxTemperature = 12.0,
        minHumidity = 85.0,
        maxHumidity = 95.0,
        criticalMin = 3.0,
        criticalMax = 15.0,
        warningThreshold = 2.0,
        alertCooldown = 20
    )
    
    fun getThreshold(cargoType: CargoType): TemperatureThreshold {
        return when (cargoType) {
            CargoType.FOOD -> FOOD
            CargoType.MEDICAL -> MEDICAL
            CargoType.FROZEN_GOODS -> FROZEN_GOODS
            CargoType.PHARMACEUTICAL -> PHARMACEUTICAL
            CargoType.DAIRY -> DAIRY
            CargoType.MEAT -> MEAT
            CargoType.VEGETABLES -> VEGETABLES
            CargoType.FRUITS -> FRUITS
            else -> FOOD // Default
        }
    }
}
