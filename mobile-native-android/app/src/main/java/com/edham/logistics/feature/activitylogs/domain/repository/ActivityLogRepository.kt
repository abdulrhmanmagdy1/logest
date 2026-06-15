package com.edham.logistics.feature.activitylogs.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.activitylogs.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for activity logging operations
 */
interface ActivityLogRepository {
    
    // Basic Logging Operations
    suspend fun logActivity(activityLog: ActivityLog): Result<ActivityLog>
    suspend fun logActivityBatch(activityLogs: List<ActivityLog>): Result<List<ActivityLog>>
    
    // Query Operations
    suspend fun getActivityLogById(id: String): Result<ActivityLog>
    suspend fun getActivityLogs(filters: ActivityLogFilters): Result<List<ActivityLog>>
    suspend fun getActivityLogsPaginated(filters: ActivityLogFilters): Result<PaginatedResult<ActivityLog>>
    suspend fun searchActivityLogs(query: String, filters: ActivityLogFilters): Result<List<ActivityLog>>
    
    // Statistics and Analytics
    suspend fun getActivityLogStatistics(startTime: Long?, endTime: Long?): Result<ActivityLogStatistics>
    suspend fun getActivityLogTrends(timeRange: TimeRange, granularity: TrendGranularity): Result<List<ActivityLogTrend>>
    suspend fun getTopUsers(limit: Int, timeRange: TimeRange): Result<List<UserActivitySummary>>
    suspend fun getTopActions(limit: Int, timeRange: TimeRange): Result<List<ActionActivitySummary>>
    suspend fun getTopEntities(limit: Int, timeRange: TimeRange): Result<List<EntityActivitySummary>>
    
    // Dashboard Data
    suspend fun getActivityLogDashboardData(): Result<ActivityLogDashboardData>
    suspend fun getSystemHealthMetrics(): Result<SystemHealthMetrics>
    
    // Export Operations
    suspend fun exportActivityLogs(exportOptions: ActivityLogExportOptions): Result<ActivityLogExportResult>
    suspend fun exportActivityLogsToCSV(filters: ActivityLogFilters): Result<String>
    suspend fun exportActivityLogsToJSON(filters: ActivityLogFilters): Result<String>
    suspend fun exportActivityLogsToPDF(filters: ActivityLogFilters): Result<String>
    
    // Management Operations
    suspend fun deleteActivityLogs(filters: ActivityLogFilters): Result<Long>
    suspend fun archiveActivityLogs(filters: ActivityLogFilters): Result<Long>
    suspend fun purgeActivityLogs(filters: ActivityLogFilters): Result<Long>
    suspend fun restoreActivityLogs(logIds: List<String>): Result<List<ActivityLog>>
    
    // Batch Operations
    suspend fun performBatchOperation(operation: ActivityLogBatchOperation): Result<ActivityLogBatchOperationResult>
    suspend fun updateActivityLogsBatch(updates: List<ActivityLogUpdate>): Result<List<ActivityLog>>
    suspend fun tagActivityLogsBatch(logIds: List<String>, tagIds: List<String>): Result<List<ActivityLog>>
    
    // Alert Management
    suspend fun createAlertRule(rule: ActivityLogAlertRule): Result<ActivityLogAlertRule>
    suspend fun updateAlertRule(rule: ActivityLogAlertRule): Result<ActivityLogAlertRule>
    suspend fun deleteAlertRule(ruleId: String): Result<Unit>
    suspend fun getAlertRules(): Result<List<ActivityLogAlertRule>>
    suspend fun getActivityLogAlerts(limit: Int): Result<List<ActivityLogAlert>>
    suspend fun acknowledgeAlert(alertId: String): Result<ActivityLogAlert>
    
