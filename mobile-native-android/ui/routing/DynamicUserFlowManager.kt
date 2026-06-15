// ============================================
// 🚀 Edham Logistics - Dynamic User Flow Manager
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.routing

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Dynamic User Flow Manager - مدير تدفق المستخدم الديناميكي
 * ============================================
 * تغيير واجهة التطبيق بناءً على إيميل المستخدم ودوره
 */

@Singleton
class DynamicUserFlowManager @Inject constructor() {
    
    private val _currentUser = MutableStateFlow<UserRole?>(null)
    val currentUser: StateFlow<UserRole?> = _currentUser
    
    private val _availableFeatures = MutableStateFlow<List<Feature>>(emptyList())
    val availableFeatures: StateFlow<List<Feature>> = _availableFeatures
    
    private val _navigationConfig = MutableStateFlow<NavigationConfig>(NavigationConfig())
    val navigationConfig: StateFlow<NavigationConfig> = _navigationConfig
    
    private val _uiConfiguration = MutableStateFlow<UIConfiguration>(UIConfiguration())
    val uiConfiguration: StateFlow<UIConfiguration> = _uiConfiguration
    
    /**
     * تحديد دور المستخدم بناءً على الإيميل
     */
    fun determineUserRole(email: String, name: String): UserRole {
        val role = when {
            email.endsWith("@driver.edham.com") -> UserRole.DRIVER
            email.endsWith("@supervisor.edham.com") -> UserRole.SUPERVISOR
            email.endsWith("@accountant.edham.com") -> UserRole.ACCOUNTANT
            email.endsWith("@maintenance.edham.com") -> UserRole.MAINTENANCE
            email.endsWith("@admin.edham.com") -> UserRole.ADMIN
            email.endsWith("@ceo.edham.com") -> UserRole.CEO
            email.endsWith("@manager.edham.com") -> UserRole.MANAGER
            email.contains("@gmail.com") || email.contains("@outlook.com") || 
            email.contains("@yahoo.com") || email.contains("@hotmail.com") -> UserRole.CLIENT
            else -> UserRole.CLIENT // افتراضي للعملاء
        }
        
        val userRole = UserRole(
            email = email,
            name = name,
            role = role,
            permissions = getPermissionsForRole(role),
            features = getFeaturesForRole(role),
            navigation = getNavigationForRole(role),
            uiConfig = getUIConfigForRole(role)
        )
        
        _currentUser.value = userRole
        _availableFeatures.value = userRole.features
        _navigationConfig.value = userRole.navigation
        _uiConfiguration.value = userRole.uiConfig
        
        return userRole
    }
    
    /**
     * الحصول على الصلاحيات بناءً على الدور
     */
    private fun getPermissionsForRole(role: UserRoleType): List<Permission> {
        return when (role) {
            UserRoleType.DRIVER -> listOf(
                Permission.VIEW_OWN_TASKS,
                Permission.UPDATE_DELIVERY_STATUS,
                Permission.CAPTURE_PROOF_OF_DELIVERY,
                Permission.VIEW_ROUTE,
                Permission.REPORT_ISSUE
            )
            UserRoleType.SUPERVISOR -> listOf(
                Permission.VIEW_ALL_TASKS,
                Permission.ASSIGN_TASKS,
                Permission.VIEW_FLEET_STATUS,
                Permission.GENERATE_REPORTS,
                Permission.MANAGE_DRIVERS,
                Permission.VIEW_ANALYTICS
            )
            UserRoleType.ACCOUNTANT -> listOf(
                Permission.VIEW_INVOICES,
                Permission.MANAGE_PAYMENTS,
                Permission.GENERATE_FINANCIAL_REPORTS,
                Permission.VIEW_TRANSACTIONS,
                Permission.MANAGE_BILLING
            )
            UserRoleType.MAINTENANCE -> listOf(
                Permission.VIEW_VEHICLE_STATUS,
                Permission.SCHEDULE_MAINTENANCE,
                Permission.REPORT_ISSUES,
                Permission.VIEW_MAINTENANCE_HISTORY,
                Permission.MANAGE_PARTS
            )
            UserRoleType.ADMIN -> listOf(
                Permission.MANAGE_USERS,
                Permission.VIEW_SYSTEM_LOGS,
                Permission.CONFIGURE_SYSTEM,
                Permission.MANAGE_PERMISSIONS,
                Permission.VIEW_ALL_DATA
            )
            UserRoleType.CEO -> listOf(
                Permission.VIEW_ALL_REPORTS,
                Permission.STRATEGIC_DECISIONS,
                Permission.VIEW_COMPANY_METRICS,
                Permission.APPROVE_MAJOR_DECISIONS
            )
            UserRoleType.MANAGER -> listOf(
                Permission.VIEW_TEAM_PERFORMANCE,
                Permission.MANAGE_TEAM,
                Permission.GENERATE_TEAM_REPORTS,
                Permission.APPROVE_REQUESTS
            )
            UserRoleType.CLIENT -> listOf(
                Permission.CREATE_SHIPMENT,
                Permission.VIEW_OWN_SHIPMENTS,
                Permission.TRACK_SHIPMENT,
                Permission.VIEW_INVOICES,
                Permission.MANAGE_ADDRESSES
            )
        }
    }
    
