package com.edham.logistics.feature.search.domain.model

import java.util.*

/**
 * Global search system models for intelligent search and filtering
 */

/**
 * Main search result entity
 */
data class SearchResult(
    val id: String,
    val type: SearchResultType,
    val title: String,
    val description: String,
    val relevanceScore: Float,
    val highlights: List<String>,
    val metadata: SearchResultMetadata,
    val timestamp: Long
)

/**
 * Search result types
 */
enum class SearchResultType(val displayName: String, val icon: String) {
    SHIPMENT("شحنة", "ic_shipment"),
    USER("مستخدم", "ic_person"),
    VEHICLE("مركبة", "ic_vehicle"),
    INVOICE("فاتورة", "ic_invoice"),
    ACTIVITY_LOG("سجل نشاط", "ic_activity"),
    DOCUMENT("مستند", "ic_document"),
    NOTIFICATION("إشعار", "ic_notification"),
    REPORT("تقرير", "ic_report"),
    PAYMENT("دفع", "ic_payment"),
    LOCATION("موقع", "ic_location"),
    ROUTE("مسار", "ic_route"),
    DRIVER("سائق", "ic_driver"),
    CUSTOMER("عميل", "ic_customer"),
    ADMIN("مشرف", "ic_admin"),
    ACCOUNTANT("محاسب", "ic_accountant")
}

/**
 * Search result metadata
 */
data class SearchResultMetadata(
    val entityId: String,
    val entityType: String,
    val fields: Map<String, Any>,
    val tags: List<String>,
    val categories: List<String>,
    val status: String?,
    val priority: SearchPriority,
    val source: SearchSource,
    val lastModified: Long,
    val created: Long,
    val owner: String?,
    val location: LocationInfo?,
    val permissions: Set<String>
)

/**
 * Search priority levels
 */
enum class SearchPriority(val level: Int, val displayName: String) {
    LOW(1, "منخفض"),
    MEDIUM(2, "متوسط"),
    HIGH(3, "عالي"),
    URGENT(4, "عاجل"),
    CRITICAL(5, "حرج")
}

/**
 * Search sources
 */
enum class SearchSource(val displayName: String) {
    DATABASE("قاعدة البيانات"),
    CACHE("الذاكرة المؤقتة"),
    API("واجهة برمجة التطبيقات"),
    INDEX("فهرس البحث"),
    EXTERNAL("مصدر خارجي"),
    FILE("ملف"),
    MEMORY("الذاكرة")
}

/**
 * Location information
 */
data class LocationInfo(
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val city: String?,
    val country: String?,
    val postalCode: String?,
    val timezone: String?
)

/**
 * Search query
 */
data class SearchQuery(
    val text: String,
    val filters: SearchFilters,
    val options: SearchOptions,
    val pagination: PaginationOptions,
    val sorting: SortingOptions,
    val facets: List<FacetOption>
)

/**
 * Search filters
 */
data class SearchFilters(
    val entityTypes: List<SearchResultType> = emptyList(),
    val dateRange: DateRangeFilter? = null,
    val statusFilter: StatusFilter? = null,
    val roleFilter: RoleFilter? = null,
    val locationFilter: LocationFilter? = null,
    val priorityFilter: PriorityFilter? = null,
    val ownerFilter: OwnerFilter? = null,
    val tagFilter: TagFilter? = null,
    val categoryFilter: CategoryFilter? = null,
    val customFilters: Map<String, Any> = emptyMap()
)

/**
 * Date range filter
 */
data class DateRangeFilter(
    val field: String,
    val startDate: Long?,
    val endDate: Long?,
    val includeTime: Boolean = false
)

/**
 * Status filter
 */
data class StatusFilter(
    val field: String,
    val statuses: List<String>
)

/**
 * Role filter
 */
data class RoleFilter(
    val roles: List<String>
)

/**
 * Location filter
 */
data class LocationFilter(
    val latitude: Double?,
    val longitude: Double?,
    val radius: Double?, // in kilometers
    val city: String?,
    val country: String?,
    val postalCode: String?
)