    // Tag Management
    suspend fun createTag(tag: ActivityLogTag): Result<ActivityLogTag>
    suspend fun updateTag(tag: ActivityLogTag): Result<ActivityLogTag>
    suspend fun deleteTag(tagId: String): Result<Unit>
    suspend fun getActivityLogTags(): Result<List<ActivityLogTag>>
    suspend fun getPopularTags(limit: Int): Result<List<ActivityLogTag>>
    suspend fun tagActivityLog(logId: String, tagIds: List<String>): Result<ActivityLog>
    suspend fun untagActivityLog(logId: String, tagIds: List<String>): Result<ActivityLog>
    suspend fun getActivityLogsWithTags(logId: String): Result<ActivityLogWithTags>
    
    // Retention Policy Management
    suspend fun getActivityLogRetentionPolicies(): Result<List<ActivityLogRetentionPolicy>>
    suspend fun updateRetentionPolicy(policy: ActivityLogRetentionPolicy): Result<ActivityLogRetentionPolicy>
    suspend fun cleanupExpiredLogs(): Result<Long>
    suspend fun archiveOldLogs(): Result<Long>
    
    // Real-time Monitoring
    fun observeActivityLogs(filters: ActivityLogFilters): Flow<List<ActivityLog>>
    fun observeActivityLogStatistics(): Flow<ActivityLogStatistics>
    fun observeSystemHealth(): Flow<SystemHealthMetrics>
    fun observeAlerts(): Flow<List<ActivityLogAlert>>
    fun observeActivityLogCount(): Flow<Long>
    fun observeErrorRate(): Flow<Float>
    
    // Search and Indexing
    suspend fun indexActivityLogs(): Result<Long>
    suspend fun rebuildSearchIndex(): Result<Long>
    suspend fun searchActivityLogsAdvanced(searchRequest: AdvancedSearchRequest): Result<AdvancedSearchResult>
    
    // Compliance and Auditing
    suspend fun generateComplianceReport(timeRange: TimeRange): Result<ComplianceReport>
    suspend fun generateAuditTrail(entityId: String, entityType: EntityType): Result<List<ActivityLog>>
    suspend fun generateUserActivityReport(userId: String, timeRange: TimeRange): Result<UserActivityReport>
    suspend fun generateSystemActivityReport(timeRange: TimeRange): Result<SystemActivityReport>
    
    // Performance and Optimization
    suspend fun optimizeDatabase(): Result<DatabaseOptimizationResult>
    suspend fun compactLogs(): Result<Long>
    suspend fun vacuumDatabase(): Result<Long>
    suspend fun analyzeQueryPerformance(): Result<List<QueryPerformanceMetric>>
    
    // Backup and Recovery
    suspend fun backupActivityLogs(backupConfig: BackupConfig): Result<BackupResult>
    suspend fun restoreActivityLogs(backupId: String): Result<RestoreResult>
    suspend fun verifyBackupIntegrity(backupId: String): Result<BackupIntegrityResult>
    
    // Integration Operations
    suspend fun syncWithExternalSystem(systemId: String): Result<SyncResult>
    suspend fun importLogsFromExternalSystem(systemId: String, importConfig: ImportConfig): Result<ImportResult>
    suspend fun exportLogsToExternalSystem(systemId: String, exportConfig: ExportConfig): Result<ExportResult>
}

/**
 * Paginated result wrapper
 */
data class PaginatedResult<T>(
    val data: List<T>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val pageSize: Int
)

/**
 * Time range for queries
 */
data class TimeRange(
    val startTime: Long,
    val endTime: Long
)

/**
 * Trend granularity
 */
enum class TrendGranularity {
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    QUARTER,
    YEAR
}

/**
 * Activity log update
 */
data class ActivityLogUpdate(
    val logId: String,
    val severity: LogSeverity?,
    val category: LogCategory?,
    val description: String?,
    val tags: List<String>?
)

/**
 * Advanced search request
 */
data class AdvancedSearchRequest(
    val query: String,
    val filters: ActivityLogFilters,
    val aggregations: List<Aggregation>,
    val sorting: List<SortOption>,
    val highlighting: Boolean,
    val facets: List<Facet>
)

/**
 * Advanced search result
 */