    /**
     * الحصول على الميزات بناءً على الدور
     */
    private fun getFeaturesForRole(role: UserRoleType): List<Feature> {
        return when (role) {
            UserRoleType.DRIVER -> listOf(
                Feature("live_map", "الخريطة الحية", Icons.Map, true),
                Feature("tasks", "المهام", Icons.Task, true),
                Feature("navigation", "الملاحة", Icons.Navigation, true),
                Feature("camera", "الكاميرا", Icons.Camera, true),
                Feature("messages", "الرسائل", Icons.Message, true),
                Feature("profile", "الملف الشخصي", Icons.Person, true)
            )
            UserRoleType.SUPERVISOR -> listOf(
                Feature("dashboard", "لوحة التحكم", Icons.Dashboard, true),
                Feature("fleet", "الأسطول", Icons.Truck, true),
                Feature("analytics", "التحليلات", Icons.Analytics, true),
                Feature("dispatch", "التوزيع", Icons.Dispatch, true),
                Feature("reports", "التقارير", Icons.Report, true),
                Feature("drivers", "السائقين", Icons.People, true)
            )
            UserRoleType.ACCOUNTANT -> listOf(
                Feature("invoices", "الفواتير", Icons.Invoice, true),
                Feature("payments", "المدفوعات", Icons.Payment, true),
                Feature("transactions", "المعاملات", Icons.Transaction, true),
                Feature("reports", "التقارير المالية", Icons.FinancialReport, true),
                Feature("billing", "الفوترة", Icons.Billing, true),
                Feature("analytics", "تحليلات مالية", Icons.Money, true)
            )
            UserRoleType.MAINTENANCE -> listOf(
                Feature("vehicles", "المركبات", Icons.Car, true),
                Feature("maintenance", "الصيانة", Icons.Tools, true),
                Feature("parts", "قطع الغيار", Icons.Parts, true),
                Feature("schedule", "الجدولة", Icons.Schedule, true),
                Feature("reports", "تقارير الصيانة", Icons.MaintenanceReport, true),
                Feature("inventory", "المخزون", Icons.Inventory, true)
            )
            UserRoleType.ADMIN -> listOf(
                Feature("system", "النظام", Icons.Settings, true),
                Feature("users", "المستخدمون", Icons.Users, true),
                Feature("permissions", "الصلاحيات", Icons.Security, true),
                Feature("logs", "السجلات", Icons.Log, true),
                Feature("backup", "النسخ الاحتياطي", Icons.Backup, true),
                Feature("api", "واجهات البرمجة", Icons.Api, true)
            )
            UserRoleType.CEO -> listOf(
                Feature("executive_dashboard", "لوحة تنفيذية", Icons.Executive, true),
                Feature("company_metrics", "مقاييس الشركة", Icons.Metrics, true),
                Feature("strategic_reports", "تقارير استراتيجية", Icons.Strategy, true),
                Feature("investor_relations", "علاقات المستثمرين", Icons.Investor, true),
                Feature("board_reports", "تقارير مجلس الإدارة", Icons.Board, true)
            )
            UserRoleType.MANAGER -> listOf(
                Feature("team_dashboard", "لوحة الفريق", Icons.Team, true),
                Feature("performance", "الأداء", Icons.Performance, true),
                Feature("schedule", "الجدولة", Icons.Calendar, true),
                Feature("reports", "تقارير الفريق", Icons.TeamReport, true),
                Feature("approvals", "الموافقات", Icons.Approve, true)
            )
            UserRoleType.CLIENT -> listOf(
                Feature("home", "الرئيسية", Icons.Home, true),
                Feature("create_shipment", "إنشاء شحنة", Icons.Add, true),
                Feature("track", "تتبع", Icons.Track, true),
                Feature("history", "السجل", Icons.History, true),
                Feature("invoices", "الفواتير", Icons.Invoice, true),
                Feature("profile", "الملف الشخصي", Icons.Person, true),
                Feature("support", "الدعم", Icons.Support, true)
            )
        }
    }
    
