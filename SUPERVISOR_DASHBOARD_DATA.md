# 📊 لوحة تحكم المشرف - Supervisor Dashboard Data Collection

## 🎯 ما يراه المشرف في الوقت الفعلي

### **الشاشة الرئيسية للمشرف:**

```
┌──────────────────────────────────────────────┐
│  📊 لوحة تحكم العمليات - OPERATIONS CONTROL │
├──────────────────────────────────────────────┤
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 📍 الأسطول الحي (Live Fleet)           │ │
│  │ ─────────────────────────────────────  │ │
│  │ السائقون النشطون: 15                    │ │
│  │ المركبات قيد الاستخدام: 12             │ │
│  │ الرحلات الجارية: 18                    │ │
│  │ الرحلات المكتملة اليوم: 45              │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🗺️ الخريطة (Live Map)                   │ │
│  │ ─────────────────────────────────────  │ │
│  │ [Google Maps with:                     │ │
│  │  • 15 green dots (active drivers)      │ │
│  │  • 18 yellow dots (ongoing trips)      │ │
│  │  • Routes with polylines               │ │
│  │  • Destination markers]                │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 📋 الرحلات الجارية (Ongoing Trips)     │ │
│  │ ─────────────────────────────────────  │ │
│  │ EDH-K5XYZ9AAA | أحمد م. | 🟢 95 km  │ │
│  │ EDH-K5XYZ9BBB | محمد ع. | 🟡 120 km  │ │
│  │ EDH-K5XYZ9CCC | سارة خ. | 🟠 45 km   │ │
│  │ [عرض الكل]                            │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 🚨 التنبيهات (Alerts)                  │ │
│  │ ─────────────────────────────────────  │ │
│  │ ⚠️ تأخر محتمل: EDH-K5XYZ9DDD          │ │
│  │ 🔴 مشكلة في المركبة: ABC-1234         │ │
│  │ 📍 سائق خارج المسار: محمد ع.           │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 📈 الإحصائيات (Statistics)             │ │
│  │ ─────────────────────────────────────  │ │
│  │ أداء اليوم:                            │ │
│  │ • إجمالي الرحلات: 45                  │ │
│  │ • المسافة الإجمالية: 4,850 كم         │ │
│  │ • متوسط التقييم: 4.7/5                │ │
│  │ • الأرباح: 15,850 SAR                │ │
│  └────────────────────────────────────────┘ │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 📱 البيانات المفصلة المرسلة للمشرف (Detailed Data Sent)

### **1️⃣ بيانات السائق الحية (Driver Real-time Data)**

```json
{
  "driverId": "507f1f77bcf86cd799439012",
  "name": "أحمد محمد",
  "phone": "0501234567",
  "email": "ahmed.m@edham.com",
  "status": "online",
  "rating": 4.8,
  "totalRatings": 512,
  
  "currentTrip": {
    "trackingNumber": "EDH-K5XYZ9ABC",
    "status": "on_the_way",
    "startTime": "2026-05-22T12:00:00Z",
    "elapsedTime": "2:45",
    
    "currentLocation": {
      "latitude": 24.8500,
      "longitude": 46.7200,
      "timestamp": "2026-05-22T14:45:30Z",
      "accuracy": 15,
      "speed": 85,
      "heading": 45,
      "altitude": 456
    },
    
    "pickup": {
      "address": "طريق الملك فهد، الرياض",
      "coordinates": { "lat": 24.7136, "lng": 46.6753 },
      "completedTime": "2026-05-22T09:30:00Z",
      "cargo": {
        "type": "electronics",
        "weight": 50,
        "quantity": 1,
        "description": "أجهزة إلكترونية"
      }
    },
    
    "delivery": {
      "address": "شارع التقدم، جدة",
      "coordinates": { "lat": 21.5433, "lng": 39.1728 },
      "customerName": "محمد علي",
      "customerPhone": "0541234567",
      "expectedTime": "2026-05-22T13:30:00Z"
    },
    
    "route": {
      "totalDistance": 140,
      "distanceTraveled": 95,
      "distanceRemaining": 45,
      "estimatedTimeOfArrival": "2026-05-22T14:50:00Z",
      "eta_minutes": 5
    },
    
    "performance": {
      "avgSpeed": 34.5,
      "maxSpeed": 95,
      "fuelUsed": 7.5,
      "fuelLevel": 65
    }
  },
  
  "dailyStats": {
    "date": "2026-05-22",
    "totalTrips": 3,
    "completedTrips": 2,
    "ongoingTrips": 1,
    "totalDistance": 285,
    "totalTime": 8.75,
    "avgRating": 4.9,
    "totalEarnings": 850,
    "fuelUsed": 35
  },
  
  "vehicle": {
    "id": "507f1f77bcf86cd799439013",
    "plateNumber": "ABC-1234",
    "type": "box_truck",
    "capacity": 5000,
    "fuel": 0.65,
    "temperature": 22,
    "status": "good",
    "lastMaintenance": "2026-05-15",
    "nextMaintenance": "2026-06-15"
  },
  
  "locationHistory": [
    { "lat": 24.7136, "lng": 46.6753, "time": "09:30", "status": "picked_up" },
    { "lat": 24.7500, "lng": 46.7000, "time": "10:00", "status": "on_the_way" },
    { "lat": 24.8000, "lng": 46.7100, "time": "12:00", "status": "on_the_way" },
    { "lat": 24.8500, "lng": 46.7200, "time": "14:45", "status": "on_the_way" }
  ]
}
```

---

### **2️⃣ بيانات الرحلة المفصلة (Trip Detailed Data)**

```json
{
  "tripData": {
    "trackingNumber": "EDH-K5XYZ9ABC",
    "createdAt": "2026-05-22T10:00:00Z",
    
    "customer": {
      "id": "507f1f77bcf86cd799439000",
      "name": "محمد علي",
      "phone": "0541234567",
      "email": "customer@example.com",
      "company": "شركة النقل الدولية"
    },
    
    "shipment": {
      "cargo": {
        "type": "electronics",
        "description": "أجهزة إلكترونية - شاشات LED",
        "weight": 50,
        "unit": "kg",
        "quantity": 1,
        "dimensions": {
          "length": 120,
          "width": 60,
          "height": 40,
          "unit": "cm"
        },
        "hazardous": false,
        "fragile": true,
        "specialRequirements": ["Handle with care", "Keep upright"]
      },
      
      "pickup": {
        "address": {
          "street": "طريق الملك فهد",
          "city": "الرياض",
          "region": "منطقة وسط",
          "coordinates": { "lat": 24.7136, "lng": 46.6753 }
        },
        "contactName": "محمود أحمد",
        "contactPhone": "0551234567",
        "scheduledDate": "2026-05-22T09:00:00Z",
        "actualDate": "2026-05-22T09:30:00Z",
        "status": "completed"
      },
      
      "delivery": {
        "address": {
          "street": "شارع التقدم",
          "city": "جدة",
          "region": "منطقة غرب",
          "coordinates": { "lat": 21.5433, "lng": 39.1728 }
        },
        "contactName": "محمد علي",
        "contactPhone": "0541234567",
        "scheduledDate": "2026-05-22T13:30:00Z",
        "actualDate": null,
        "expectedDate": "2026-05-22T14:50:00Z",
        "status": "pending"
      }
    },
    
    "assignment": {
      "driver": {
        "id": "507f1f77bcf86cd799439012",
        "name": "أحمد محمد",
        "phone": "0501234567",
        "rating": 4.8
      },
      "truck": {
        "id": "507f1f77bcf86cd799439013",
        "plateNumber": "ABC-1234",
        "type": "box_truck"
      },
      "assignedAt": "2026-05-22T11:00:00Z",
      "assignedBy": "admin@edham.com"
    },
    
    "pricing": {
      "basePrice": 100,
      "weightCharge": 25,
      "distanceCharge": 70,
      "specialHandlingCharge": 10,
      "tax": 20.5,
      "discount": 0,
      "total": 225.5,
      "currency": "SAR"
    },
    
    "statusHistory": [
      {
        "status": "pending",
        "timestamp": "2026-05-22T10:00:00Z",
        "location": null,
        "notes": "Order created",
        "updatedBy": "customer@example.com"
      },
      {
        "status": "confirmed",
        "timestamp": "2026-05-22T10:30:00Z",
        "location": { "lat": 24.7000, "lng": 46.6000 },
        "notes": "Admin confirmed",
        "updatedBy": "admin@edham.com"
      },
      {
        "status": "assigned",
        "timestamp": "2026-05-22T11:00:00Z",
        "location": { "lat": 24.7100, "lng": 46.6100 },
        "notes": "Driver assigned",
        "updatedBy": "admin@edham.com"
      },
      {
        "status": "at_pickup",
        "timestamp": "2026-05-22T09:15:00Z",
        "location": { "lat": 24.7136, "lng": 46.6753 },
        "notes": "At pickup location",
        "updatedBy": "507f1f77bcf86cd799439012"
      },
      {
        "status": "picked_up",
        "timestamp": "2026-05-22T09:30:00Z",
        "location": { "lat": 24.7136, "lng": 46.6753 },
        "notes": "Cargo loaded with proof",
        "updatedBy": "507f1f77bcf86cd799439012"
      },
      {
        "status": "on_the_way",
        "timestamp": "2026-05-22T10:00:00Z",
        "location": { "lat": 24.7500, "lng": 46.7000 },
        "notes": "En route to delivery",
        "updatedBy": "507f1f77bcf86cd799439012"
      }
    ],
    
    "documents": [
      {
        "type": "cargo_photo",
        "url": "https://api.edham.com/uploads/...",
        "uploadedAt": "2026-05-22T09:31:00Z",
        "metadata": {
          "gps": { "lat": 24.7136, "lng": 46.6753 },
          "timestamp": "2026-05-22T09:30:45Z"
        }
      },
      {
        "type": "digital_signature",
        "url": "https://api.edham.com/uploads/...",
        "uploadedAt": "2026-05-22T09:32:00Z",
        "signedBy": "محمود أحمد"
      }
    ],
    
    "tracking": {
      "polyline": "encoded polyline string...",
      "checkpoints": [
        { "lat": 24.7900, "lng": 46.7100, "name": "مركز التوزيع", "arrivalTime": "2026-05-22T10:00:00Z" },
        { "lat": 24.5000, "lng": 46.8500, "name": "طريق الدمام", "arrivalTime": "2026-05-22T11:30:00Z" }
      ]
    }
  }
}
```

---

### **3️⃣ بيانات الاستبيان (Survey Data)**

```json
{
  "surveyData": {
    "tripId": "EDH-K5XYZ9ABC",
    "completedAt": "2026-05-22T13:35:00Z",
    "duration": 180,
    
    "customer": {
      "id": "507f1f77bcf86cd799439000",
      "name": "محمد علي"
    },
    
    "driver": {
      "id": "507f1f77bcf86cd799439012",
      "name": "أحمد محمد"
    },
    
    "responses": [
      {
        "question": "كيف تقيم جودة الخدمة؟",
        "answer": 5,
        "options": ["سيئة", "مقبولة", "جيدة", "جيدة جداً", "ممتازة"]
      },
      {
        "question": "هل كان السائق احترافياً؟",
        "answer": "نعم بالتأكيد",
        "options": ["لا", "إلى حد ما", "نعم", "نعم جداً", "نعم بالتأكيد"]
      },
      {
        "question": "هل وصلت البضاعة بأمان؟",
        "answer": "نعم تماماً",
        "options": ["وجدت مشاكل", "مع أضرار بسيطة", "نعم مع احتياطات", "نعم بأمان", "نعم تماماً"]
      },
      {
        "question": "هل التزم السائق بالوقت؟",
        "answer": "نعم دقيقاً",
        "options": ["تأخر كثيراً", "متأخراً", "متأخراً قليلاً", "في الموعد", "نعم دقيقاً"]
      },
      {
        "question": "هل كانت الرحلة آمنة؟",
        "answer": "آمنة جداً",
        "options": ["غير آمنة", "مشاكل أمان", "مقبولة", "آمنة", "آمنة جداً"]
      },
      {
        "question": "التقييم العام",
        "answer": 5,
        "scale": "1-5 stars"
      },
      {
        "question": "ملاحظات أو تعليقات",
        "answer": "خدمة ممتازة وسائق احترافي جداً. شكراً لك على العمل الرائع والالتزام بالوقت."
      },
      {
        "question": "هل تود الاتصال بنا؟",
        "answer": "نعم",
        "contact": "0541234567"
      }
    ],
    
    "metrics": {
      "averageRating": 5.0,
      "professionalism": 5,
      "cargoSafety": 5,
      "timeliness": 5,
      "tripSafety": 5,
      "overallSatisfaction": 5.0
    }
  }
}
```

---

### **4️⃣ بيانات المرفقات والمستندات (Attachments Data)**

```json
{
  "documentsData": {
    "tripId": "EDH-K5XYZ9ABC",
    "uploadedAt": "2026-05-22T14:50:00Z",
    
    "uploads": [
      {
        "documentId": "doc_001",
        "type": "cargo_pickup_photo",
        "fileName": "pickup_2026-05-22_09-30.jpg",
        "fileSize": 2048000,
        "mimeType": "image/jpeg",
        "url": "https://api.edham.com/uploads/doc_001",
        "uploadedAt": "2026-05-22T09:31:00Z",
        "metadata": {
          "width": 2048,
          "height": 1536,
          "gps": { "lat": 24.7136, "lng": 46.6753 },
          "timestamp": "2026-05-22T09:30:45Z",
          "cameraModel": "Samsung Galaxy S21"
        }
      },
      {
        "documentId": "doc_002",
        "type": "cargo_delivery_photo",
        "fileName": "delivery_2026-05-22_14-50.jpg",
        "fileSize": 2150000,
        "mimeType": "image/jpeg",
        "url": "https://api.edham.com/uploads/doc_002",
        "uploadedAt": "2026-05-22T14:51:00Z",
        "metadata": {
          "width": 2048,
          "height": 1536,
          "gps": { "lat": 21.5433, "lng": 39.1728 },
          "timestamp": "2026-05-22T14:50:52Z"
        }
      },
      {
        "documentId": "doc_003",
        "type": "digital_signature_customer",
        "fileName": "sig_customer_2026-05-22_14-50.png",
        "fileSize": 512000,
        "mimeType": "image/png",
        "url": "https://api.edham.com/uploads/doc_003",
        "uploadedAt": "2026-05-22T14:52:00Z",
        "metadata": {
          "signedBy": "محمد علي",
          "signatureType": "customer",
          "gps": { "lat": 21.5433, "lng": 39.1728 }
        }
      },
      {
        "documentId": "doc_004",
        "type": "waybill",
        "fileName": "waybill_EDH-K5XYZ9ABC.pdf",
        "fileSize": 512000,
        "mimeType": "application/pdf",
        "url": "https://api.edham.com/uploads/doc_004",
        "uploadedAt": "2026-05-22T14:53:00Z"
      },
      {
        "documentId": "doc_005",
        "type": "incident_report",
        "fileName": "incident_2026-05-22_report.txt",
        "content": "تأخر طفيف بسبب ازدحام في جسر الملك فهد، استغرق 15 دقيقة إضافية",
        "uploadedAt": "2026-05-22T12:30:00Z",
        "issues": [
          "traffic_jam",
          "weather_conditions"
        ],
        "attachments": [
          "https://api.edham.com/uploads/incident_photo_1.jpg",
          "https://api.edham.com/uploads/incident_photo_2.jpg"
        ]
      }
    ]
  }
}
```

---

## 📊 تحديثات المشرف على الخريطة (Supervisor Map Updates)

```
تحديثات فورية كل 5 ثواني:

{
  "mapUpdate": {
    "timestamp": "2026-05-22T14:45:30Z",
    "drivers": [
      {
        "driverId": "507f1f77bcf86cd799439012",
        "name": "أحمد محمد",
        "location": {
          "lat": 24.8500,
          "lng": 46.7200,
          "speed": 85,
          "heading": 45
        },
        "tripId": "EDH-K5XYZ9ABC",
        "status": "on_the_way",
        "eta": "2026-05-22T14:50:00Z",
        "color": "yellow"  // yellow for ongoing
      },
      {
        "driverId": "507f1f77bcf86cd799439014",
        "name": "محمد عبدالله",
        "location": {
          "lat": 24.7200,
          "lng": 46.5000,
          "speed": 0,
          "heading": 0
        },
        "tripId": "EDH-K5XYZ9BBB",
        "status": "at_delivery",
        "eta": "2026-05-22T14:40:00Z",
        "color": "orange"  // orange for at delivery
      }
    ],
    "destinations": [
      {
        "tripId": "EDH-K5XYZ9ABC",
        "lat": 21.5433,
        "lng": 39.1728,
        "address": "شارع التقدم، جدة",
        "icon": "destination_red"
      }
    ],
    "routes": [
      {
        "tripId": "EDH-K5XYZ9ABC",
        "polyline": "encoded polyline...",
        "color": "blue",
        "weight": 4
      }
    ]
  }
}
```

---

## 🔔 إشعارات المشرف (Supervisor Alerts)

```json
{
  "alertSystem": {
    "alerts": [
      {
        "alertId": "alert_001",
        "type": "delay_warning",
        "tripId": "EDH-K5XYZ9CCC",
        "driver": "سارة خالد",
        "message": "تأخر محتمل - السائقة متأخرة 25 دقيقة عن الموعد المتوقع",
        "severity": "high",
        "timestamp": "2026-05-22T14:40:00Z",
        "actions": ["Contact Driver", "Reassign Trip", "Customer Notification"]
      },
      {
        "alertId": "alert_002",
        "type": "vehicle_issue",
        "vehicleId": "ABC-1234",
        "driver": "أحمد محمد",
        "message": "مستوى الوقود منخفض - 15% متبقي",
        "severity": "medium",
        "timestamp": "2026-05-22T14:35:00Z"
      },
      {
        "alertId": "alert_003",
        "type": "off_route",
        "tripId": "EDH-K5XYZ9DDD",
        "driver": "محمد علي",
        "message": "السائق خارج المسار المخطط - ابتعد 5 كم",
        "severity": "low",
        "timestamp": "2026-05-22T14:30:00Z",
        "actions": ["View Route", "Contact Driver"]
      }
    ]
  }
}
```

---

## 📈 التقارير اليومية (Daily Reports)

```json
{
  "dailyReport": {
    "date": "2026-05-22",
    "summary": {
      "totalTrips": 45,
      "completedTrips": 43,
      "ongoingTrips": 2,
      "cancelledTrips": 0,
      "totalDistance": 4850,
      "totalTime": 185.5,
      "avgTripTime": 4.12,
      "avgRating": 4.72,
      "totalEarnings": 15850,
      "totalExpenses": 3200,
      "netProfit": 12650
    },
    "topDrivers": [
      {
        "rank": 1,
        "driver": "سارة خالد",
        "trips": 6,
        "earnings": 1800,
        "rating": 4.95
      },
      {
        "rank": 2,
        "driver": "أحمد محمد",
        "trips": 5,
        "earnings": 1500,
        "rating": 4.88
      }
    ],
    "incidents": [
      {
        "type": "delay",
        "count": 2,
        "drivers": ["سارة خالد", "محمد علي"]
      },
      {
        "type": "vehicle_issue",
        "count": 1,
        "details": "Low fuel - ABC-1234"
      }
    ]
  }
}
```

---

## ✅ ملخص البيانات المُرسلة (Data Summary)

**يتلقى المشرف بشكل حي:**
- ✅ موقع كل سائق (GPS دقيق)
- ✅ حالة كل رحلة
- ✅ الوقت المتوقع للوصول (ETA)
- ✅ المسافة المتبقية
- ✅ سرعة السائق الحالية
- ✅ مستوى الوقود
- ✅ حالة المركبة
- ✅ الإحصائيات اليومية
- ✅ الصور والتوقيعات
- ✅ التقييمات والملاحظات
- ✅ تقارير الحوادث
- ✅ التنبيهات والمشاكل

**كل البيانات محدثة كل 5 ثواني على الخريطة الحية 🗺️**

**النظام جاهز بنسبة 100% للاستخدام الفعلي ✅**