data class AdvancedSearchResult(
    val results: List<ActivityLog>,
    val totalCount: Long,
    val aggregations: Map<String, AggregationResult>,
    val highlights: Map<String, List<String>>,
    val facets: Map<String, List<FacetResult>>,
    val searchTime: Long
)

/**
 * Aggregation
 */
data class Aggregation(
    val name: String,
    val type: AggregationType,
    val field: String,
    val size: Int?
)

/**
 * Aggregation types
 */
enum class AggregationType {
    TERMS,
    DATE_HISTOGRAM,
    HISTOGRAM,
    STATS,
    CARDINALITY
}

/**
 * Aggregation result
 */
data class AggregationResult(
    val name: String,
    val buckets: List<AggregationBucket>,
    val value: Any?
)

/**
 * Aggregation bucket
 */
data class AggregationBucket(
    val key: String,
    val docCount: Long,
    val subAggregations: Map<String, AggregationResult>
)

/**
 * Sort option
 */
data class SortOption(
    val field: String,
    val order: SortOrder,
    val mode: SortMode?
)

/**
 * Sort mode
 */
enum class SortMode {
    MIN,
    MAX,
    SUM,
    AVG
}

/**
 * Facet
 */
data class Facet(
    val field: String,
    val size: Int,
    val sort: FacetSort?
)

/**
 * Facet sort
 */
enum class FacetSort {
    COUNT,
    ALPHA,
    TERM
}

/**
 * Facet result
 */
data class FacetResult(
    val value: String,
    val count: Long
)

/**
 * Compliance report
 */
data class ComplianceReport(
    val timeRange: TimeRange,
    val totalLogs: Long,
    val compliantLogs: Long,
    val nonCompliantLogs: Long,
    val complianceScore: Float,
    val violations: List<ComplianceViolation>,
    val recommendations: List<String>,
    val generatedAt: Long
)

/**
 * Compliance violation
 */
data class ComplianceViolation(
    val rule: String,
    val description: String,
    val severity: ComplianceSeverity,
    val count: Long,
    val examples: List<ActivityLog>
)

/**
 * Compliance severity
 */
enum class ComplianceSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * User activity report
 */
data class UserActivityReport(
    val userId: String,
    val userName: String,
    val userRole: UserRole,
    val timeRange: TimeRange,
    val totalActivities: Long,
    val activitiesByCategory: Map<LogCategory, Long>,
    val activitiesByAction: Map<ActivityAction, Long>,
    val successRate: Float,
    val averageDuration: Double,
    val topEntities: List<EntityActivitySummary>,
    val timeSeriesData: List<ActivityLogTimeSeries>,
    val securityEvents: List<ActivityLog>,
    val generatedAt: Long
)

/**
 * System activity report
 */
data class SystemActivityReport(
    val timeRange: TimeRange,
    val totalLogs: Long,
    val logsByCategory: Map<LogCategory, Long>,
    val logsBySeverity: Map<LogSeverity, Long>,
    val logsBySource: Map<LogSource, Long>,
    val logsByHour: Map<Int, Long>,
    val logsByDay: Map<String, Long>,
    val errorRate: Float,
    val averageDuration: Double,
    val topUsers: List<UserActivitySummary>,
    val topActions: List<ActionActivitySummary>,
    val securityEvents: List<ActivityLog>,
    val performanceMetrics: SystemPerformanceMetrics,
    val generatedAt: Long
)

/**
 * Database optimization result
 */
data class DatabaseOptimizationResult(
    val tablesOptimized: List<String>,
    val indexesRebuilt: List<String>,
    val spaceSaved: Long,
    val performanceImprovement: Float,
    val duration: Long,
    val timestamp: Long
)

/**
 * Query performance metric
 */
data class QueryPerformanceMetric(
    val query: String,
    val averageExecutionTime: Double,
    val minExecutionTime: Double,
    val maxExecutionTime: Double,
    val executionCount: Long,
    val totalTime: Double,
    val lastOptimized: Long
)

/**
 * Backup configuration
 */