    /**
     * الحصول على تكوين التنقل بناءً على الدور
     */
    private fun getNavigationForRole(role: UserRoleType): NavigationConfig {
        return when (role) {
            UserRoleType.DRIVER -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = false,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "live_map",
                tabs = listOf("live_map", "tasks", "navigation", "camera", "messages")
            )
            UserRoleType.SUPERVISOR -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = true,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "dashboard",
                tabs = listOf("dashboard", "fleet", "analytics", "dispatch", "reports")
            )
            UserRoleType.ACCOUNTANT -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = false,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "invoices",
                tabs = listOf("invoices", "payments", "transactions", "reports", "billing")
            )
            UserRoleType.MAINTENANCE -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = false,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "vehicles",
                tabs = listOf("vehicles", "maintenance", "parts", "schedule", "reports")
            )
            UserRoleType.ADMIN -> NavigationConfig(
                bottomNavVisible = false,
                topBarVisible = true,
                drawerEnabled = true,
                navigationStyle = NavigationStyle.DRAWER,
                defaultTab = "system",
                tabs = listOf("system", "users", "permissions", "logs", "backup", "api")
            )
            UserRoleType.CEO -> NavigationConfig(
                bottomNavVisible = false,
                topBarVisible = true,
                drawerEnabled = true,
                navigationStyle = NavigationStyle.DRAWER,
                defaultTab = "executive_dashboard",
                tabs = listOf("executive_dashboard", "company_metrics", "strategic_reports", "investor_relations", "board_reports")
            )
            UserRoleType.MANAGER -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = false,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "team_dashboard",
                tabs = listOf("team_dashboard", "performance", "schedule", "reports", "approvals")
            )
            UserRoleType.CLIENT -> NavigationConfig(
                bottomNavVisible = true,
                topBarVisible = true,
                drawerEnabled = false,
                navigationStyle = NavigationStyle.BOTTOM_TABS,
                defaultTab = "home",
                tabs = listOf("home", "create_shipment", "track", "history", "invoices", "profile", "support")
            )
        }
    }
    
    /**
     * الحصول على تكوين الواجهة بناءً على الدور
     */
    private fun getUIConfigForRole(role: UserRoleType): UIConfiguration {
        return when (role) {
            UserRoleType.DRIVER -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = true,
                showQuickActions = true,
                compactMode = true,
                dataRefreshInterval = 30, // ثانية
                enableOfflineMode = true,
                showBatteryWarning = true
            )
            UserRoleType.SUPERVISOR -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = true,
                showQuickActions = true,
                compactMode = false,
                dataRefreshInterval = 60,
                enableOfflineMode = false,
                showBatteryWarning = false
            )
            UserRoleType.ACCOUNTANT -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = false,
                showQuickActions = false,
                compactMode = false,
                dataRefreshInterval = 300, // 5 دقائق
                enableOfflineMode = false,
                showBatteryWarning = false
            )
            UserRoleType.MAINTENANCE -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = false,
                showQuickActions = true,
                compactMode = true,
                dataRefreshInterval = 120, // دقيقتان
                enableOfflineMode = true,
                showBatteryWarning = false
            )
            UserRoleType.ADMIN -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = false,
                showQuickActions = false,
                compactMode = false,
                dataRefreshInterval = 600, // 10 دقائق
                enableOfflineMode = false,
                showBatteryWarning = false
            )
            UserRoleType.CEO -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = false,
                showQuickActions = false,
                compactMode = false,
                dataRefreshInterval = 1800, // 30 دقيقة
                enableOfflineMode = false,
                showBatteryWarning = false
            )
            UserRoleType.MANAGER -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = true,
                showQuickActions = true,
                compactMode = false,
                dataRefreshInterval = 180, // 3 دقائق
                enableOfflineMode = false,
                showBatteryWarning = false
            )
            UserRoleType.CLIENT -> UIConfiguration(
                theme = ThemeType.DARK,
                showNotifications = true,
                showLiveTracking = true,
                showQuickActions = true,
                compactMode = false,
                dataRefreshInterval = 120, // دقيقتان
                enableOfflineMode = false,
                showBatteryWarning = false
            )
        }
    }
    
    /**
     * التحقق من صلاحية المستخدم
     */
    fun hasPermission(permission: Permission): Boolean {
        return _currentUser.value?.permissions?.contains(permission) == true
    }
    
    /**
     * التحقق من توفر ميزة
     */
    fun hasFeature(featureId: String): Boolean {
        return _availableFeatures.value.any { it.id == featureId }
    }
    
    /**
     * الحصول على ميزة
     */
    fun getFeature(featureId: String): Feature? {
        return _availableFeatures.value.find { it.id == featureId }
    }
    
    /**
     * تحديث دور المستخدم
     */
    fun updateUserRole(newRole: UserRoleType) {
        val currentUser = _currentUser.value ?: return
        
        val updatedUser = currentUser.copy(
            role = newRole,
            permissions = getPermissionsForRole(newRole),
            features = getFeaturesForRole(newRole),
            navigation = getNavigationForRole(newRole),
            uiConfig = getUIConfigForRole(newRole)
        )
        
        _currentUser.value = updatedUser
        _availableFeatures.value = updatedUser.features
        _navigationConfig.value = updatedUser.navigation
        _uiConfiguration.value = updatedUser.uiConfig
    }
    
    /**
     * إضافة ميزة مخصصة
     */
    fun addCustomFeature(feature: Feature) {
        val currentFeatures = _availableFeatures.value.toMutableList()
        currentFeatures.add(feature)
        _availableFeatures.value = currentFeatures
    }
    
    /**
     * إزالة ميزة
     */
    fun removeFeature(featureId: String) {
        val currentFeatures = _availableFeatures.value.toMutableList()
        currentFeatures.removeAll { it.id == featureId }
        _availableFeatures.value = currentFeatures
    }
    
    /**
     * الحصول على تكوين الشاشة بناءً على الدور
     */
    fun getScreenConfiguration(screenId: String): ScreenConfiguration? {
        val role = _currentUser.value?.role ?: return null
        
        return when (screenId) {
            "dashboard" -> when (role) {
                UserRoleType.SUPERVISOR -> ScreenConfiguration(
                    title = "لوحة التحكم",
                    showCharts = true,
                    showMetrics = true,
                    refreshInterval = 60,
                    widgets = listOf("fleet_status", "active_tasks", "performance_metrics")
                )
                UserRoleType.CEO -> ScreenConfiguration(
                    title = "لوحة تنفيذية",
                    showCharts = true,
                    showMetrics = true,
                    refreshInterval = 1800,
                    widgets = listOf("company_overview", "financial_metrics", "strategic_kpis")
                )
                UserRoleType.MANAGER -> ScreenConfiguration(
                    title = "لوحة الفريق",
                    showCharts = true,
                    showMetrics = true,
                    refreshInterval = 180,
                    widgets = listOf("team_performance", "task_status", "resource_utilization")
                )
                else -> null
            }
            "shipments" -> when (role) {
                UserRoleType.CLIENT -> ScreenConfiguration(
                    title = "شحناتي",
                    showCharts = false,
                    showMetrics = true,
                    refreshInterval = 120,
                    widgets = listOf("active_shipments", "delivery_status", "tracking_map")
                )
                UserRoleType.DRIVER -> ScreenConfiguration(
                    title = "مهامي",
                    showCharts = false,
                    showMetrics = true,
                    refreshInterval = 30,
                    widgets = listOf("current_task", "route_info", "delivery_timeline")
                )
                else -> null
            }
            else -> null
        }
    }
    
    /**
     * الحصول على مسار التنقل الأولي
     */
    fun getInitialRoute(): String {
        return _navigationConfig.value.defaultTab
    }
    
    /**
     * تسجيل الخروج
     */
    fun logout() {
        _currentUser.value = null
        _availableFeatures.value = emptyList()
        _navigationConfig.value = NavigationConfig()
        _uiConfiguration.value = UIConfiguration()
    }
}

