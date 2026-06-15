package com.edham.logistics.domain.model

enum class DocumentStatus {
    DRAFT,           // مسودة
    IN_PROGRESS,     // قيد الإعداد
    PENDING_REVIEW,  // في انتظار المراجعة
    PENDING_SIGN,    // في انتظار التوقيع
    SIGNED,          // موقعة
    APPROVED,        // موافق عليها
    REJECTED,        // مرفوضة
    ARCHIVED         // مؤرشفة
}
