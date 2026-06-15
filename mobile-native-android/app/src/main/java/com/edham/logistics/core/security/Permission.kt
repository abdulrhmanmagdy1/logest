package com.edham.logistics.core.security

/**
 * Comprehensive permission definitions for the Edham Logistics system
 * Each permission represents a specific action or capability
 */
enum class Permission(
    val displayName: String,
    val description: String,
    val category: PermissionCategory,
    val isCritical: Boolean = false
) {
    
    // Dashboard Permissions
    VIEW_DASHBOARD(
        displayName = "عرض لوحة التحكم",
        description = "الوصول إلى لوحة التحكم الرئيسية",
        category = PermissionCategory.DASHBOARD
    ),
    
    // Shipment Management
    CREATE_SHIPMENT(
        displayName = "إنشاء شحنة",
        description = "إنشاء شحنات جديدة",
        category = PermissionCategory.SHIPMENT_MANAGEMENT
    ),
    MANAGE_SHIPMENTS(
        displayName = "إدارة الشحنات",
        description = "إدارة جميع الشحنات في النظام",
        category = PermissionCategory.SHIPMENT_MANAGEMENT,
        isCritical = true
    ),
    VIEW_OWN_SHIPMENTS(
        displayName = "عرض شحناتي",
        description = "عرض الشحنات الخاصة بالمستخدم",
        category = PermissionCategory.SHIPMENT_MANAGEMENT
    ),
    TRACK_SHIPMENT(
        displayName = "تتبع الشحنة",
        description = "تتبع حالة الشحنات في الوقت الفعلي",
        category = PermissionCategory.SHIPMENT_MANAGEMENT
    ),
    VIEW_SHIPMENT_HISTORY(
        displayName = "عرض سجل الشحنات",
        description = "عرض السجل التاريخي للشحنات",
        category = PermissionCategory.SHIPMENT_MANAGEMENT
    ),
    VIEW_OWN_ORDERS_ONLY(
        displayName = "عرض طلباتي فقط",
        description = "عرض الطلبات الخاصة بالمستخدم فقط",
        category = PermissionCategory.SHIPMENT_MANAGEMENT
    ),
    
    // Driver Management
    ASSIGN_DRIVERS(
        displayName = "تعيين سائقين",
        description = "تعيين سائقين للشحنات",
        category = PermissionCategory.DRIVER_MANAGEMENT,
        isCritical = true
    ),
    VIEW_DRIVER_PERFORMANCE(
        displayName = "عرض أداء السائقين",
        description = "عرض تقارير أداء السائقين",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    VIEW_TASKS(
        displayName = "عرض المهام",
        description = "عرض المهام المسندة للسائق",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    VIEW_OWN_TASKS_ONLY(
        displayName = "عرض مهامي فقط",
        description = "عرض المهام الخاصة بالسائق فقط",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    UPDATE_LOCATION(
        displayName = "تحديث الموقع",
        description = "تحديث موقع السائق في الوقت الفعلي",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    UPDATE_TASK_STATUS(
        displayName = "تحديث حالة المهمة",
        description = "تحديث حالة المهام المسندة",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    UPDATE_OWN_TASK_STATUS_ONLY(
        displayName = "تحديث حالة مهامي فقط",
        description = "تحديث حالة المهام الخاصة بالسائق فقط",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    VIEW_EARNINGS(
        displayName = "عرض الأرباح",
        description = "عرض أرباح السائق",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    VIEW_SCHEDULE(
        displayName = "عرض الجدول",
        description = "عرض جدول أعمال السائق",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    UPLOAD_PROOF_OF_DELIVERY(
        displayName = "رفع إثبات التسليم",
        description = "رفع صور أو مستندات إثبات التسليم",
        category = PermissionCategory.DRIVER_MANAGEMENT
    ),
    
    // Fleet Management
    VIEW_FLEET(
        displayName = "عرض الأسطول",
        description = "عرض جميع المركبات في الأسطول",
        category = PermissionCategory.FLEET_MANAGEMENT
    ),
    VIEW_FLEET_HEALTH(
        displayName = "عرض صحة الأسطول",
        description = "عرض حالة صحة الأسطول والصيانة",
        category = PermissionCategory.FLEET_MANAGEMENT
    ),
    MANAGE_VEHICLES(
        displayName = "إدارة المركبات",
        description = "إضافة وتعديل وحذف المركبات",
        category = PermissionCategory.FLEET_MANAGEMENT,
        isCritical = true
    ),
    VIEW_VEHICLE_DIAGNOSTICS(
        displayName = "عرض تشخيص المركبات",
        description = "عرض بيانات تشخيص المركبات",
        category = PermissionCategory.FLEET_MANAGEMENT
    ),
    
    // Operations Management
    MANAGE_OPERATIONS(
        displayName = "إدارة العمليات",
        description = "إدارة العمليات اليومية",
        category = PermissionCategory.OPERATIONS_MANAGEMENT,
        isCritical = true
    ),
    MANAGE_DISPATCH(
        displayName = "إدارة التوزيع",
        description = "توزيع الشحنات على السائقين",
        category = PermissionCategory.OPERATIONS_MANAGEMENT,
        isCritical = true
    ),
    ACCESS_OPERATIONS_ROOM(
        displayName = "الوصول لغرفة العمليات",
        description = "الوصول إلى غرفة العمليات المركزية",
        category = PermissionCategory.OPERATIONS_MANAGEMENT
    ),
    
    // Financial Management
    MANAGE_INVOICES(
        displayName = "إدارة الفواتير",
        description = "إنشاء وإدارة الفواتير",
        category = PermissionCategory.FINANCIAL_MANAGEMENT,
        isCritical = true
    ),
    VIEW_TRANSACTIONS(
        displayName = "عرض المعاملات",
        description = "عرض جميع المعاملات المالية",
        category = PermissionCategory.FINANCIAL_MANAGEMENT
    ),
    MANAGE_PAYMENTS(
        displayName = "إدارة المدفوعات",
        description = "إدارة المدفوعات والمصروفات",
        category = PermissionCategory.FINANCIAL_MANAGEMENT,
        isCritical = true
    ),
    APPROVE_PAYMENTS(
        displayName = "الموافقة على المدفوعات",
        description = "الموافقة على المدفوعات المستحقة",
        category = PermissionCategory.FINANCIAL_MANAGEMENT,
        isCritical = true
    ),
    VIEW_TAX_REPORTS(
        displayName = "عرض تقارير الضرائب",
        description = "عرض التقارير الضريبية",
        category = PermissionCategory.FINANCIAL_MANAGEMENT
    ),
    MANAGE_FINANCIAL_RECORDS(
        displayName = "إدارة السجلات المالية",
        description = "إدارة السجلات المالية والمحاسبية",
        category = PermissionCategory.FINANCIAL_MANAGEMENT,
        isCritical = true
    ),
    EXPORT_FINANCIAL_DATA(
        displayName = "تصدير البيانات المالية",
        description = "تصدير البيانات المالية والتقارير",
        category = PermissionCategory.FINANCIAL_MANAGEMENT
    ),
    VIEW_FINANCIAL_ANALYTICS(
        displayName = "عرض التحليلات المالية",
        description = "عرض التحليلات والتقارير المالية",
        category = PermissionCategory.FINANCIAL_MANAGEMENT
    ),
    
    // Workshop Management
    MANAGE_MAINTENANCE(
        displayName = "إدارة الصيانة",
        description = "إدارة عمليات صيانة المركبات",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    UPDATE_MAINTENANCE_STATUS(
        displayName = "تحديث حالة الصيانة",
        description = "تحديث حالة عمليات الصيانة",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    MANAGE_PARTS(
        displayName = "إدارة القطع",
        description = "إدارة قطع الغيار والمستلزمات",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    MANAGE_INVENTORY(
        displayName = "إدارة المخزون",
        description = "إدارة مخزون الورشة والقطع",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    CREATE_MAINTENANCE_REQUESTS(
        displayName = "إنشاء طلبات صيانة",
        description = "إنشاء طلبات صيانة جديدة",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    MANAGE_WORKSHOP_OPERATIONS(
        displayName = "إدارة عمليات الورشة",
        description = "إدارة جميع عمليات الورشة",
        category = PermissionCategory.WORKSHOP_MANAGEMENT
    ),
    
    // Reports and Analytics
    VIEW_REPORTS(
        displayName = "عرض التقارير",
        description = "عرض التقارير المختلفة",
        category = PermissionCategory.REPORTS
    ),
    VIEW_ANALYTICS(
        displayName = "عرض التحليلات",
        description = "عرض التحليلات والإحصائيات",
        category = PermissionCategory.REPORTS
    ),
    GENERATE_REPORTS(
        displayName = "إنشاء التقارير",
        description = "إنشاء تقارير مخصصة",
        category = PermissionCategory.REPORTS
    ),
    
    // User Management
    MANAGE_USERS(
        displayName = "إدارة المستخدمين",
        description = "إدارة حسابات المستخدمين",
        category = PermissionCategory.USER_MANAGEMENT,
        isCritical = true
    ),
    MANAGE_ROLES(
        displayName = "إدارة الأدوار",
        description = "إدارة أدوار المستخدمين وصلاحياتهم",
        category = PermissionCategory.USER_MANAGEMENT,
        isCritical = true
    ),
    
    // System Management
    MANAGE_SYSTEM(
        displayName = "إدارة النظام",
        description = "إدارة إعدادات وتكوينات النظام",
        category = PermissionCategory.SYSTEM_MANAGEMENT,
        isCritical = true
    ),
    VIEW_SYSTEM_LOGS(
        displayName = "عرض سجلات النظام",
        description = "عرض سجلات النظام والتدقيق",
        category = PermissionCategory.SYSTEM_MANAGEMENT
    ),
    
    // Customer Specific
    MANAGE_PROFILE(
        displayName = "إدارة الملف الشخصي",
        description = "إدارة الملف الشخصي للمستخدم",
        category = PermissionCategory.USER_PROFILE
    ),
    VIEW_WALLET(
        displayName = "عرض المحفظة",
        description = "عرض رصيد وحركات المحفظة",
        category = PermissionCategory.USER_PROFILE
    ),
    MAKE_PAYMENTS(
        displayName = "إجراء المدفوعات",
        description = "إجراء المدفوعات عبر التطبيق",
        category = PermissionCategory.USER_PROFILE
    ),
    GET_QUOTES(
        displayName = "الحصول على عروض الأسعار",
        description = "الحصول على عروض أسعار الشحن",
        category = PermissionCategory.USER_PROFILE
    );
    
    companion object {
        /**
         * Get all permissions by category
         */
        fun getPermissionsByCategory(category: PermissionCategory): List<Permission> {
            return values().filter { it.category == category }
        }
        
        /**
         * Get all critical permissions
         */
        fun getCriticalPermissions(): List<Permission> {
            return values().filter { it.isCritical }
        }
        
        /**
         * Get permissions by display name (case-insensitive)
         */
        fun findByDisplayName(displayName: String): Permission? {
            return values().find { 
                it.displayName.equals(displayName, ignoreCase = true) 
            }
        }
        
        /**
         * Get all permissions sorted by display name
         */
        fun getAllSorted(): List<Permission> {
            return values().sortedBy { it.displayName }
        }
    }
    
    /**
     * Get permission description formatted
     */
    fun getFormattedDescription(): String {
        val criticalMarker = if (isCritical) "🔴 " else "🟢 "
        return "$criticalMarker$displayName - $description"
    }
}

/**
 * Permission categories for organization
 */
enum class PermissionCategory(
    val displayName: String,
    val description: String
) {
    DASHBOARD("لوحة التحكم", "الوصول إلى لوحات التحكم المختلفة"),
    SHIPMENT_MANAGEMENT("إدارة الشحنات", "إدارة وتتبع الشحنات"),
    DRIVER_MANAGEMENT("إدارة السائقين", "إدارة السائقين والمهام"),
    FLEET_MANAGEMENT("إدارة الأسطول", "إدارة مركبات الأسطول"),
    OPERATIONS_MANAGEMENT("إدارة العمليات", "إدارة العمليات اليومية"),
    FINANCIAL_MANAGEMENT("إدارة المالية", "إدارة الشؤون المالية والفواتير"),
    WORKSHOP_MANAGEMENT("إدارة الورشة", "إدارة صيانة المركبات والورشة"),
    REPORTS("التقارير والتحليلات", "عرض وإنشاء التقارير"),
    USER_MANAGEMENT("إدارة المستخدمين", "إدارة حسابات المستخدمين والأدوار"),
    SYSTEM_MANAGEMENT("إدارة النظام", "إدارة إعدادات النظام"),
    USER_PROFILE("الملف الشخصي", "إدارة الملف الشخصي والمحفظة");
    
    companion object {
        fun getAllCategories(): List<PermissionCategory> {
            return values().toList()
        }
    }
}
