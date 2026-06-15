// ============================================
// 🚀 Edham Logistics - Advanced Support System
// Premium Dark Theme with Smart Support Management
// ============================================

package com.edham.logistics.ui.support

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.edham.logistics.ui.theme.EdhamOrange
import com.edham.logistics.ui.theme.SuccessGreen
import com.edham.logistics.ui.theme.WarningYellow
import com.edham.logistics.ui.theme.ErrorRed
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * ============================================
 * Advanced Support System Service
 * ============================================
 * نظام الدعم الفني المتقدم مع تذاكر ذكية
 */
class SupportSystemService(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ar"))
    
    // Secure SharedPreferences for support data
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_support",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    data class SupportTicket(
        val id: String,
        val userId: String,
        val category: TicketCategory,
        val priority: TicketPriority,
        val title: String,
        val description: String,
        val status: TicketStatus,
        val createdAt: Date,
        val updatedAt: Date,
        val assignedTo: String? = null,
        val estimatedResolution: Date? = null,
        val actualResolution: Date? = null,
        val attachments: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class SupportRequest(
        val category: TicketCategory,
        val priority: TicketPriority,
        val title: String,
        val description: String,
        val attachments: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val metadata: Map<String, Any> = emptyMap()
    )
    
    data class SupportResponse(
        val ticketId: String,
        val message: String,
        val attachments: List<String> = emptyList(),
        val isInternal: Boolean = false,
        val timestamp: Date = Date()
    )
    
    data class SupportAnalytics(
        val totalTickets: Int,
        val openTickets: Int,
        val closedTickets: Int,
        val averageResolutionTime: Double,
        val categoryBreakdown: Map<TicketCategory, Int>,
        val priorityBreakdown: Map<TicketPriority, Int>,
        val satisfactionScore: Double,
        val responseTimeAverage: Double,
        val topIssues: List<TopIssue>
    )
    
    data class TopIssue(
        val category: TicketCategory,
        val count: Int,
        val averageResolutionTime: Double,
        val satisfactionScore: Double
    )
    
    data class SupportAgent(
        val id: String,
        val name: String,
        val email: String,
        val department: String,
        val isOnline: Boolean,
        val currentLoad: Int,
        val maxLoad: Int,
        val averageResponseTime: Double,
        val satisfactionRating: Double,
        val languages: List<String>
    )
    
    data class KnowledgeBase(
        val id: String,
        val title: String,
        val content: String,
        val category: String,
        val tags: List<String>,
        val views: Int,
        val helpfulCount: Int,
        val lastUpdated: Date,
        val difficulty: Difficulty
    )
    
    enum class TicketCategory {
        SHIPMENT_ISSUES,
        PAYMENT_PROBLEMS,
        TECHNICAL_SUPPORT,
        ACCOUNT_ISSUES,
        BILLING_INQUIRIES,
        GENERAL_INQUIRIES,
        FEEDBACK_COMPLAINTS,
        FEATURE_REQUESTS,
        DELIVERY_ISSUES,
        DOCUMENTATION_HELP
    }
    
    enum class TicketPriority {
        URGENT,
        HIGH,
        MEDIUM,
        LOW
    }
    
    enum class TicketStatus {
        OPEN,
        IN_PROGRESS,
        WAITING_FOR_CUSTOMER,
        WAITING_FOR_SUPPORT,
        RESOLVED,
        CLOSED,
        CANCELLED
    }
    
    enum class Difficulty {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
    
    /**
     * ============================================
     * Ticket Management
     * ============================================
     */
    suspend fun createTicket(supportRequest: SupportRequest): Result<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            // Validate support request
            validateSupportRequest(supportRequest)
            
            // Create ticket
            val ticket = SupportTicket(
                id = "TICKET_${System.currentTimeMillis()}",
                userId = getCurrentUserId(),
                category = supportRequest.category,
                priority = supportRequest.priority,
                title = supportRequest.title,
                description = supportRequest.description,
                status = TicketStatus.OPEN,
                createdAt = Date(),
                updatedAt = Date(),
                attachments = supportRequest.attachments,
                tags = supportRequest.tags,
                metadata = supportRequest.metadata + mapOf(
                    "device_info" to getDeviceInfo(),
                    "app_version" to getAppVersion(),
                    "user_agent" to getUserAgent()
                )
            )
            
            // Save ticket
            saveTicket(ticket)
            
            // Auto-assign ticket if possible
            autoAssignTicket(ticket)
            
            // Send notifications
            sendTicketNotifications(ticket)
            
            // Update analytics
            updateSupportAnalytics()
            
            Result.success(ticket)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTicketStatus(
        ticketId: String,
        status: TicketStatus,
        assignedTo: String? = null,
        estimatedResolution: Date? = null
    ): Result<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val ticket = getTicket(ticketId)
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Ticket not found")
                )
            
            val updatedTicket = ticket.copy(
                status = status,
                assignedTo = assignedTo,
                estimatedResolution = estimatedResolution,
                updatedAt = Date()
            )
            
            // Update ticket
            updateTicket(updatedTicket)
            
            // Send notifications
            sendStatusUpdateNotifications(updatedTicket)
            
            Result.success(updatedTicket)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addResponse(
        ticketId: String,
        response: SupportResponse
    ): Result<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val ticket = getTicket(ticketId)
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Ticket not found")
                )
            
            // Save response
            saveResponse(ticketId, response)
            
            // Update ticket status
            val newStatus = if (response.isInternal) {
                when (ticket.status) {
                    TicketStatus.OPEN, TicketStatus.WAITING_FOR_SUPPORT -> TicketStatus.IN_PROGRESS
                    else -> ticket.status
                }
            } else {
                when (ticket.status) {
                    TicketStatus.OPEN, TicketStatus.IN_PROGRESS -> TicketStatus.WAITING_FOR_CUSTOMER
                    TicketStatus.WAITING_FOR_CUSTOMER -> TicketStatus.IN_PROGRESS
                    else -> ticket.status
                }
            }
            
            val updatedTicket = ticket.copy(
                status = newStatus,
                updatedAt = Date()
            )
            
            updateTicket(updatedTicket)
            
            // Send notifications
            sendResponseNotifications(updatedTicket, response)
            
            Result.success(updatedTicket)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resolveTicket(
        ticketId: String,
        resolution: String,
        satisfactionRating: Int? = null
    ): Result<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val ticket = getTicket(ticketId)
                ?: return@withContext Result.failure(
                    IllegalArgumentException("Ticket not found")
                )
            
            val resolvedTicket = ticket.copy(
                status = TicketStatus.RESOLVED,
                actualResolution = Date(),
                updatedAt = Date()
            )
            
            // Update ticket
            updateTicket(resolvedTicket)
            
            // Save resolution
            saveResolution(ticketId, resolution, satisfactionRating)
            
            // Send notifications
            sendResolutionNotifications(resolvedTicket, resolution)
            
            // Update analytics
            updateSupportAnalytics()
            
            // Check for follow-up satisfaction survey
            scheduleSatisfactionSurvey(resolvedTicket)
            
            Result.success(resolvedTicket)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * ============================================
     * Get Tickets
     * ============================================
     */
    suspend fun getTicket(ticketId: String): SupportTicket? = withContext(Dispatchers.IO) {
        try {
            val ticketsJson = securePrefs.getString("support_tickets", "[]")
            val jsonArray = org.json.JSONArray(ticketsJson)
            
            for (i in 0 until jsonArray.length()) {
                val ticketJson = jsonArray.getJSONObject(i)
                if (ticketJson.getString("id") == ticketId) {
                    return@withContext parseTicketFromJson(ticketJson)
                }
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getUserTickets(
        userId: String? = null,
        status: TicketStatus? = null,
        limit: Int = 50
    ): List<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val targetUserId = userId ?: getCurrentUserId()
            val ticketsJson = securePrefs.getString("support_tickets", "[]")
            val jsonArray = org.json.JSONArray(ticketsJson)
            val tickets = mutableListOf<SupportTicket>()
            
            for (i in 0 until jsonArray.length()) {
                val ticketJson = jsonArray.getJSONObject(i)
                val ticket = parseTicketFromJson(ticketJson)
                
                if (ticket.userId == targetUserId &&
                    (status == null || ticket.status == status)) {
                    tickets.add(ticket)
                }
            }
            
            tickets.sortedByDescending { it.createdAt }.take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getAllTickets(
        status: TicketStatus? = null,
        category: TicketCategory? = null,
        priority: TicketPriority? = null,
        limit: Int = 100
    ): List<SupportTicket> = withContext(Dispatchers.IO) {
        try {
            val ticketsJson = securePrefs.getString("support_tickets", "[]")
            val jsonArray = org.json.JSONArray(ticketsJson)
            val tickets = mutableListOf<SupportTicket>()
            
            for (i in 0 until jsonArray.length()) {
                val ticketJson = jsonArray.getJSONObject(i)
                val ticket = parseTicketFromJson(ticketJson)
                
                if ((status == null || ticket.status == status) &&
                    (category == null || ticket.category == category) &&
                    (priority == null || ticket.priority == priority)) {
                    tickets.add(ticket)
                }
            }
            
            tickets.sortedByDescending { it.createdAt }.take(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * ============================================
     * Support Analytics
     * ============================================
     */
    suspend fun getSupportAnalytics(
        startDate: Date? = null,
        endDate: Date? = null
    ): SupportAnalytics = withContext(Dispatchers.IO) {
        try {
            val tickets = getAllTickets()
                .filter { ticket ->
                    (startDate == null || ticket.createdAt >= startDate) &&
                    (endDate == null || ticket.createdAt <= endDate)
                }
            
            val totalTickets = tickets.size
            val openTickets = tickets.count { it.status == TicketStatus.OPEN || it.status == TicketStatus.IN_PROGRESS }
            val closedTickets = tickets.count { it.status == TicketStatus.RESOLVED || it.status == TicketStatus.CLOSED }
            
            // Calculate average resolution time
            val resolvedTickets = tickets.filter { it.status == TicketStatus.RESOLVED && it.actualResolution != null }
            val averageResolutionTime = if (resolvedTickets.isNotEmpty()) {
                val totalResolutionTime = resolvedTickets.map { ticket ->
                    if (ticket.actualResolution != null && ticket.createdAt != null) {
                        ticket.actualResolution.time - ticket.createdAt.time
                    } else 0L
                }.sum()
                
                totalResolutionTime.toDouble() / resolvedTickets.size / (1000 * 60 * 60) // Convert to hours
            } else 0.0
            
            // Category breakdown
            val categoryBreakdown = tickets
                .groupBy { it.category }
                .mapValues { it.value.size }
            
            // Priority breakdown
            val priorityBreakdown = tickets
                .groupBy { it.priority }
                .mapValues { it.value.size }
            
            // Satisfaction score (mock data for now)
            val satisfactionScore = 4.2
            
            // Response time average (mock data for now)
            val responseTimeAverage = 2.5 // hours
            
            // Top issues
            val topIssues = categoryBreakdown.map { (category, count) ->
                val categoryTickets = tickets.filter { it.category == category }
                val categoryResolutionTime = if (categoryTickets.isNotEmpty()) {
                    val totalCategoryTime = categoryTickets
                        .filter { it.actualResolution != null && it.createdAt != null }
                        .map { it.actualResolution!!.time - it.createdAt.time }
                        .sum()
                    
                    totalCategoryTime.toDouble() / categoryTickets.size / (1000 * 60 * 60)
                } else 0.0
                
                TopIssue(
                    category = category,
                    count = count,
                    averageResolutionTime = categoryResolutionTime,
                    satisfactionScore = satisfactionScore
                )
            }.sortedByDescending { it.count }.take(5)
            
            SupportAnalytics(
                totalTickets = totalTickets,
                openTickets = openTickets,
                closedTickets = closedTickets,
                averageResolutionTime = averageResolutionTime,
                categoryBreakdown = categoryBreakdown,
                priorityBreakdown = priorityBreakdown,
                satisfactionScore = satisfactionScore,
                responseTimeAverage = responseTimeAverage,
                topIssues = topIssues
            )
            
        } catch (e: Exception) {
            SupportAnalytics(
                totalTickets = 0,
                openTickets = 0,
                closedTickets = 0,
                averageResolutionTime = 0.0,
                categoryBreakdown = emptyMap(),
                priorityBreakdown = emptyMap(),
                satisfactionScore = 0.0,
                responseTimeAverage = 0.0,
                topIssues = emptyList()
            )
        }
    }
    
    /**
     * ============================================
     * Knowledge Base
     * ============================================
     */
    suspend fun searchKnowledgeBase(query: String): List<KnowledgeBase> = withContext(Dispatchers.IO) {
        try {
            val knowledgeBaseJson = securePrefs.getString("knowledge_base", "[]")
            val jsonArray = org.json.JSONArray(knowledgeBaseJson)
            val articles = mutableListOf<KnowledgeBase>()
            
            for (i in 0 until jsonArray.length()) {
                val articleJson = jsonArray.getJSONObject(i)
                val article = parseKnowledgeBaseFromJson(articleJson)
                
                // Simple search - check if query is in title or content
                if (article.title.contains(query, ignoreCase = true) ||
                    article.content.contains(query, ignoreCase = true) ||
                    article.tags.any { it.contains(query, ignoreCase = true) }) {
                    articles.add(article)
                }
            }
            
            articles.sortedByDescending { it.views }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getKnowledgeBaseArticle(articleId: String): KnowledgeBase? = withContext(Dispatchers.IO) {
        try {
            val knowledgeBaseJson = securePrefs.getString("knowledge_base", "[]")
            val jsonArray = org.json.JSONArray(knowledgeBaseJson)
            
            for (i in 0 until jsonArray.length()) {
                val articleJson = jsonArray.getJSONObject(i)
                if (articleJson.getString("id") == articleId) {
                    return@withContext parseKnowledgeBaseFromJson(articleJson)
                }
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun markArticleHelpful(articleId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val articles = getKnowledgeBaseArticles().toMutableList()
            val articleIndex = articles.indexOfFirst { it.id == articleId }
            
            if (articleIndex != -1) {
                val article = articles[articleIndex]
                val updatedArticle = article.copy(helpfulCount = article.helpfulCount + 1)
                articles[articleIndex] = updatedArticle
                
                saveKnowledgeBaseArticles(articles)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * ============================================
     * Support Agents
     * ============================================
     */
    suspend fun getAvailableAgents(): List<SupportAgent> = withContext(Dispatchers.IO) {
        try {
            val agentsJson = securePrefs.getString("support_agents", "[]")
            val jsonArray = org.json.JSONArray(agentsJson)
            val agents = mutableListOf<SupportAgent>()
            
            for (i in 0 until jsonArray.length()) {
                val agentJson = jsonArray.getJSONObject(i)
                agents.add(parseAgentFromJson(agentJson))
            }
            
            agents.filter { it.isOnline }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * ============================================
     * Helper Methods
     * ============================================
     */
    private fun validateSupportRequest(supportRequest: SupportRequest) {
        require(supportRequest.title.isNotBlank()) { "Title is required" }
        require(supportRequest.description.isNotBlank()) { "Description is required" }
        require(supportRequest.title.length <= 200) { "Title must be 200 characters or less" }
        require(supportRequest.description.length <= 2000) { "Description must be 2000 characters or less" }
    }
    
    private fun saveTicket(ticket: SupportTicket) {
        try {
            val tickets = getAllTickets().toMutableList()
            tickets.add(ticket)
            saveAllTickets(tickets)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun updateTicket(ticket: SupportTicket) {
        try {
            val tickets = getAllTickets().toMutableList()
            val ticketIndex = tickets.indexOfFirst { it.id == ticket.id }
            
            if (ticketIndex != -1) {
                tickets[ticketIndex] = ticket
                saveAllTickets(tickets)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun saveAllTickets(tickets: List<SupportTicket>) {
        try {
            val jsonArray = org.json.JSONArray()
            tickets.forEach { ticket ->
                val ticketJson = JSONObject().apply {
                    put("id", ticket.id)
                    put("userId", ticket.userId)
                    put("category", ticket.category.name)
                    put("priority", ticket.priority.name)
                    put("title", ticket.title)
                    put("description", ticket.description)
                    put("status", ticket.status.name)
                    put("createdAt", dateFormat.format(ticket.createdAt))
                    put("updatedAt", dateFormat.format(ticket.updatedAt))
                    put("assignedTo", ticket.assignedTo)
                    put("estimatedResolution", ticket.estimatedResolution?.let { dateFormat.format(it) })
                    put("actualResolution", ticket.actualResolution?.let { dateFormat.format(it) })
                    put("attachments", org.json.JSONArray(ticket.attachments))
                    put("tags", org.json.JSONArray(ticket.tags))
                    put("metadata", JSONObject(ticket.metadata))
                }
                jsonArray.put(ticketJson)
            }
            
            securePrefs.edit()
                .putString("support_tickets", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun parseTicketFromJson(ticketJson: JSONObject): SupportTicket {
        val attachments = mutableListOf<String>()
        val attachmentsArray = ticketJson.optJSONArray("attachments")
        if (attachmentsArray != null) {
            for (i in 0 until attachmentsArray.length()) {
                attachments.add(attachmentsArray.getString(i))
            }
        }
        
        val tags = mutableListOf<String>()
        val tagsArray = ticketJson.optJSONArray("tags")
        if (tagsArray != null) {
            for (i in 0 until tagsArray.length()) {
                tags.add(tagsArray.getString(i))
            }
        }
        
        val metadata = mutableMapOf<String, Any>()
        val metadataObject = ticketJson.optJSONObject("metadata")
        if (metadataObject != null) {
            metadataObject.keys().forEach { key ->
                metadata[key] = metadataObject.get(key)
            }
        }
        
        return SupportTicket(
            id = ticketJson.getString("id"),
            userId = ticketJson.getString("userId"),
            category = TicketCategory.valueOf(ticketJson.getString("category")),
            priority = TicketPriority.valueOf(ticketJson.getString("priority")),
            title = ticketJson.getString("title"),
            description = ticketJson.getString("description"),
            status = TicketStatus.valueOf(ticketJson.getString("status")),
            createdAt = dateFormat.parse(ticketJson.getString("createdAt")) ?: Date(),
            updatedAt = dateFormat.parse(ticketJson.getString("updatedAt")) ?: Date(),
            assignedTo = ticketJson.optString("assignedTo"),
            estimatedResolution = ticketJson.optString("estimatedResolution")?.let { 
                dateFormat.parse(it) 
            },
            actualResolution = ticketJson.optString("actualResolution")?.let { 
                dateFormat.parse(it) 
            },
            attachments = attachments,
            tags = tags,
            metadata = metadata
        )
    }
    
    private fun saveResponse(ticketId: String, response: SupportResponse) {
        try {
            val responses = getTicketResponses(ticketId).toMutableList()
            responses.add(response)
            saveTicketResponses(ticketId, responses)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun getTicketResponses(ticketId: String): List<SupportResponse> {
        // This would retrieve responses for a specific ticket
        // For now, return empty list
        return emptyList()
    }
    
    private fun saveTicketResponses(ticketId: String, responses: List<SupportResponse>) {
        // This would save responses for a specific ticket
    }
    
    private fun saveResolution(ticketId: String, resolution: String, satisfactionRating: Int?) {
        // This would save resolution details
    }
    
    private fun getKnowledgeBaseArticles(): List<KnowledgeBase> {
        try {
            val knowledgeBaseJson = securePrefs.getString("knowledge_base", "[]")
            val jsonArray = org.json.JSONArray(knowledgeBaseJson)
            val articles = mutableListOf<KnowledgeBase>()
            
            for (i in 0 until jsonArray.length()) {
                articles.add(parseKnowledgeBaseFromJson(jsonArray.getJSONObject(i)))
            }
            
            return articles
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveKnowledgeBaseArticles(articles: List<KnowledgeBase>) {
        try {
            val jsonArray = org.json.JSONArray()
            articles.forEach { article ->
                val articleJson = JSONObject().apply {
                    put("id", article.id)
                    put("title", article.title)
                    put("content", article.content)
                    put("category", article.category)
                    put("tags", org.json.JSONArray(article.tags))
                    put("views", article.views)
                    put("helpfulCount", article.helpfulCount)
                    put("lastUpdated", dateFormat.format(article.lastUpdated))
                    put("difficulty", article.difficulty.name)
                }
                jsonArray.put(articleJson)
            }
            
            securePrefs.edit()
                .putString("knowledge_base", jsonArray.toString())
                .apply()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun parseKnowledgeBaseFromJson(articleJson: JSONObject): KnowledgeBase {
        val tags = mutableListOf<String>()
        val tagsArray = articleJson.optJSONArray("tags")
        if (tagsArray != null) {
            for (i in 0 until tagsArray.length()) {
                tags.add(tagsArray.getString(i))
            }
        }
        
        return KnowledgeBase(
            id = articleJson.getString("id"),
            title = articleJson.getString("title"),
            content = articleJson.getString("content"),
            category = articleJson.getString("category"),
            tags = tags,
            views = articleJson.getInt("views"),
            helpfulCount = articleJson.getInt("helpfulCount"),
            lastUpdated = dateFormat.parse(articleJson.getString("lastUpdated")) ?: Date(),
            difficulty = Difficulty.valueOf(articleJson.getString("difficulty"))
        )
    }
    
    private fun parseAgentFromJson(agentJson: JSONObject): SupportAgent {
        val languages = mutableListOf<String>()
        val languagesArray = agentJson.optJSONArray("languages")
        if (languagesArray != null) {
            for (i in 0 until languagesArray.length()) {
                languages.add(languagesArray.getString(i))
            }
        }
        
        return SupportAgent(
            id = agentJson.getString("id"),
            name = agentJson.getString("name"),
            email = agentJson.getString("email"),
            department = agentJson.getString("department"),
            isOnline = agentJson.getBoolean("isOnline"),
            currentLoad = agentJson.getInt("currentLoad"),
            maxLoad = agentJson.getInt("maxLoad"),
            averageResponseTime = agentJson.getDouble("averageResponseTime"),
            satisfactionRating = agentJson.getDouble("satisfactionRating"),
            languages = languages
        )
    }
    
    private fun autoAssignTicket(ticket: SupportTicket) {
        // This would implement automatic ticket assignment logic
        // Based on category, priority, and agent availability
    }
    
    private fun sendTicketNotifications(ticket: SupportTicket) {
        // This would send notifications about new ticket creation
    }
    
    private fun sendStatusUpdateNotifications(ticket: SupportTicket) {
        // This would send notifications about ticket status updates
    }
    
    private fun sendResponseNotifications(ticket: SupportTicket, response: SupportResponse) {
        // This would send notifications about new responses
    }
    
    private fun sendResolutionNotifications(ticket: SupportTicket, resolution: String) {
        // This would send notifications about ticket resolution
    }
    
    private fun scheduleSatisfactionSurvey(ticket: SupportTicket) {
        // This would schedule a satisfaction survey for the user
    }
    
    private fun updateSupportAnalytics() {
        // This would update support analytics in real-time
        scope.launch {
            val analytics = getSupportAnalytics()
            // Update analytics cache
        }
    }
    
    private fun getCurrentUserId(): String {
        // This should get the current user ID from authentication service
        return securePrefs.getString("current_user_id", "user_default") ?: "user_default"
    }
    
    private fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "device_model" to android.os.Build.MODEL,
            "device_brand" to android.os.Build.BRAND,
            "os_version" to android.os.Build.VERSION.RELEASE,
            "app_version" to "1.0.0"
        )
    }
    
    private fun getAppVersion(): String {
        return "1.0.0"
    }
    
    private fun getUserAgent(): String {
        return "Edham Logistics Android App v${getAppVersion()}"
    }
}
