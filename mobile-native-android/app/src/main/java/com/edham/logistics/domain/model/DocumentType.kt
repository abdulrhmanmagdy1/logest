package com.edham.logistics.domain.model

enum class DocumentType {
    INVOICE,           // الفاتورة
    DELIVERY_NOTE,     // وثيقة التسليم
    RECEIPT,          // إيصال
    CONTRACT,         // عقد
    REPORT,           // تقرير
    SHIPMENT_LABEL,   // ملصق شحنة
    CUSTOMS,          // وثائق جمركية
    INSURANCE,        // وثائق تأمين
    OTHER
}
