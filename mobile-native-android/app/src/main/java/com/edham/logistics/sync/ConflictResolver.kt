package com.edham.logistics.sync

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Conflict Resolver - Handles conflict resolution between local and server data
 */
class ConflictResolver private constructor(context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: ConflictResolver? = null
        
        fun getInstance(context: Context): ConflictResolver {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConflictResolver(context).also { INSTANCE = it }
            }
        }
    }
    
    private val gson = Gson()
    private val jsonParser = JsonParser()
    
    private val _resolutionRules = MutableStateFlow<List<ResolutionRule>>(emptyList())
    val resolutionRules = _resolutionRules.asStateFlow()
    
    private val _conflictHistory = MutableStateFlow<List<ConflictResolution>>(emptyList())
    val conflictHistory = _conflictHistory.asStateFlow()
    
    init {
        initializeDefaultRules()
    }
    
    /**
     * Initialize default conflict resolution rules
     */
    private fun initializeDefaultRules() {
        val defaultRules = listOf(
            ResolutionRule(
                id = "server-wins-default",
                name = "Server Wins (Default)",
                description = "Always prefer server data over local data",
                entityType = "*",
                conflictType = ConflictType.DATA_MISMATCH,
                resolutionType = ResolutionType.SERVER_WINS,
                priority = RulePriority.LOW,
                enabled = true
            ),
            ResolutionRule(
                id = "latest-wins-timestamp",
                name = "Latest Timestamp Wins",
                description = "Prefer data with the most recent timestamp",
                entityType = "*",
                conflictType = ConflictType.TIMESTAMP_CONFLICT,
                resolutionType = ResolutionType.LATEST_WINS,
                priority = RulePriority.HIGH,
                enabled = true
            ),
            ResolutionRule(
                id = "user-preference-wins",
                name = "User Preference Wins",
                description = "Prefer user-modified data over system changes",
                entityType = "*",
                conflictType = ConflictType.USER_MODIFICATION,
                resolutionType = ResolutionType.USER_WINS,
                priority = RulePriority.HIGH,
                enabled = true
            ),
            ResolutionRule(
                id = "shipment-status-server",
                name = "Shipment Status: Server Wins",
                description = "Always prefer server status for shipments",
                entityType = "shipment",
                conflictType = ConflictType.STATUS_CONFLICT,
                resolutionType = ResolutionType.SERVER_WINS,
                priority = RulePriority.CRITICAL,
                enabled = true
            ),
            ResolutionRule(
                id = "driver-location-latest",
                name = "Driver Location: Latest Wins",
                description = "Always prefer latest driver location",
                entityType = "driver",
                conflictType = ConflictType.LOCATION_CONFLICT,
                resolutionType = ResolutionType.LATEST_WINS,
                priority = RulePriority.CRITICAL,
                enabled = true
            ),
            ResolutionRule(
                id = "manual-review-required",
                name = "Manual Review Required",
                description = "Require manual review for critical conflicts",
                entityType = "*",
                conflictType = ConflictType.CRITICAL_DATA_CONFLICT,
                resolutionType = ResolutionType.MANUAL_REVIEW,
                priority = RulePriority.CRITICAL,
                enabled = true
            )
        )
        
        _resolutionRules.value = defaultRules
    }
    
    /**
     * Detect conflict between local and server data
     */
    fun detectConflict(
        entityType: String,
        entityId: String,
        localData: String,
        serverData: String
    ): SyncConflict? {
        try {
            val localJson = jsonParser.parse(localData).asJsonObject
            val serverJson = jsonParser.parse(serverData).asJsonObject
            
            val conflicts = mutableListOf<ConflictDetail>()
            
            // Check for various conflict types
            checkTimestampConflict(localJson, serverJson)?.let { conflicts.add(it) }
            checkDataMismatch(localJson, serverJson)?.let { conflicts.add(it) }
            checkStatusConflict(localJson, serverJson)?.let { conflicts.add(it) }
            checkLocationConflict(localJson, serverJson)?.let { conflicts.add(it) }
            checkUserModification(localJson, serverJson)?.let { conflicts.add(it) }
            checkCriticalDataConflict(localJson, serverJson)?.let { conflicts.add(it) }
            
            if (conflicts.isNotEmpty()) {
                return SyncConflict(
                    id = UUID.randomUUID().toString(),
                    entityType = entityType,
                    entityId = entityId,
                    localData = localData,
                    serverData = serverData,
                    conflicts = conflicts,
                    detectedAt = System.currentTimeMillis(),
                    status = ConflictStatus.PENDING
                )
            }
            
        } catch (e: Exception) {
            // Handle parsing error
        }
        
        return null
    }
    
    /**
     * Check for timestamp conflict
     */
    private fun checkTimestampConflict(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val localTimestamp = getTimestamp(localJson)
        val serverTimestamp = getTimestamp(serverJson)
        
        return if (localTimestamp != null && serverTimestamp != null && localTimestamp != serverTimestamp) {
            ConflictDetail(
                type = ConflictType.TIMESTAMP_CONFLICT,
                field = "timestamp",
                localValue = localTimestamp.toString(),
                serverValue = serverTimestamp.toString(),
                severity = ConflictSeverity.MEDIUM,
                description = "Timestamp mismatch between local and server data"
            )
        } else null
    }
    
    /**
     * Check for data mismatch
     */
    private fun checkDataMismatch(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val localFields = localJson.keySet()
        val serverFields = serverJson.keySet()
        
        val mismatchedFields = mutableListOf<String>()
        
        // Check for different values in same fields
        val commonFields = localFields.intersect(serverFields)
        commonFields.forEach { field ->
            if (field != "timestamp" && field != "lastUpdated") {
                val localValue = localJson.get(field)
                val serverValue = serverJson.get(field)
                if (localValue != serverValue) {
                    mismatchedFields.add(field)
                }
            }
        }
        
        // Check for missing/extra fields
        val missingInLocal = serverFields - localFields
        val missingInServer = localFields - serverFields
        
        return if (mismatchedFields.isNotEmpty() || missingInLocal.isNotEmpty() || missingInServer.isNotEmpty()) {
            ConflictDetail(
                type = ConflictType.DATA_MISMATCH,
                field = "data",
                localValue = "Fields: ${localFields.joinToString(", ")}",
                serverValue = "Fields: ${serverFields.joinToString(", ")}",
                severity = ConflictSeverity.MEDIUM,
                description = "Data mismatch detected: ${mismatchedFields.joinToString(", ")}"
            )
        } else null
    }
    
    /**
     * Check for status conflict
     */
    private fun checkStatusConflict(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val localStatus = getStringField(localJson, "status")
        val serverStatus = getStringField(serverJson, "status")
        
        return if (localStatus != null && serverStatus != null && localStatus != serverStatus) {
            ConflictDetail(
                type = ConflictType.STATUS_CONFLICT,
                field = "status",
                localValue = localStatus,
                serverValue = serverStatus,
                severity = ConflictSeverity.HIGH,
                description = "Status conflict between local and server"
            )
        } else null
    }
    
    /**
     * Check for location conflict
     */
    private fun checkLocationConflict(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val localLocation = getStringField(localJson, "currentLocation") ?: 
                           getStringField(localJson, "location")
        val serverLocation = getStringField(serverJson, "currentLocation") ?: 
                            getStringField(serverJson, "location")
        
        return if (localLocation != null && serverLocation != null && localLocation != serverLocation) {
            ConflictDetail(
                type = ConflictType.LOCATION_CONFLICT,
                field = "location",
                localValue = localLocation,
                serverValue = serverLocation,
                severity = ConflictSeverity.MEDIUM,
                description = "Location conflict detected"
            )
        } else null
    }
    
    /**
     * Check for user modification
     */
    private fun checkUserModification(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val localModified = getBooleanField(localJson, "userModified") ?: false
        val serverModified = getBooleanField(serverJson, "userModified") ?: false
        
        return if (localModified && !serverModified) {
            ConflictDetail(
                type = ConflictType.USER_MODIFICATION,
                field = "userModified",
                localValue = localModified.toString(),
                serverValue = serverModified.toString(),
                severity = ConflictSeverity.HIGH,
                description = "Local data has user modifications not present on server"
            )
        } else null
    }
    
    /**
     * Check for critical data conflict
     */
    private fun checkCriticalDataConflict(localJson: JsonObject, serverJson: JsonObject): ConflictDetail? {
        val criticalFields = listOf("id", "trackingNumber", "amount", "paymentStatus")
        
        for (field in criticalFields) {
            val localValue = getStringField(localJson, field)
            val serverValue = getStringField(serverJson, field)
            
            if (localValue != null && serverValue != null && localValue != serverValue) {
                return ConflictDetail(
                    type = ConflictType.CRITICAL_DATA_CONFLICT,
                    field = field,
                    localValue = localValue,
                    serverValue = serverValue,
                    severity = ConflictSeverity.CRITICAL,
                    description = "Critical field conflict: $field"
                )
            }
        }
        
        return null
    }
    
    /**
     * Resolve conflict using configured rules
     */
    fun resolveConflict(conflict: SyncConflict): ConflictResolution {
        val applicableRules = getApplicableRules(conflict)
        val rule = applicableRules.firstOrNull() ?: getDefaultRule()
        
        val resolution = when (rule.resolutionType) {
            ResolutionType.SERVER_WINS -> resolveServerWins(conflict)
            ResolutionType.LATEST_WINS -> resolveLatestWins(conflict)
            ResolutionType.USER_WINS -> resolveUserWins(conflict)
            ResolutionType.MANUAL_REVIEW -> resolveManualReview(conflict)
            ResolutionType.MERGE -> resolveMerge(conflict)
            ResolutionType.CUSTOM -> resolveCustom(conflict, rule)
        }
        
        // Add to history
        val historyEntry = resolution.copy(
            resolvedAt = System.currentTimeMillis(),
            appliedRule = rule.id
        )
        _conflictHistory.value = _conflictHistory.value + historyEntry
        
        return resolution
    }
    
    /**
     * Get applicable rules for conflict
     */
    private fun getApplicableRules(conflict: SyncConflict): List<ResolutionRule> {
        return _resolutionRules.value
            .filter { it.enabled }
            .filter { rule ->
                rule.entityType == "*" || rule.entityType == conflict.entityType
            }
            .filter { rule ->
                conflict.conflicts.any { conflictDetail ->
                    conflictDetail.type == rule.conflictType
                }
            }
            .sortedBy { it.priority.ordinal }
    }
    
    /**
     * Get default rule
     */
    private fun getDefaultRule(): ResolutionRule {
        return _resolutionRules.value
            .find { it.id == "server-wins-default" }
            ?: ResolutionRule(
                id = "default",
                name = "Default Rule",
                description = "Default conflict resolution",
                entityType = "*",
                conflictType = ConflictType.DATA_MISMATCH,
                resolutionType = ResolutionType.SERVER_WINS,
                priority = RulePriority.LOW,
                enabled = true
            )
    }
    
    /**
     * Resolve with server wins
     */
    private fun resolveServerWins(conflict: SyncConflict): ConflictResolution {
        return ConflictResolution(
            id = UUID.randomUUID().toString(),
            conflictId = conflict.id,
            type = ResolutionType.SERVER_WINS,
            resolvedData = conflict.serverData,
            description = "Server data preferred over local data",
            appliedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Resolve with latest wins
     */
    private fun resolveLatestWins(conflict: SyncConflict): ConflictResolution {
        val localJson = jsonParser.parse(conflict.localData).asJsonObject
        val serverJson = jsonParser.parse(conflict.serverData).asJsonObject
        
        val localTimestamp = getTimestamp(localJson) ?: 0L
        val serverTimestamp = getTimestamp(serverJson) ?: 0L
        
        val resolvedData = if (localTimestamp > serverTimestamp) {
            conflict.localData
        } else {
            conflict.serverData
        }
        
        return ConflictResolution(
            id = UUID.randomUUID().toString(),
            conflictId = conflict.id,
            type = ResolutionType.LATEST_WINS,
            resolvedData = resolvedData,
            description = "Latest timestamp data preferred",
            appliedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Resolve with user wins
     */
    private fun resolveUserWins(conflict: SyncConflict): ConflictResolution {
        return ConflictResolution(
            id = UUID.randomUUID().toString(),
            conflictId = conflict.id,
            type = ResolutionType.USER_WINS,
            resolvedData = conflict.localData,
            description = "User-modified data preferred",
            appliedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Resolve with manual review
     */
    private fun resolveManualReview(conflict: SyncConflict): ConflictResolution {
        return ConflictResolution(
            id = UUID.randomUUID().toString(),
            conflictId = conflict.id,
            type = ResolutionType.MANUAL_REVIEW,
            resolvedData = conflict.serverData, // Default to server until manual review
            description = "Manual review required for this conflict",
            appliedAt = System.currentTimeMillis(),
            requiresManualAction = true
        )
    }
    
    /**
     * Resolve with merge
     */
    private fun resolveMerge(conflict: SyncConflict): ConflictResolution {
        val localJson = jsonParser.parse(conflict.localData).asJsonObject
        val serverJson = jsonParser.parse(conflict.serverData).asJsonObject
        
        val mergedJson = JsonObject()
        
        // Merge all fields from both objects
        val allFields = localJson.keySet() + serverJson.keySet()
        allFields.forEach { field ->
            when {
                field == "timestamp" || field == "lastUpdated" -> {
                    // Use latest timestamp
                    val localTimestamp = getTimestamp(localJson) ?: 0L
                    val serverTimestamp = getTimestamp(serverJson) ?: 0L
                    if (localTimestamp > serverTimestamp) {
                        mergedJson.add(field, localJson.get(field))
                    } else {
                        mergedJson.add(field, serverJson.get(field))
                    }
                }
                !localJson.has(field) -> {
                    mergedJson.add(field, serverJson.get(field))
                }
                !serverJson.has(field) -> {
                    mergedJson.add(field, localJson.get(field))
                }
                else -> {
                    // For conflicting fields, prefer server (can be customized)
                    mergedJson.add(field, serverJson.get(field))
                }
            }
        }
        
        return ConflictResolution(
            id = UUID.randomUUID().toString(),
            conflictId = conflict.id,
            type = ResolutionType.MERGE,
            resolvedData = gson.toJson(mergedJson),
            description = "Data merged from local and server",
            appliedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Resolve with custom rule
     */
    private fun resolveCustom(conflict: SyncConflict, rule: ResolutionRule): ConflictResolution {
        // Custom resolution logic based on rule
        return when (rule.id) {
            "shipment-status-server" -> resolveServerWins(conflict)
            "driver-location-latest" -> resolveLatestWins(conflict)
            else -> resolveServerWins(conflict)
        }
    }
    
    /**
     * Add custom resolution rule
     */
    fun addResolutionRule(rule: ResolutionRule) {
        val currentRules = _resolutionRules.value.toMutableList()
        currentRules.add(rule)
        _resolutionRules.value = currentRules
    }
    
    /**
     * Remove resolution rule
     */
    fun removeResolutionRule(ruleId: String) {
        val currentRules = _resolutionRules.value.toMutableList()
        currentRules.removeAll { it.id == ruleId }
        _resolutionRules.value = currentRules
    }
    
    /**
     * Update resolution rule
     */
    fun updateResolutionRule(rule: ResolutionRule) {
        val currentRules = _resolutionRules.value.toMutableList()
        val index = currentRules.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            currentRules[index] = rule
            _resolutionRules.value = currentRules
        }
    }
    
    /**
     * Get conflict resolution statistics
     */
    fun getResolutionStatistics(): ConflictResolutionStatistics {
        val history = _conflictHistory.value
        val rules = _resolutionRules.value
        
        return ConflictResolutionStatistics(
            totalConflictsResolved = history.size,
            conflictsByType = history.groupBy { it.type }.mapValues { it.value.size },
            conflictsByEntityType = history.groupBy { 
                val conflictId = it.conflictId
                // This would need to be tracked separately in a real implementation
                "unknown"
            }.mapValues { it.value.size },
            averageResolutionTime = if (history.isNotEmpty()) {
                history.map { it.resolvedAt - it.appliedAt }.average()
            } else 0.0,
            mostUsedRule = history.groupBy { it.appliedRule }.maxByOrNull { it.value.size }?.key,
            manualReviewCount = history.count { it.requiresManualAction }
        )
    }
    
    // Helper methods
    private fun getTimestamp(json: JsonObject): Long? {
        return try {
            when {
                json.has("timestamp") -> json.get("timestamp").asLong
                json.has("lastUpdated") -> json.get("lastUpdated").asLong
                json.has("updatedAt") -> json.get("updatedAt").asLong
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getStringField(json: JsonObject, fieldName: String): String? {
        return try {
            if (json.has(fieldName) && !json.get(fieldName).isJsonNull) {
                json.get(fieldName).asString
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getBooleanField(json: JsonObject, fieldName: String): Boolean? {
        return try {
            if (json.has(fieldName) && !json.get(fieldName).isJsonNull) {
                json.get(fieldName).asBoolean
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Data classes
 */
data class SyncConflict(
    val id: String,
    val entityType: String,
    val entityId: String,
    val localData: String,
    val serverData: String,
    val conflicts: List<ConflictDetail>,
    val detectedAt: Long,
    val status: ConflictStatus
)

data class ConflictDetail(
    val type: ConflictType,
    val field: String,
    val localValue: String,
    val serverValue: String,
    val severity: ConflictSeverity,
    val description: String
)

data class ConflictResolution(
    val id: String,
    val conflictId: String,
    val type: ResolutionType,
    val resolvedData: String,
    val description: String,
    val appliedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long = System.currentTimeMillis(),
    val appliedRule: String? = null,
    val requiresManualAction: Boolean = false
)

data class ResolutionRule(
    val id: String,
    val name: String,
    val description: String,
    val entityType: String,
    val conflictType: ConflictType,
    val resolutionType: ResolutionType,
    val priority: RulePriority,
    val enabled: Boolean
)

data class ConflictResolutionStatistics(
    val totalConflictsResolved: Int,
    val conflictsByType: Map<ResolutionType, Int>,
    val conflictsByEntityType: Map<String, Int>,
    val averageResolutionTime: Double,
    val mostUsedRule: String?,
    val manualReviewCount: Int
)

enum class ConflictType {
    DATA_MISMATCH,
    TIMESTAMP_CONFLICT,
    STATUS_CONFLICT,
    LOCATION_CONFLICT,
    USER_MODIFICATION,
    CRITICAL_DATA_CONFLICT
}

enum class ConflictSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class ConflictStatus {
    PENDING,
    RESOLVED,
    IGNORED,
    MANUAL_REVIEW
}

enum class ResolutionType {
    SERVER_WINS,
    LATEST_WINS,
    USER_WINS,
    MANUAL_REVIEW,
    MERGE,
    CUSTOM
}

enum class RulePriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}
