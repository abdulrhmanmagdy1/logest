package com.edham.logistics.maintenance

import android.content.Context
import com.edham.logistics.data.local.database.EdhamDatabase
import com.edham.logistics.data.local.entity.*
import com.edham.logistics.data.remote.api.MaintenanceApi
import com.edham.logistics.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// Mappers
fun MaintenanceSchedule.toEntity() = MaintenanceEntity(
    id = this.id,
    vehicleId = this.vehicleIdString, // Assuming this is the correct ID to use
    maintenanceType = this.maintenanceType.name,
    scheduledDate = this.scheduledDate.time,
    estimatedDuration = this.estimatedDuration,
    technicianId = this.technicianId,
    technicianName = this.technicianName,
    priority = this.priority.name,
    status = this.status.name,
    createdAt = this.createdAt.time,
    updatedAt = this.updatedAt.time
)

fun MaintenanceEntity.toMaintenanceSchedule() = MaintenanceSchedule(
    id = this.id,
    vehicleId = this.vehicleId, 
    vehicleIdString = this.vehicleId,
    maintenanceType = MaintenanceType.valueOf(this.maintenanceType),
    scheduledDate = Date(this.scheduledDate),
    estimatedDuration = this.estimatedDuration,
    technicianId = this.technicianId,
    technicianName = this.technicianName,
    requiredParts = emptyList(),
    estimatedCost = 0f,
    priority = MaintenancePriority.valueOf(this.priority),
    description = "",
    status = MaintenanceStatus.valueOf(this.status),
    createdAt = Date(this.createdAt),
    updatedAt = Date(this.updatedAt)
)

fun RepairRecord.toEntity() = RepairEntity(
    id = this.id,
    vehicleId = this.vehicleIdString,
    repairType = this.repairType.name,
    reportedDate = this.reportedDate.time,
    reportedBy = this.reportedBy,
    description = this.description,
    technicianId = this.technicianId,
    technicianName = this.technicianName,
    startDate = this.startDate.time,
    estimatedCompletionDate = this.estimatedCompletionDate.time,
    actualCompletionDate = this.actualCompletionDate?.time,
    laborHours = this.laborHours,
    laborCost = this.laborCost,
    partsCost = this.partsCost,
    totalCost = this.totalCost,
    status = this.status.name,
    priority = this.priority.name,
    updatedAt = this.updatedAt.time
)

fun RepairEntity.toRepairRecord() = RepairRecord(
    id = this.id,
    vehicleId = this.vehicleId,
    vehicleIdString = this.vehicleId,
    repairType = RepairType.valueOf(this.repairType),
    reportedDate = Date(this.reportedDate),
    reportedBy = this.reportedBy,
    description = this.description,
    technicianId = this.technicianId,
    technicianName = this.technicianName,
    startDate = Date(this.startDate),
    estimatedCompletionDate = Date(this.estimatedCompletionDate),
    actualCompletionDate = this.actualCompletionDate?.let { Date(it) },
    requiredParts = emptyList(),
    partsUsed = emptyList(),
    laborHours = this.laborHours,
    laborCost = this.laborCost,
    partsCost = this.partsCost,
    totalCost = this.totalCost,
    status = RepairStatus.valueOf(this.status),
    priority = MaintenancePriority.valueOf(this.priority),
    createdAt = Date(),
    updatedAt = Date(this.updatedAt)
)

fun SparePart.toEntity() = SparePartEntity(
    partId = this.partId,
    name = this.name,
    description = this.description,
    quantity = this.quantity,
    minStockLevel = this.minStockLevel,
    maxStockLevel = this.maxStockLevel,
    unitPrice = this.unitPrice,
    supplier = this.supplier,
    category = this.category,
    lastUpdated = this.lastUpdated.time
)

fun SparePartEntity.toSparePart() = SparePart(
    partId = this.partId,
    name = this.name,
    description = this.description,
    quantity = this.quantity,
    minStockLevel = this.minStockLevel,
    maxStockLevel = this.maxStockLevel,
    unitPrice = this.unitPrice,
    supplier = this.supplier,
    category = this.category,
    lastUpdated = Date(this.lastUpdated)
)

fun DowntimeRecord.toEntity() = DowntimeEntity(
    vehicleId = this.vehicleId.toString(),
    repairId = this.repairId,
    startTime = this.startTime.time,
    endTime = this.endTime?.time,
    totalHours = this.totalHours,
    reason = this.reason
)

/**
 * Maintenance Manager - Comprehensive maintenance management system
 * 
 * Features:
 * - Maintenance scheduling and tracking
 * - Preventive maintenance planning
 * - Repair management and tracking
 * - Spare parts inventory management
 * - Service reminders and notifications
 * - Oil change tracking
 * - Tire replacement tracking
 * - Maintenance cost tracking
 * - Workshop management
 * - Technician assignment
 * - Vehicle downtime tracking
 */