/**
 * ============================================
// Data Classes and Enums
// ============================================
 */

data class UserRole(
    val email: String,
    val name: String,
    val role: UserRoleType,
    val permissions: List<Permission>,
    val features: List<Feature>,
    val navigation: NavigationConfig,
    val uiConfig: UIConfiguration
)

data class Feature(
    val id: String,
    val title: String,
    val icon: String, // Icon name or resource
    val enabled: Boolean,
    val description: String = ""
)

data class NavigationConfig(
    val bottomNavVisible: Boolean = true,
    val topBarVisible: Boolean = true,
    val drawerEnabled: Boolean = false,
    val navigationStyle: NavigationStyle = NavigationStyle.BOTTOM_TABS,
    val defaultTab: String = "home",
    val tabs: List<String> = emptyList()
)

data class UIConfiguration(
    val theme: ThemeType = ThemeType.DARK,
    val showNotifications: Boolean = true,
    val showLiveTracking: Boolean = false,
    val showQuickActions: Boolean = false,
    val compactMode: Boolean = false,
    val dataRefreshInterval: Int = 60, // بالثواني
    val enableOfflineMode: Boolean = false,
    val showBatteryWarning: Boolean = false
)

data class ScreenConfiguration(
    val title: String,
    val showCharts: Boolean,
    val showMetrics: Boolean,
    val refreshInterval: Int,
    val widgets: List<String>
)