/**
 * Priority filter
 */
data class PriorityFilter(
    val priorities: List<SearchPriority>
)

/**
 * Owner filter
 */
data class OwnerFilter(
    val owners: List<String>
)

/**
 * Tag filter
 */
data class TagFilter(
    val tags: List<String>,
    val matchAll: Boolean = false // true = AND, false = OR
)

/**
 * Category filter
 */
data class CategoryFilter(
    val categories: List<String>,
    val matchAll: Boolean = false
)

/**
 * Search options
 */
data class SearchOptions(
    val fuzzySearch: Boolean = true,
    val fuzzyThreshold: Float = 0.7f,
    val caseSensitive: Boolean = false,
    val diacriticSensitive: Boolean = false,
    val includeHighlights: Boolean = true,
    val maxHighlights: Int = 3,
    val highlightPreTag: String = "<mark>",
    val highlightPostTag: String = "</mark>",
    val includeMetadata: Boolean = true,
    val includeScore: Boolean = true,
    val minScore: Float = 0.1f,
    val explainResults: Boolean = false
)

/**
 * Pagination options
 */
data class PaginationOptions(
    val page: Int = 1,
    val pageSize: Int = 20,
    val offset: Int = 0
)

/**
 * Sorting options
 */
data class SortingOptions(
    val field: String = "relevance",
    val order: SortOrder = SortOrder.DESC,
    val mode: SortMode = SortMode.NONE
)

/**
 * Sort order
 */
enum class SortOrder {
    ASC,
    DESC
}

/**
 * Sort mode
 */
enum class SortMode {
    MIN,
    MAX,
    SUM,
    AVG,
    NONE
}

/**
 * Facet option
 */