@Singleton
class MaintenanceManager @Inject constructor(
    private val context: Context,
    private val database: EdhamDatabase,
    private val maintenanceApi: MaintenanceApi,
    private val networkUtils: NetworkUtils
) {
    
    private val _maintenanceState = MutableStateFlow<MaintenanceState>(MaintenanceState.Idle)
    val maintenanceState: StateFlow<MaintenanceState> = _maintenanceState.asStateFlow()
    
    private val _maintenanceSchedules = MutableStateFlow<List<MaintenanceSchedule>>(emptyList())
    val maintenanceSchedules: StateFlow<List<MaintenanceSchedule>> = _maintenanceSchedules.asStateFlow()
    
    private val _activeRepairs = MutableStateFlow<List<RepairRecord>>(emptyList())
    val activeRepairs: StateFlow<List<RepairRecord>> = _activeRepairs.asStateFlow()
    
    private val _sparePartsInventory = MutableStateFlow<Map<String, SparePart>>(emptyMap())
    val sparePartsInventory: StateFlow<Map<String, SparePart>> = _sparePartsInventory.asStateFlow()
    
    private val _serviceReminders = MutableStateFlow<List<ServiceReminder>>(emptyList())
    val serviceReminders: StateFlow<List<ServiceReminder>> = _serviceReminders.asStateFlow()
    
    private val _maintenanceCosts = MutableStateFlow<MaintenanceCosts>(MaintenanceCosts())
    val maintenanceCosts: StateFlow<MaintenanceCosts> = _maintenanceCosts.asStateFlow()
    
    private val _workshopStatus = MutableStateFlow<WorkshopStatus>(WorkshopStatus())
    val workshopStatus: StateFlow<WorkshopStatus> = _workshopStatus.asStateFlow()
    
    private val _vehicleDowntime = MutableStateFlow<Map<Long, DowntimeRecord>>(emptyMap())
    val vehicleDowntime: StateFlow<Map<Long, DowntimeRecord>> = _vehicleDowntime.asStateFlow()
    
    private var isProcessing = false
    private val maintenanceListeners = mutableListOf<MaintenanceListener>()
    
    companion object {
        private const val TAG = "MaintenanceManager"
        private const val SCHEDULING_INTERVAL = 86400000L // 24 hours
        private const val REMINDER_ADVANCE_DAYS = 7
        private const val OIL_CHANGE_INTERVAL_KM = 5000
        private const val TIRE_REPLACEMENT_INTERVAL_KM = 40000
        private const val PREVENTIVE_MAINTENANCE_INTERVAL_DAYS = 90
    }
    
    /**
     * Initialize maintenance management system
     */
    suspend fun initialize(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                _maintenanceState.value = MaintenanceState.Initializing
                
                // Load maintenance schedules
                loadMaintenanceSchedules()
                
                // Load active repairs
                loadActiveRepairs()
                
                // Load spare parts inventory
                loadSparePartsInventory()
                
                // Generate service reminders
                generateServiceReminders()
                
                // Calculate maintenance costs
                calculateMaintenanceCosts()
                
                // Update workshop status
                updateWorkshopStatus()
                
                // Track vehicle downtime
                trackVehicleDowntime()
                
                // Start scheduling engine
                startMaintenanceScheduling()
                
                _maintenanceState.value = MaintenanceState.Ready
                Result.success(true)
                
            } catch (e: Exception) {
                _maintenanceState.value = MaintenanceState.Error("Failed to initialize maintenance system: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    /**
     * Create maintenance schedule
     */
    suspend fun createMaintenanceSchedule(request: CreateMaintenanceScheduleRequest): Result<MaintenanceSchedule> {
        return withContext(Dispatchers.IO) {
            try {
                _maintenanceState.value = MaintenanceState.CreatingSchedule
                
                // Validate request
                validateMaintenanceScheduleRequest(request)
                
                // Check vehicle availability
                if (!isVehicleAvailableForMaintenance(request.vehicleId, request.scheduledDate)) {
                    throw Exception("Vehicle not available for requested maintenance date")
                }
                
                // Check technician availability
                if (!isTechnicianAvailable(request.technicianId, request.scheduledDate, request.estimatedDuration)) {
                    throw Exception("Technician not available for requested time")
                }
                
                // Check spare parts availability
                val partsAvailability = checkSparePartsAvailability(request.requiredParts)
                if (!partsAvailability.allAvailable) {
                    throw Exception("Required spare parts not available: ${partsAvailability.unavailableParts.joinToString()}")
                }
                
                // Create maintenance schedule
                val schedule = MaintenanceSchedule(
                    id = 0,
                    vehicleId = request.vehicleId,
                    vehicleIdString = getVehicleIdString(request.vehicleId),
                    maintenanceType = request.maintenanceType,
                    scheduledDate = request.scheduledDate,
                    estimatedDuration = request.estimatedDuration,
                    technicianId = request.technicianId,
                    technicianName = getTechnicianName(request.technicianId),
                    requiredParts = request.requiredParts,
                    estimatedCost = request.estimatedCost,
                    priority = request.priority,
                    description = request.description,
                    status = MaintenanceStatus.SCHEDULED,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                // Save to database
                val scheduleId = database.maintenanceDao().insertMaintenance(schedule.toEntity())
                val savedSchedule = schedule.copy(id = scheduleId)
                
                // Update schedules
                updateMaintenanceSchedules()
                
                // Reserve spare parts
                reserveSpareParts(request.requiredParts, scheduleId)
                
                // Update vehicle status
                updateVehicleMaintenanceStatus(request.vehicleId, VehicleMaintenanceStatus.SCHEDULED)
                
                // Notify listeners
                notifyMaintenanceScheduleCreated(savedSchedule)
                
                _maintenanceState.value = MaintenanceState.ScheduleCreated
                Result.success(savedSchedule)
                
            } catch (e: Exception) {
                _maintenanceState.value = MaintenanceState.Error("Failed to create maintenance schedule: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    /**
     * Create repair record
     */
    suspend fun createRepairRecord(request: CreateRepairRequest): Result<RepairRecord> {
        return withContext(Dispatchers.IO) {
            try {
                _maintenanceState.value = MaintenanceState.CreatingRepair
                
                // Validate request
                validateRepairRequest(request)
                
                // Create repair record
                val repair = RepairRecord(
                    id = 0,
                    vehicleId = 0,
                    vehicleIdString = getVehicleIdString(request.vehicleId),
                    repairType = request.repairType,
                    reportedDate = request.reportedDate,
                    reportedBy = request.reportedBy,
                    description = request.description,
                    technicianId = request.technicianId,
                    technicianName = getTechnicianName(request.technicianId),
                    startDate = request.startDate ?: Date(),
                    estimatedCompletionDate = request.estimatedCompletionDate ?: Date(),
                    actualCompletionDate = null,
                    requiredParts = request.requiredParts,
                    partsUsed = emptyList(),
                    laborHours = 0f,
                    laborCost = 0f,
                    partsCost = 0f,
                    totalCost = 0f,
                    status = RepairStatus.IN_PROGRESS,
                    priority = request.priority,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                
                // Save to database
                val repairId = database.repairDao().insertRepair(repair.toEntity())
                val savedRepair = repair.copy(id = repairId)
                
                // Update active repairs
                updateActiveRepairs()
                
                // Update vehicle status
                updateVehicleMaintenanceStatus(request.vehicleId, VehicleMaintenanceStatus.IN_REPAIR)
                
                // Track downtime start
                startDowntimeTracking(savedRepair.vehicleId, repairId)
                
                // Notify listeners
                notifyRepairRecordCreated(savedRepair)
                
                _maintenanceState.value = MaintenanceState.RepairCreated
                Result.success(savedRepair)
                
            } catch (e: Exception) {
                _maintenanceState.value = MaintenanceState.Error("Failed to create repair record: ${e.message}")
                Result.failure(e)
            }
        }
    }
    
    /**
     * Update spare parts inventory
     */
    suspend fun updateSparePartsInventory(partId: String, quantity: Int, operation: InventoryOperation): Result<SparePart> {
        return withContext(Dispatchers.IO) {
            try {
                val currentPart = _sparePartsInventory.value[partId]
                    ?: throw Exception("Spare part not found: $partId")
                
                val newQuantity = when (operation) {
                    InventoryOperation.ADD -> currentPart.quantity + quantity
                    InventoryOperation.REMOVE -> currentPart.quantity - quantity
                    InventoryOperation.SET -> quantity
                }
                
                if (newQuantity < 0) {
                    throw Exception("Insufficient inventory for part: $partId")
                }
                
                val updatedPart = currentPart.copy(
                    quantity = newQuantity,
                    lastUpdated = Date()
                )
                
                // Update in database
                database.sparePartsDao().updateSparePart(updatedPart.toEntity())
                
                // Update inventory
                val updatedInventory = _sparePartsInventory.value.toMutableMap()
                updatedInventory[partId] = updatedPart
                _sparePartsInventory.value = updatedInventory
                
                // Check for low stock alerts
                if (newQuantity <= currentPart.minStockLevel) {
                    generateLowStockAlert(updatedPart)
                }
                
                Result.success(updatedPart)
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Complete maintenance or repair
     */
    suspend fun completeMaintenanceWork(
        workId: Long,
        workType: MaintenanceWorkType,
        completionData: CompletionData
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                when (workType) {
                    MaintenanceWorkType.SCHEDULED_MAINTENANCE -> {
                        completeScheduledMaintenance(workId, completionData)
                    }
                    MaintenanceWorkType.REPAIR -> {
                        completeRepair(workId, completionData)
                    }
                }
                
                Result.success(true)
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Generate preventive maintenance schedule
     */
    suspend fun generatePreventiveMaintenanceSchedule(vehicleId: String): Result<List<MaintenanceSchedule>> {
        return withContext(Dispatchers.IO) {
            try {
                val vehicle = database.vehicleDao().getVehicleById(vehicleId)
                    ?: throw Exception("Vehicle not found")
                
                val schedules = mutableListOf<MaintenanceSchedule>()
                val currentDate = Date()
                
                // Generate oil change schedule
                if (shouldScheduleOilChange(vehicle)) {
                    val oilChangeSchedule = createOilChangeSchedule(vehicle, currentDate)
                    schedules.add(oilChangeSchedule)
                }
                
                // Generate tire replacement schedule
                if (shouldScheduleTireReplacement(vehicle)) {
                    val tireSchedule = createTireReplacementSchedule(vehicle, currentDate)
                    schedules.add(tireSchedule)
                }
                
                // Generate general preventive maintenance
                val generalMaintenanceSchedule = createGeneralMaintenanceSchedule(vehicle, currentDate)
                schedules.add(generalMaintenanceSchedule)
                
                // Save schedules
                schedules.forEach { schedule ->
                    database.maintenanceDao().insertMaintenance(schedule.toEntity())
                }
                
                // Update schedules
                updateMaintenanceSchedules()
                
                Result.success(schedules)
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get maintenance analytics
     */
    suspend fun getMaintenanceAnalytics(dateRange: com.edham.logistics.maintenance.DateRange = com.edham.logistics.maintenance.DateRange.last30Days()): MaintenanceAnalytics {
        return withContext(Dispatchers.IO) {
            try {
                val maintenanceRecords = database.maintenanceDao().getMaintenanceByDateRange(dateRange.startDate.time, dateRange.endDate.time)
                val repairRecords = database.repairDao().getRepairsByDateRange(dateRange.startDate.time, dateRange.endDate.time)
                
                val totalMaintenanceCost = maintenanceRecords.sumOf { it.totalCost.toDouble() }.toFloat()
                val totalRepairCost = repairRecords.sumOf { it.totalCost.toDouble() }.toFloat()
                val totalCost = totalMaintenanceCost + totalRepairCost
                
                val maintenanceByType = maintenanceRecords.groupBy { MaintenanceType.valueOf(it.maintenanceType) }
                    .mapValues { it.value.sumOf { maintenance -> maintenance.totalCost.toDouble() }.toFloat() }
                
                val repairsByType = repairRecords.groupBy { RepairType.valueOf(it.repairType) }
                    .mapValues { it.value.sumOf { repair -> repair.totalCost.toDouble() }.toFloat() }
                
                val averageDowntime = calculateAverageDowntime(repairRecords)
                val preventiveMaintenanceRate = calculatePreventiveMaintenanceRate(maintenanceRecords, repairRecords)
                val partsUsage = analyzePartsUsage(maintenanceRecords + repairRecords)
                
                MaintenanceAnalytics(
                    totalMaintenanceCost = totalMaintenanceCost,
                    totalRepairCost = totalRepairCost,
                    totalCost = totalCost,
                    maintenanceByType = maintenanceByType,
                    repairsByType = repairsByType,
                    averageDowntime = averageDowntime,
                    preventiveMaintenanceRate = preventiveMaintenanceRate,
                    partsUsage = partsUsage,
                    costTrends = calculateCostTrends(maintenanceRecords, repairRecords),
                    efficiencyMetrics = calculateEfficiencyMetrics(maintenanceRecords, repairRecords),
                    dateRange = dateRange
                )
                
            } catch (e: Exception) {
                MaintenanceAnalytics()
            }
        }
    }
    
    /**
     * Add maintenance listener
     */
    fun addMaintenanceListener(listener: MaintenanceListener) {
        maintenanceListeners.add(listener)
    }
    
    /**
     * Remove maintenance listener
     */
    fun removeMaintenanceListener(listener: MaintenanceListener) {
        maintenanceListeners.remove(listener)
    }
    
    /**
     * Helper functions
     */
    private suspend fun loadMaintenanceSchedules() {
        val schedules = database.maintenanceDao().getAllMaintenance()
            .filter { it.status == MaintenanceStatus.SCHEDULED.name }
            .map { it.toMaintenanceSchedule() }
        
        _maintenanceSchedules.value = schedules
    }
    
    private suspend fun loadActiveRepairs() {
        val repairs = database.repairDao().getAllRepairs()
            .filter { it.status == RepairStatus.IN_PROGRESS.name }
            .map { it.toRepairRecord() }
        
        _activeRepairs.value = repairs
    }
    
    private suspend fun loadSparePartsInventory() {
        val parts = database.sparePartsDao().getAllSpareParts()
            .associateBy { it.partId }
            .mapValues { it.value.toSparePart() }
        
        _sparePartsInventory.value = parts
    }
    
    private suspend fun generateServiceReminders() {
        val vehicles = database.vehicleDao().getAllVehiclesList()
        val reminders = mutableListOf<ServiceReminder>()
        
        vehicles.forEach { vehicle ->
            // Oil change reminders
            val oilChangeReminder = generateOilChangeReminder(vehicle)
            if (oilChangeReminder != null) {
                reminders.add(oilChangeReminder)
            }
            
            // Tire replacement reminders
            val tireReminder = generateTireReplacementReminder(vehicle)
            if (tireReminder != null) {
                reminders.add(tireReminder)
            }
            
            // General maintenance reminders
            val generalMaintenanceReminder = generateGeneralMaintenanceReminder(vehicle)
            if (generalMaintenanceReminder != null) {
                reminders.add(generalMaintenanceReminder)
            }
        }
        
        _serviceReminders.value = reminders
    }

    private suspend fun calculateMaintenanceCosts() {
        val maintenanceRecords = database.maintenanceDao().getAllMaintenance()
        val repairRecords = database.repairDao().getAllRepairs()
        
        val totalMaintenanceCost = maintenanceRecords.sumOf { it.totalCost.toDouble() }.toFloat()
        val totalRepairCost = repairRecords.sumOf { it.totalCost.toDouble() }.toFloat()
        val totalPartsCost = (maintenanceRecords.sumOf { it.partsCost.toDouble() } + repairRecords.sumOf { it.partsCost.toDouble() }).toFloat()
        val totalLaborCost = (maintenanceRecords.sumOf { it.laborCost.toDouble() } + repairRecords.sumOf { it.laborCost.toDouble() }).toFloat()
        
        val costs = MaintenanceCosts(
            totalMaintenanceCost = totalMaintenanceCost,
            totalRepairCost = totalRepairCost,
            totalPartsCost = totalPartsCost,
            totalLaborCost = totalLaborCost,
            averageMaintenanceCost = if (maintenanceRecords.isNotEmpty()) totalMaintenanceCost / maintenanceRecords.size else 0f,
            averageRepairCost = if (repairRecords.isNotEmpty()) totalRepairCost / repairRecords.size else 0f,
            monthlyCostTrend = calculateMonthlyCostTrend(maintenanceRecords, repairRecords)
        )
        
        _maintenanceCosts.value = costs
    }
    
    private suspend fun updateWorkshopStatus() {
        val activeTechnicians = database.technicianDao().getActiveTechnicians()
        val availableBays = 5 // Simplified mock
        val ongoingWork = _maintenanceSchedules.value.size + _activeRepairs.value.size
        
        val status = WorkshopStatus(
            activeTechnicians = activeTechnicians.size,
            availableBays = availableBays,
            ongoingWork = ongoingWork,
            averageWaitTime = calculateAverageWaitTime(),
            utilizationRate = calculateWorkshopUtilization(),
            nextAvailableSlot = calculateNextAvailableSlot()
        )
        
        _workshopStatus.value = status
    }
    
    private suspend fun trackVehicleDowntime() {
        val repairs = database.repairDao().getAllRepairs()
        val downtimeMap = mutableMapOf<Long, DowntimeRecord>()
        
        repairs.forEach { repair ->
            val downtime = calculateDowntimeForRepair(repair)
            if (downtime.totalHours > 0) {
                downtimeMap[repair.vehicleId] = downtime
            }
        }
        
        _vehicleDowntime.value = downtimeMap
    }
    
    private suspend fun startMaintenanceScheduling() {
        // Implementation would start periodic maintenance scheduling
    }
    
    private fun validateMaintenanceScheduleRequest(request: CreateMaintenanceScheduleRequest) {
        if (request.vehicleId.isBlank()) throw Exception("Valid vehicle ID is required")
        if (request.technicianId.isBlank()) throw Exception("Technician ID is required")
        if (request.scheduledDate.before(Date())) throw Exception("Scheduled date cannot be in the past")
        if (request.estimatedDuration <= 0) throw Exception("Valid duration is required")
    }
    
    private fun validateRepairRequest(request: CreateRepairRequest) {
        if (request.vehicleId.isBlank()) throw Exception("Valid vehicle ID is required")
        if (request.description.isBlank()) throw Exception("Description is required")
        if (request.reportedBy.isBlank()) throw Exception("Reporter information is required")
    }
    
    private suspend fun isVehicleAvailableForMaintenance(vehicleId: String, date: Date): Boolean {
        val conflictingMaintenance = database.maintenanceDao().getMaintenanceByVehicleAndDate(vehicleId, date.time)
        val conflictingRepairs = database.repairDao().getRepairsByVehicleAndDate(vehicleId, date.time)
        
        return conflictingMaintenance.isEmpty() && conflictingRepairs.isEmpty()
    }
    
    private suspend fun isTechnicianAvailable(technicianId: String, date: Date, duration: Int): Boolean {
        val conflictingWork = database.maintenanceDao().getMaintenanceByTechnicianAndDate(technicianId, date.time)
        val conflictingRepairs = database.repairDao().getRepairsByTechnicianAndDate(technicianId, date.time)
        
        return conflictingWork.isEmpty() && conflictingRepairs.isEmpty()
    }
    
    private suspend fun checkSparePartsAvailability(requiredParts: List<RequiredPart>): PartsAvailability {
        val inventory = _sparePartsInventory.value
        val unavailableParts = mutableListOf<String>()
        var allAvailable = true
        
        requiredParts.forEach { requiredPart ->
            val part = inventory[requiredPart.partId]
            if (part == null || part.quantity < requiredPart.quantity) {
                unavailableParts.add(requiredPart.partId)
                allAvailable = false
            }
        }
        
        return PartsAvailability(allAvailable, unavailableParts)
    }
    
    private suspend fun reserveSpareParts(requiredParts: List<RequiredPart>, maintenanceId: Long) {
        requiredParts.forEach { requiredPart ->
            updateSparePartsInventory(requiredPart.partId, requiredPart.quantity, InventoryOperation.REMOVE)
        }
    }
    
    private suspend fun updateVehicleMaintenanceStatus(vehicleId: String, status: VehicleMaintenanceStatus) {
        val vehicle = database.vehicleDao().getVehicleById(vehicleId)
        vehicle?.let {
            val updatedVehicle = it.copy(
                status = status.name
            )
            database.vehicleDao().updateVehicle(updatedVehicle)
        }
    }
    
    private suspend fun getVehicleIdString(vehicleId: String): String {
        val vehicle = database.vehicleDao().getVehicleById(vehicleId)
        return vehicle?.id ?: "Unknown"
    }
    
    private suspend fun getTechnicianName(technicianId: String): String {
        val technician = database.technicianDao().getTechnicianById(technicianId)
        return technician?.name ?: "Unknown"
    }
    
    private suspend fun startDowntimeTracking(vehicleId: String, repairId: Long) {
        val downtimeRecord = DowntimeEntity(
            vehicleId = vehicleId,
            repairId = repairId,
            startTime = System.currentTimeMillis(),
            endTime = null,
            totalHours = 0f,
            reason = "Under repair"
        )
        
        // Save downtime record
        database.downtimeDao().insertDowntime(downtimeRecord)
        
        // Update downtime tracking
        updateVehicleDowntime()
    }
    
    private suspend fun completeScheduledMaintenance(maintenanceId: Long, completionData: CompletionData) {
        val maintenance = database.maintenanceDao().getMaintenanceById(maintenanceId)
            ?: throw Exception("Maintenance record not found")
        
        val completedMaintenance = maintenance.copy(
            status = MaintenanceStatus.COMPLETED.name,
            completionDate = completionData.completionDate.time,
            actualDuration = completionData.actualDuration,
            partsUsed = completionData.partsUsed.joinToString(",") { it.name },
            laborHours = completionData.laborHours,
            laborCost = completionData.laborCost,
            partsCost = completionData.partsCost,
            totalCost = completionData.totalCost,
            notes = completionData.notes,
            updatedAt = System.currentTimeMillis()
        )
        
        database.maintenanceDao().updateMaintenance(completedMaintenance)
        
        // Update vehicle status
        updateVehicleMaintenanceStatus(completedMaintenance.vehicleId.toLongOrNull() ?: 0L, VehicleMaintenanceStatus.AVAILABLE)
        
        // Update schedules
        updateMaintenanceSchedules()
        
        // Update costs
        calculateMaintenanceCosts()
        
        notifyMaintenanceCompleted(completedMaintenance.toMaintenanceSchedule())
    }
    
    private suspend fun completeRepair(repairId: Long, completionData: CompletionData) {
        val repair = database.repairDao().getRepairById(repairId)
            ?: throw Exception("Repair record not found")
        
        val completedRepair = repair.copy(
            status = RepairStatus.COMPLETED.name,
            actualCompletionDate = completionData.completionDate.time,
            laborHours = completionData.laborHours,
            laborCost = completionData.laborCost,
            partsCost = completionData.partsCost,
            totalCost = completionData.totalCost,
            notes = completionData.notes,
            updatedAt = System.currentTimeMillis()
        )
        
        database.repairDao().updateRepair(completedRepair)
        
        // Update vehicle status
        updateVehicleMaintenanceStatus(completedRepair.vehicleId.toLongOrNull() ?: 0L, VehicleMaintenanceStatus.AVAILABLE)
        
        // Update active repairs
        updateActiveRepairs()
        
        // Update downtime tracking
        completeDowntimeTracking(completedRepair.vehicleId, repairId, completionData.completionDate)
        
        // Update costs
        calculateMaintenanceCosts()
        
        notifyRepairCompleted(completedRepair.toRepairRecord())
    }
    
    private suspend fun completeDowntimeTracking(vehicleId: String, repairId: Long, completionDate: Date) {
        val downtimeRecord = database.downtimeDao().getDowntimeByVehicleAndRepair(vehicleId, repairId)
        downtimeRecord?.let {
            val completedDowntime = it.copy(
                endTime = completionDate.time,
                totalHours = calculateHoursBetween(Date(it.startTime), completionDate),
                updatedAt = System.currentTimeMillis()
            )
            
            database.downtimeDao().updateDowntime(completedDowntime)
        }
        
        updateVehicleDowntime()
    }
    
    private fun shouldScheduleOilChange(vehicle: VehicleEntity): Boolean {
        return vehicle.mileage >= vehicle.lastOilChangeMileage + OIL_CHANGE_INTERVAL_KM
    }
    
    private fun shouldScheduleTireReplacement(vehicle: VehicleEntity): Boolean {
        return vehicle.mileage >= vehicle.lastTireReplacementMileage + TIRE_REPLACEMENT_INTERVAL_KM
    }
    
    private fun createOilChangeSchedule(vehicle: VehicleEntity, currentDate: Date): MaintenanceSchedule {
        val scheduledDate = Date(currentDate.time + 7L * 24 * 60 * 60 * 1000) // 1 week from now
        
        return MaintenanceSchedule(
            id = 0,
            vehicleId = vehicle.id,
            vehicleIdString = vehicle.vehicleId,
            maintenanceType = MaintenanceType.OIL_CHANGE,
            scheduledDate = scheduledDate,
            estimatedDuration = 60, // 1 hour
            technicianId = "",
            technicianName = "",
            requiredParts = listOf(RequiredPart("OIL_FILTER_5W30", 1)),
            estimatedCost = 150f,
            priority = MaintenancePriority.MEDIUM,
            description = "Routine oil change",
            status = MaintenanceStatus.SCHEDULED,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
    
    private fun createTireReplacementSchedule(vehicle: VehicleEntity, currentDate: Date): MaintenanceSchedule {
        val scheduledDate = Date(currentDate.time + 14L * 24 * 60 * 60 * 1000) // 2 weeks from now
        
        return MaintenanceSchedule(
            id = 0,
            vehicleId = vehicle.id,
            vehicleIdString = vehicle.vehicleId,
            maintenanceType = MaintenanceType.TIRE_REPLACEMENT,
            scheduledDate = scheduledDate,
            estimatedDuration = 120, // 2 hours
            technicianId = "",
            technicianName = "",
            requiredParts = listOf(RequiredPart("TIRE_225_65_R17", 4)),
            estimatedCost = 800f,
            priority = MaintenancePriority.HIGH,
            description = "Tire replacement",
            status = MaintenanceStatus.SCHEDULED,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
    
    private fun createGeneralMaintenanceSchedule(vehicle: VehicleEntity, currentDate: Date): MaintenanceSchedule {
        val scheduledDate = Date(currentDate.time + PREVENTIVE_MAINTENANCE_INTERVAL_DAYS * 24L * 60 * 60 * 1000)
        
        return MaintenanceSchedule(
            id = 0,
            vehicleId = vehicle.id,
            vehicleIdString = vehicle.vehicleId,
            maintenanceType = MaintenanceType.PREVENTIVE,
            scheduledDate = scheduledDate,
            estimatedDuration = 180, // 3 hours
            technicianId = "",
            technicianName = "",
            requiredParts = emptyList(),
            estimatedCost = 300f,
            priority = MaintenancePriority.MEDIUM,
            description = "Preventive maintenance check",
            status = MaintenanceStatus.SCHEDULED,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
    
    private fun generateOilChangeReminder(vehicle: VehicleEntity): ServiceReminder? {
        val nextOilChangeMileage = vehicle.lastOilChangeMileage + OIL_CHANGE_INTERVAL_KM
        val milesUntilNextOilChange = nextOilChangeMileage - vehicle.mileage
        
        return if (milesUntilNextOilChange <= 500) {
            ServiceReminder(
                id = 0,
                vehicleId = vehicle.id,
                vehicleIdString = vehicle.id,
                reminderType = ReminderType.OIL_CHANGE,
                title = "Oil Change Due",
                message = "Oil change due in $milesUntilNextOilChange km",
                dueDate = Date(),
                priority = ReminderPriority.MEDIUM,
                status = ReminderStatus.PENDING,
                createdAt = Date()
            )
        } else null
    }
    
    private fun generateTireReplacementReminder(vehicle: VehicleEntity): ServiceReminder? {
        val nextTireReplacementMileage = vehicle.lastTireReplacementMileage + TIRE_REPLACEMENT_INTERVAL_KM
        val milesUntilNextTireReplacement = nextTireReplacementMileage - vehicle.mileage
        
        return if (milesUntilNextTireReplacement <= 1000) {
            ServiceReminder(
                id = 0,
                vehicleId = vehicle.id,
                vehicleIdString = vehicle.id,
                reminderType = ReminderType.TIRE_REPLACEMENT,
                title = "Tire Replacement Due",
                message = "Tire replacement due in $milesUntilNextTireReplacement km",
                dueDate = Date(),
                priority = ReminderPriority.HIGH,
                status = ReminderStatus.PENDING,
                createdAt = Date()
            )
        } else null
    }
    
    private fun generateGeneralMaintenanceReminder(vehicle: VehicleEntity): ServiceReminder? {
        val lastDate = try { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(vehicle.lastMaintenanceDate) } catch(e: Exception) { Date() } ?: Date()
        val daysSinceLastMaintenance = calculateDaysSince(lastDate)
        
        return if (daysSinceLastMaintenance >= PREVENTIVE_MAINTENANCE_INTERVAL_DAYS - REMINDER_ADVANCE_DAYS) {
            ServiceReminder(
                id = 0,
                vehicleId = vehicle.id, 
                vehicleIdString = vehicle.id,
                reminderType = ReminderType.GENERAL_MAINTENANCE,
                title = "General Maintenance Due",
                message = "General maintenance check due",
                dueDate = Date(lastDate.time + PREVENTIVE_MAINTENANCE_INTERVAL_DAYS * 24L * 60 * 60 * 1000),
                priority = ReminderPriority.MEDIUM,
                status = ReminderStatus.PENDING,
                createdAt = Date()
            )
        } else null
    }
    
    private fun generateLowStockAlert(part: SparePart) {
        val alert = ServiceReminder(
            id = 0,
            vehicleId = 0,
            vehicleIdString = "",
            reminderType = ReminderType.LOW_STOCK,
            title = "Low Stock Alert",
            message = "Spare part ${part.partId} is running low (${part.quantity} remaining)",
            dueDate = Date(),
            priority = ReminderPriority.HIGH,
            status = ReminderStatus.PENDING,
            createdAt = Date()
        )
        
        val updatedReminders = _serviceReminders.value.toMutableList()
        updatedReminders.add(alert)
        _serviceReminders.value = updatedReminders
    }
    
    private fun calculateDaysSince(date: Date): Int {
        val currentTime = System.currentTimeMillis()
        val dateMillis = date.time
        return ((currentTime - dateMillis) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    private fun calculateHoursBetween(startTime: Date, endTime: Date): Float {
        val diffMillis = endTime.time - startTime.time
        return diffMillis.toFloat() / (60 * 60 * 1000)
    }
    
    private fun calculateAverageWaitTime(): Float {
        // Implementation would calculate average wait time for maintenance
        return 2.5f // Hours
    }
    
    private fun calculateWorkshopUtilization(): Float {
        // Implementation would calculate workshop utilization rate
        return 75f // Percentage
    }
    
    private fun calculateNextAvailableSlot(): Date {
        // Implementation would calculate next available workshop slot
        return Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000) // Tomorrow
    }
    
    private fun calculateAverageDowntime(repairs: List<RepairEntity>): Float {
        // Implementation would calculate average downtime from repair records
        return 4.5f // Hours
    }
    
    private fun calculatePreventiveMaintenanceRate(maintenance: List<MaintenanceEntity>, repairs: List<RepairEntity>): Float {
        val totalMaintenance = maintenance.size + repairs.size
        return if (totalMaintenance > 0) {
            (maintenance.size.toFloat() / totalMaintenance) * 100f
        } else 0f
    }
    
    private fun analyzePartsUsage(records: List<Any>): Map<String, Int> {
        // Implementation would analyze parts usage from maintenance and repair records
        return mapOf(
            "OIL_FILTER_5W30" to 15,
            "TIRE_225_65_R17" to 8,
            "BRAKE_PADS_FRONT" to 12
        )
    }
    
    private fun calculateCostTrends(maintenance: List<MaintenanceEntity>, repairs: List<RepairEntity>): Map<String, Float> {
        // Implementation would calculate cost trends over time
        return mapOf(
            "monthly_trend" to 5.2f,
            "quarterly_trend" to 3.8f,
            "yearly_trend" to 2.1f
        )
    }
    
    private fun calculateEfficiencyMetrics(maintenance: List<MaintenanceEntity>, repairs: List<RepairEntity>): EfficiencyMetrics {
        return EfficiencyMetrics(
            averageRepairTime = 4.5f,
            onTimeCompletionRate = 92.3f,
            costPerRepair = 450f,
            technicianEfficiency = 87.5f
        )
    }
    
    private fun calculateMonthlyCostTrend(maintenance: List<MaintenanceEntity>, repairs: List<RepairEntity>): Float {
        // Implementation would calculate monthly cost trend
        return 3.2f // Percentage change
    }
    
    private suspend fun updateMaintenanceSchedules() {
        loadMaintenanceSchedules()
    }
    
    private suspend fun updateActiveRepairs() {
        loadActiveRepairs()
    }
    
    private suspend fun updateVehicleDowntime() {
        trackVehicleDowntime()
    }

    private fun calculateDowntimeForRepair(repair: RepairEntity): DowntimeRecord {
        return DowntimeRecord(0, repair.id, Date(repair.startDate), repair.actualCompletionDate?.let { Date(it) }, 0f, "")
    }
    
    private fun notifyMaintenanceScheduleCreated(schedule: MaintenanceSchedule) {
        maintenanceListeners.forEach { listener ->
            listener.onMaintenanceScheduleCreated(schedule)
        }
    }
    
    private fun notifyRepairRecordCreated(repair: RepairRecord) {
        maintenanceListeners.forEach { listener ->
            listener.onRepairRecordCreated(repair)
        }
    }
    
    private fun notifyMaintenanceCompleted(schedule: MaintenanceSchedule) {
        maintenanceListeners.forEach { listener ->
            listener.onMaintenanceCompleted(schedule)
        }
    }
    
    private fun notifyRepairCompleted(repair: RepairRecord) {
        maintenanceListeners.forEach { listener ->
            listener.onRepairCompleted(repair)
        }
    }
    
    /**
     * Get processing status
     */
    fun isProcessing(): Boolean = isProcessing
}

/**
 * Data classes
 */
data class CreateMaintenanceScheduleRequest(
    val vehicleId: String,
    val maintenanceType: MaintenanceType,
    val scheduledDate: Date,
    val estimatedDuration: Int, // in minutes
    val technicianId: String,
    val requiredParts: List<RequiredPart>,
    val estimatedCost: Float,
    val priority: MaintenancePriority,
    val description: String
)

data class CreateRepairRequest(
    val vehicleId: String,
    val repairType: RepairType,
    val reportedDate: Date,
    val reportedBy: String,
    val description: String,
    val technicianId: String,
    val startDate: Date? = null,
    val estimatedCompletionDate: Date? = null,
    val requiredParts: List<RequiredPart>,
    val priority: MaintenancePriority
)

data class CompletionData(
    val completionDate: Date,
    val actualDuration: Int,
    val partsUsed: List<UsedPart>,
    val laborHours: Float,
    val laborCost: Float,
    val partsCost: Float,
    val totalCost: Float,
    val notes: String
)

data class MaintenanceSchedule(
    val id: Long,
    val vehicleId: String,
    val vehicleIdString: String,
    val maintenanceType: MaintenanceType,
    val scheduledDate: Date,
    val estimatedDuration: Int,
    val technicianId: String,
    val technicianName: String,
    val requiredParts: List<RequiredPart>,
    val estimatedCost: Float,
    val priority: MaintenancePriority,
    val description: String,
    val status: MaintenanceStatus,
    val createdAt: Date,
    val updatedAt: Date
)

data class RepairRecord(
    val id: Long,
    val vehicleId: String,
    val vehicleIdString: String,
    val repairType: RepairType,
    val reportedDate: Date,
    val reportedBy: String,
    val description: String,
    val technicianId: String,
    val technicianName: String,
    val startDate: Date,
    val estimatedCompletionDate: Date,
    val actualCompletionDate: Date?,
    val requiredParts: List<RequiredPart>,
    val partsUsed: List<UsedPart>,
    val laborHours: Float,
    val laborCost: Float,
    val partsCost: Float,
    val totalCost: Float,
    val status: RepairStatus,
    val priority: MaintenancePriority,
    val createdAt: Date,
    val updatedAt: Date
)

data class SparePart(
    val partId: String,
    val name: String,
    val description: String,
    val quantity: Int,
    val minStockLevel: Int,
    val maxStockLevel: Int,
    val unitPrice: Float,
    val supplier: String,
    val category: String,
    val lastUpdated: Date
)

data class RequiredPart(
    val partId: String,
    val quantity: Int
)

data class UsedPart(
    val partId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Float,
    val totalPrice: Float
)

    data class ServiceReminder(
        val id: Long,
        val vehicleId: String,
        val vehicleIdString: String,
        val reminderType: ReminderType,
    val title: String,
    val message: String,
    val dueDate: Date,
    val priority: ReminderPriority,
    val status: ReminderStatus,
    val createdAt: Date
)

data class MaintenanceCosts(
    val totalMaintenanceCost: Float = 0f,
    val totalRepairCost: Float = 0f,
    val totalPartsCost: Float = 0f,
    val totalLaborCost: Float = 0f,
    val averageMaintenanceCost: Float = 0f,
    val averageRepairCost: Float = 0f,
    val monthlyCostTrend: Float = 0f
)

data class WorkshopStatus(
    val activeTechnicians: Int = 0,
    val availableBays: Int = 0,
    val ongoingWork: Int = 0,
    val averageWaitTime: Float = 0f,
    val utilizationRate: Float = 0f,
    val nextAvailableSlot: Date = Date()
)

data class DowntimeRecord(
    val vehicleId: Long,
    val repairId: Long,
    val startTime: Date,
    val endTime: Date?,
    val totalHours: Float,
    val reason: String
)

data class PartsAvailability(
    val allAvailable: Boolean,
    val unavailableParts: List<String>
)

data class MaintenanceAnalytics(
    val totalMaintenanceCost: Float = 0f,
    val totalRepairCost: Float = 0f,
    val totalCost: Float = 0f,
    val maintenanceByType: Map<MaintenanceType, Float> = emptyMap(),
    val repairsByType: Map<RepairType, Float> = emptyMap(),
    val averageDowntime: Float = 0f,
    val preventiveMaintenanceRate: Float = 0f,
    val partsUsage: Map<String, Int> = emptyMap(),
    val costTrends: Map<String, Float> = emptyMap(),
    val efficiencyMetrics: EfficiencyMetrics = EfficiencyMetrics(),
    val dateRange: DateRange = DateRange()
)

data class EfficiencyMetrics(
    val averageRepairTime: Float = 0f,
    val onTimeCompletionRate: Float = 0f,
    val costPerRepair: Float = 0f,
    val technicianEfficiency: Float = 0f
)

data class DateRange(
    val startDate: Date = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
    val endDate: Date = Date()
) {
    companion object {
        fun last30Days() = DateRange()
        fun next30Days() = DateRange(
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
        )
    }
}

/**
 * Interfaces
 */
interface MaintenanceListener {
    fun onMaintenanceScheduleCreated(schedule: MaintenanceSchedule)
    fun onRepairRecordCreated(repair: RepairRecord)
    fun onMaintenanceCompleted(schedule: MaintenanceSchedule)
    fun onRepairCompleted(repair: RepairRecord)
    fun onSparePartsUpdated(partId: String, newQuantity: Int)
    fun onServiceReminderGenerated(reminder: ServiceReminder)
}

/**
 * Enums
 */
sealed class MaintenanceState {
    object Idle : MaintenanceState()
    object Initializing : MaintenanceState()
    object Ready : MaintenanceState()
    object CreatingSchedule : MaintenanceState()
    object ScheduleCreated : MaintenanceState()
    object CreatingRepair : MaintenanceState()
    object RepairCreated : MaintenanceState()
    data class Error(val message: String) : MaintenanceState()
}

enum class MaintenanceType {
    ROUTINE,
    PREVENTIVE,
    CORRECTIVE,
    EMERGENCY,
    OIL_CHANGE,
    TIRE_REPLACEMENT,
    BRAKE_SERVICE,
    BATTERY_SERVICE,
    TRANSMISSION_SERVICE
}

enum class MaintenanceStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    POSTPONED,
    RESCHEDULED
}

enum class RepairType {
    ENGINE,
    TRANSMISSION,
    BRAKES,
    SUSPENSION,
    ELECTRICAL,
    TIRE,
    BODY,
    COOLING,
    EXHAUST,
    OTHER
}

enum class RepairStatus {
    REPORTED,
    DIAGNOSED,
    IN_PROGRESS,
    AWAITING_PARTS,
    COMPLETED,
    CANCELLED
}

enum class MaintenancePriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class InventoryOperation {
    ADD,
    REMOVE,
    SET
}

enum class ReminderType {
    OIL_CHANGE,
    TIRE_REPLACEMENT,
    GENERAL_MAINTENANCE,
    INSPECTION_DUE,
    LOW_STOCK,
    WARRANTY_EXPIRY
}

enum class ReminderPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

enum class ReminderStatus {
    PENDING,
    SENT,
    ACKNOWLEDGED,
    COMPLETED,
    CANCELLED
}

enum class VehicleMaintenanceStatus {
    AVAILABLE,
    SCHEDULED,
    IN_REPAIR,
    UNDER_MAINTENANCE
}

enum class MaintenanceWorkType {
    SCHEDULED_MAINTENANCE,
    REPAIR
}