enum class UserRoleType {
    DRIVER, SUPERVISOR, ACCOUNTANT, MAINTENANCE, ADMIN, CEO, MANAGER, CLIENT
}

enum class Permission {
    // Driver permissions
    VIEW_OWN_TASKS, UPDATE_DELIVERY_STATUS, CAPTURE_PROOF_OF_DELIVERY, VIEW_ROUTE, REPORT_ISSUE,
    
    // Supervisor permissions
    VIEW_ALL_TASKS, ASSIGN_TASKS, VIEW_FLEET_STATUS, GENERATE_REPORTS, MANAGE_DRIVERS, VIEW_ANALYTICS,
    
    // Accountant permissions
    VIEW_INVOICES, MANAGE_PAYMENTS, GENERATE_FINANCIAL_REPORTS, VIEW_TRANSACTIONS, MANAGE_BILLING,
    
    // Maintenance permissions
    VIEW_VEHICLE_STATUS, SCHEDULE_MAINTENANCE, REPORT_ISSUES, VIEW_MAINTENANCE_HISTORY, MANAGE_PARTS,
    
    // Admin permissions
    MANAGE_USERS, VIEW_SYSTEM_LOGS, CONFIGURE_SYSTEM, MANAGE_PERMISSIONS, VIEW_ALL_DATA,
    
    // CEO permissions
    VIEW_ALL_REPORTS, STRATEGIC_DECISIONS, VIEW_COMPANY_METRICS, APPROVE_MAJOR_DECISIONS,
    
    // Manager permissions
    VIEW_TEAM_PERFORMANCE, MANAGE_TEAM, GENERATE_TEAM_REPORTS, APPROVE_REQUESTS,
    
    // Client permissions
    CREATE_SHIPMENT, VIEW_OWN_SHIPMENTS, TRACK_SHIPMENT, VIEW_INVOICES, MANAGE_ADDRESSES
}

enum class NavigationStyle {
    BOTTOM_TABS, DRAWER, TOP_TABS, SIDE_NAVIGATION
}

enum class ThemeType {
    LIGHT, DARK, AUTO
}

/**
 * ============================================
// Icon Names Constants
// ============================================
 */
object Icons {
    const val Map = "map"
    const val Task = "task"
    const val Navigation = "navigation"
    const val Camera = "camera"
    const val Message = "message"
    const val Person = "person"
    const val Dashboard = "dashboard"
    const val Truck = "truck"
    const val Analytics = "analytics"
    const val Dispatch = "dispatch"
    const val Report = "report"
    const val People = "people"
    const val Invoice = "invoice"
    const val Payment = "payment"
    const val Transaction = "transaction"
    const val FinancialReport = "financial_report"
    const val Billing = "billing"
    const val Money = "money"
    const val Car = "car"
    const val Tools = "tools"
    const val Parts = "parts"
    const val Schedule = "schedule"
    const val MaintenanceReport = "maintenance_report"
    const val Inventory = "inventory"
    const val Settings = "settings"
    const val Users = "users"
    const val Security = "security"
    const val Log = "log"
    const val Backup = "backup"
    const val Api = "api"
    const val Executive = "executive"
    const val Metrics = "metrics"
    const val Strategy = "strategy"
    const val Investor = "investor"
    const val Board = "board"
    const val Team = "team"
    const val Performance = "performance"
    const val Calendar = "calendar"
    const val TeamReport = "team_report"
    const val Approve = "approve"
    const val Home = "home"
    const val Add = "add"
    const val Track = "track"
    const val History = "history"
    const val Support = "support"
}