data class FacetOption(
    val field: String,
    val size: Int = 10,
    val sort: FacetSort = FacetSort.COUNT
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
 * Search response
 */
data class SearchResponse(
    val query: SearchQuery,
    val results: List<SearchResult>,
    val totalCount: Long,
    val took: Long, // time in milliseconds
    val aggregations: Map<String, AggregationResult>,
    val suggestions: List<String>,
    val corrections: List<SpellCorrection>,
    val facets: Map<String, List<FacetResult>>,
    val pagination: PaginationInfo,
    val metadata: SearchResponseMetadata
)

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
 * Facet result
 */
data class FacetResult(
    val value: String,
    val count: Long,
    val selected: Boolean
)

/**
 * Spell correction
 */
data class SpellCorrection(
    val original: String,
    val corrected: String,
    val score: Float
)

/**
 * Pagination info
 */
data class PaginationInfo(
    val currentPage: Int,
    val pageSize: Int,
    val totalCount: Long,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextPage: Int?,
    val previousPage: Int?
)

/**
 * Search response metadata
 */
data class SearchResponseMetadata(
    val searchTime: Long,
    val totalShards: Int,
    val successfulShards: Int,
    val skippedShards: Int,
    val timedOut: Boolean,
    val maxScore: Float,
    val hitCount: Long,
    val indexName: String
)

/**
 * Search suggestion
 */
data class SearchSuggestion(
    val text: String,
    val score: Float,
    val source: SuggestionSource,
    val type: SuggestionType,
    val metadata: Map<String, Any>
)

/**
 * Suggestion source
 */
enum class SuggestionSource {
    HISTORY,
    POPULAR,
    AUTOCOMPLETE,
    SYNONYM,
    CORRECTION
}

/**
 * Suggestion type
 */
enum class SuggestionType {
    QUERY,
    ENTITY,
    FIELD,
    VALUE
}

/**
 * Search history
 */
data class SearchHistory(
    val id: String,
    val userId: String,
    val query: String,
    val filters: SearchFilters,
    val resultCount: Int,
    val timestamp: Long,
    val duration: Long,
    val clickedResults: List<String> // result IDs that were clicked
)

/**
 * Search analytics
 */
data class SearchAnalytics(
    val totalSearches: Long,
    val uniqueQueries: Long,
    val averageResults: Double,
    val averageTime: Double,
    val topQueries: List<QueryAnalytics>,
    val noResultQueries: List<String>,
    val popularFilters: List<FilterAnalytics>,
    val clickThroughRate: Float,
    val searchTrends: List<SearchTrend>,
    val userAnalytics: Map<String, UserSearchAnalytics>
)

/**
 * Query analytics
 */
data class QueryAnalytics(
    val query: String,
    val count: Long,
    val averageResults: Double,
    val averageTime: Double,
    val clickThroughRate: Float
)

/**
 * Filter analytics
 */
data class FilterAnalytics(
    val filter: String,
    val count: Long,
    val averageResults: Double
)

/**
 * Search trend
 */
data class SearchTrend(
    val timestamp: Long,
    val searchCount: Long,
    val uniqueQueries: Long,
    val averageResults: Double
)

/**
 * User search analytics
 */
data class UserSearchAnalytics(
    val userId: String,
    val totalSearches: Long,
    val uniqueQueries: Long,
    val averageResults: Double,
    val averageTime: Double,
    val clickThroughRate: Float,
    val topQueries: List<String>
)

/**
 * Search index configuration
 */
data class SearchIndexConfig(
    val name: String,
    val entityTypes: List<SearchResultType>,
    val fields: List<IndexField>,
    val analyzers: List<Analyzer>,
    val settings: IndexSettings
)

/**
 * Index field
 */
data class IndexField(
    val name: String,
    val type: FieldType,
    val analyzer: String?,
    val searchable: Boolean = true,
    val filterable: Boolean = true,
    val sortable: Boolean = false,
    val aggregatable: Boolean = true,
    val stored: Boolean = true,
    val weight: Float = 1.0f,
    val boost: Float = 1.0f
)

/**
 * Field types
 */
enum class FieldType {
    TEXT,
    KEYWORD,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    DATE,
    GEO_POINT,
    GEO_SHAPE,
    NESTED,
    OBJECT
}

/**
 * Analyzer
 */
data class Analyzer(
    val name: String,
    val tokenizer: Tokenizer,
    val filters: List<Filter>
)

/**
 * Tokenizer
 */
data class Tokenizer(
    val type: TokenizerType,
    val settings: Map<String, Any>
)

/**
 * Tokenizer types
 */
enum class TokenizerType {
    STANDARD,
    WHITESPACE,
    KEYWORD,
    LETTER,
    LOWERCASE,
    NGRAM,
    EDGE_NGRAM,
    PATH_HIERARCHY
}

/**
 * Filter
 */
data class Filter(
    val type: FilterType,
    val settings: Map<String, Any>
)

/**
 * Filter types
 */
enum class FilterType {
    LOWERCASE,
    UPPERCASE,
    ASCIIFOLDING,
    STOP,
    STEMMER,
    SYNONYM,
    SHINGLE,
    ELISION,
    TRIM
}

/**
 * Index settings
 */
data class IndexSettings(
    val maxResultWindow: Int = 10000,
    val maxTermsCount: Int = 256000,
    val maxClauseCount: Int = 1024,
    val defaultAnalyzer: String = "standard",
    val searchAnalyzer: String = "standard",
    val indexAnalyzer: String = "standard"
)

/**
 * Search performance metrics
 */
data class SearchPerformanceMetrics(
    val queryTime: Long,
    val fetchTime: Long,
    val totalTime: Long,
    val memoryUsage: Long,
    val cacheHitRate: Float,
    val indexSize: Long,
    val shardCount: Int,
    val concurrentSearches: Int,
    val queueSize: Int
)

/**
 * Search optimization result
 */
data class SearchOptimizationResult(
    val originalQuery: SearchQuery,
    val optimizedQuery: SearchQuery,
    val improvements: List<OptimizationImprovement>,
    val performanceGain: Float,
    val appliedOptimizations: List<String>
)

/**
 * Optimization improvement
 */
data class OptimizationImprovement(
    val type: OptimizationType,
    val description: String,
    val impact: Float
)

/**
 * Optimization types
 */
enum class OptimizationType {
    QUERY_REWRITE,
    FILTER_OPTIMIZATION,
    INDEX_OPTIMIZATION,
    CACHE_OPTIMIZATION,
    SHARD_OPTIMIZATION
}

/**
 * Search cache configuration
 */
data class SearchCacheConfig(
    val enabled: Boolean = true,
    val maxSize: Long = 100 * 1024 * 1024, // 100MB
    val ttl: Long = 60 * 60 * 1000, // 1 hour
    val strategy: CacheStrategy = CacheStrategy.LRU
)

/**
 * Cache strategies
 */
enum class CacheStrategy {
    LRU,
    LFU,
    FIFO,
    TTL,
}

/**
 * Search export configuration
 */
data class SearchExportConfig(
    val format: ExportFormat,
    val fields: List<String>,
    val maxRecords: Int = 10000,
    val includeMetadata: Boolean = true,
    val compression: Boolean = false
)

/**
 * Export formats
 */
enum class ExportFormat {
    CSV,
    JSON,
    XML,
    PDF,
    EXCEL,
}

/**
 * Search export result
 */
data class SearchExportResult(
    val fileId: String,
    val fileName: String,
    val fileSize: Long,
    val recordCount: Long,
    val format: ExportFormat,
    val downloadUrl: String,
    val expiresAt: Long,
    val createdAt: Long
)

/**
 * Search alert configuration
 */
data class SearchAlertConfig(
    val id: String,
    val name: String,
    val description: String,
    val query: SearchQuery,
    val condition: AlertCondition,
    val threshold: Int,
    val timeWindow: Int, // in minutes
    val isActive: Boolean,
    val notificationChannels: List<NotificationChannel>,
    val lastTriggered: Long?,
    val triggerCount: Int
)

/**
 * Alert conditions
 */
enum class AlertCondition {
    RESULT_COUNT_GREATER_THAN,
    RESULT_COUNT_LESS_THAN,
    RESULT_COUNT_EQUALS,
    NO_RESULTS,
    SPECIFIC_RESULT_FOUND,
    ERROR_RATE_GREATER_THAN,
    RESPONSE_TIME_GREATER_THAN
}

/**
 * Notification channels
 */
enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    WEBHOOK,
    SLACK,
    TELEGRAM,
    IN_APP
}