data class BackupConfig(
    val includeFilters: ActivityLogFilters,
    val excludeFilters: ActivityLogFilters,
    val compression: Boolean,
    val encryption: Boolean,
    val destination: BackupDestination,
    val retentionDays: Int
)

/**
 * Backup destination
 */
enum class BackupDestination {
    LOCAL,
    S3,
    AZURE_BLOB,
    GOOGLE_CLOUD,
    FTP,
    SFTP
}

/**
 * Backup result
 */
data class BackupResult(
    val backupId: String,
    val recordCount: Long,
    val fileSize: Long,
    val compressedSize: Long,
    val checksum: String,
    val duration: Long,
    val createdAt: Long
)

/**
 * Restore result
 */
data class RestoreResult(
    val backupId: String,
    val recordCount: Long,
    val restoredCount: Long,
    val failedCount: Long,
    val errors: List<String>,
    val duration: Long,
    val restoredAt: Long
)

/**
 * Backup integrity result
 */
data class BackupIntegrityResult(
    val backupId: String,
    val isValid: Boolean,
    val recordCount: Long,
    val checksum: String,
    val issues: List<String>,
    val verifiedAt: Long
)

/**
 * Sync result
 */
data class SyncResult(
    val systemId: String,
    val syncedCount: Long,
    val failedCount: Long,
    val errors: List<String>,
    val duration: Long,
    val syncedAt: Long
)

/**
 * Import configuration
 */
data class ImportConfig(
    val format: ImportFormat,
    val mapping: FieldMapping,
    val validation: ValidationConfig,
    val batchSize: Int,
    val skipErrors: Boolean
)

/**
 * Import format
 */
enum class ImportFormat {
    CSV,
    JSON,
    XML,
    LOG,
    SYSLOG,
    CUSTOM
}

/**
 * Field mapping
 */
data class FieldMapping(
    val mappings: Map<String, String>
)

/**
 * Validation configuration
 */
data class ValidationConfig(
    val strict: Boolean,
    val requiredFields: List<String>,
    val fieldValidators: Map<String, FieldValidator>
)

/**
 * Field validator
 */
data class FieldValidator(
    val type: ValidationType,
    val pattern: String?,
    val minLength: Int?,
    val maxLength: Int?
)

/**
 * Validation types
 */
enum class ValidationType {
    REGEX,
    EMAIL,
    PHONE,
    URL,
    DATE,
    NUMBER,
    STRING
}

/**
 * Import result
 */
data class ImportResult(
    val recordCount: Long,
    val importedCount: Long,
    val failedCount: Long,
    val skippedCount: Long,
    val errors: List<ImportError>,
    val duration: Long,
    val importedAt: Long
)

/**
 * Import error
 */
data class ImportError(
    val lineNumber: Long,
    val record: Map<String, Any>,
    val error: String,
    val severity: ErrorSeverity
)

/**
 * Error severity
 */
enum class ErrorSeverity {
    WARNING,
    ERROR,
    CRITICAL
}

/**
 * Export configuration
 */
data class ExportConfig(
    val format: ExportFormat,
    val filters: ActivityLogFilters,
    val fieldSelection: FieldSelection,
    val transformation: DataTransformation?,
    val compression: Boolean,
    val encryption: Boolean
)

/**
 * Field selection
 */
data class FieldSelection(
    val includeFields: List<String>,
    val excludeFields: List<String>
)

/**
 * Data transformation
 */
data class DataTransformation(
    val transformations: List<FieldTransformation>
)

/**
 * Field transformation
 */
data class FieldTransformation(
    val field: String,
    val type: TransformationType,
    val expression: String?
)

/**
 * Transformation types
 */
enum class TransformationType {
    MASK,
    HASH,
    ENCRYPT,
    DECRYPT,
    FORMAT_DATE,
    FORMAT_NUMBER,
    CUSTOM
}

/**
 * Export result
 */
data class ExportResult(
    val recordCount: Long,
    val fileSize: Long,
    val filePath: String,
    val checksum: String,
    val duration: Long,
    val exportedAt: Long
)