/**
 * Search alert
 */
data class SearchAlert(
    val id: String,
    val configId: String,
    val configName: String,
    val triggeredAt: Long,
    val condition: AlertCondition,
    val threshold: Int,
    val actualValue: Int,
    val timeWindow: Int,
    val query: SearchQuery,
    val results: List<SearchResult>,
    val notificationsSent: List<NotificationResult>
)

/**
 * Notification result
 */
data class NotificationResult(
    val channel: NotificationChannel,
    val recipient: String,
    val sentAt: Long,
    val success: Boolean,
    val errorMessage: String?
)

/**
 * Search template
 */
data class SearchTemplate(
    val id: String,
    val name: String,
    val description: String,
    val query: SearchQuery,
    val parameters: List<TemplateParameter>,
    val isPublic: Boolean,
    val createdBy: String,
    val createdAt: Long,
    val usageCount: Long
)

/**
 * Template parameter
 */
data class TemplateParameter(
    val name: String,
    val type: ParameterType,
    val defaultValue: Any?,
    val required: Boolean,
    val description: String,
    val validationRules: List<ValidationRule>
)

/**
 * Parameter types
 */
enum class ParameterType {
    STRING,
    NUMBER,
    BOOLEAN,
    DATE,
    LIST,
    OBJECT
}

/**
 * Validation rule
 */
data class ValidationRule(
    val type: ValidationType,
    val value: Any?,
    val message: String
)

/**
 * Validation types
 */
enum class ValidationType {
    REQUIRED,
    MIN_LENGTH,
    MAX_LENGTH,
    MIN_VALUE,
    MAX_VALUE,
    REGEX,
    EMAIL,
    URL,
    DATE_FORMAT,
}
