# 🎯 خطة التطوير - الميزات الناقصة (نسخة عملية)

## 📌 الأولويات المرتبة

### **🔴 أولوية قصوى (يجب إكمالها أولاً)**

| الترتيب | الميزة | الأولوية | الوقت | التأثير |
|--------|--------|---------|------|--------|
| 1️⃣ | **Payment System** | حرج | 2 أسبوع | 🔥🔥🔥 تطبيق غير قابل للاستخدام بدونها |
| 2️⃣ | **GPS Backend + WebSocket** | حرج | 3 أسابيع | 🔥🔥🔥 العمود الفقري للنظام |
| 3️⃣ | **Firebase FCM** | حرج | 1 أسبوع | 🔥🔥 بدون إشعارات = تطبيق ميت |
| 4️⃣ | **Cold Chain Logic** | عالي | 2 أسبوع | 🔥🔥 الميزة الأساسية للتطبيق |
| 5️⃣ | **Offline Sync** | عالي | 2 أسبوع | 🔥 ضروري للعمل بدون إنترنت |

---

## 🛠️ تفاصيل كل ميزة

### **1️⃣ Payment System** ⏱️ 2 أسبوع

#### ✅ ما الذي يجب فعله:

```
الأسبوع 1:
├─ أ) تثبيت مكتبات Stripe
│  └─ npm install stripe @stripe/stripe-js
│
├─ ب) إنشاء Payment Model في MongoDB
│  ├─ invoiceId, amount, currency, status
│  ├─ paymentMethod, transactionId
│  └─ metadata, timestamps
│
├─ ج) إضافة API Endpoints
│  ├─ POST /api/v1/payments/create-intent
│  ├─ POST /api/v1/payments/confirm
│  ├─ GET /api/v1/payments/status/:id
│  ├─ POST /api/v1/payments/refund
│  └─ POST /api/v1/payments/webhook (Stripe)
│
└─ د) إعداد Stripe Webhook
   ├─ payment_intent.succeeded
   ├─ payment_intent.payment_failed
   └─ charge.refunded

الأسبوع 2:
├─ أ) Invoice Generation (PDF)
│  ├─ npm install pdfkit
│  ├─ POST /api/v1/invoices/generate
│  └─ Email invoice to customer
│
├─ ب) تحديث Android Frontend
│  ├─ Payment Screen UI
│  ├─ Card entry form
│  └─ Payment status tracking
│
├─ ج) Payment History
│  ├─ GET /api/v1/payments/history/:customerId
│  └─ Download invoice (PDF)
│
└─ د) Tests & QA
   ├─ Unit tests for payment logic
   ├─ Integration tests with Stripe
   └─ Security review
```

#### 🔑 المفاتيح التقنية:

```javascript
// Backend: Payment Processing
const createPaymentIntent = async (req, res) => {
    const { amount, customerId, invoiceId } = req.body;
    
    try {
        const paymentIntent = await stripe.paymentIntents.create({
            amount: amount * 100, // Convert to cents
            currency: 'sar',
            customer: customerId,
            metadata: { invoiceId },
            description: `Invoice #${invoiceId}`
        });
        
        // Save to DB
        const payment = new Payment({
            invoiceId,
            intentId: paymentIntent.id,
            amount,
            status: 'PENDING',
            customerId
        });
        await payment.save();
        
        res.json({ clientSecret: paymentIntent.client_secret });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// Webhook Handler
app.post('/webhook', express.raw({ type: 'application/json' }), async (req, res) => {
    const sig = req.headers['stripe-signature'];
    let event;
    
    try {
        event = stripe.webhooks.constructEvent(req.body, sig, process.env.STRIPE_WEBHOOK_SECRET);
    } catch (err) {
        return res.status(400).send(`Webhook Error: ${err.message}`);
    }
    
    switch (event.type) {
        case 'payment_intent.succeeded':
            const payment = await Payment.findOneAndUpdate(
                { intentId: event.data.object.id },
                { status: 'COMPLETED' }
            );
            // Update invoice status
            await Invoice.findByIdAndUpdate(payment.invoiceId, { status: 'PAID' });
            break;
    }
    
    res.json({ received: true });
});
```

---

### **2️⃣ GPS Tracking Backend** ⏱️ 3 أسابيع

#### ✅ ما الذي يجب فعله:

```
الأسبوع 1:
├─ أ) Location Model في MongoDB
│  ├─ driverId, shipmentId, coordinates
│  ├─ accuracy, speed, bearing, altitude
│  ├─ timestamp, source (GPS/Network)
│  └─ indexes on driverId, shipmentId, timestamp
│
├─ ب) API Endpoints الأساسية
│  ├─ POST /api/v1/locations/update (single)
│  ├─ POST /api/v1/locations/batch (multiple)
│  ├─ GET /api/v1/locations/current/:driverId
│  └─ GET /api/v1/locations/:shipmentId/history
│
├─ ج) Database Optimization
│  ├─ Time-series optimization
│  ├─ Geospatial indexes
│  └─ TTL for old records (30 days)
│
└─ د) Location Validation
   ├─ Check if within reasonable bounds
   ├─ Detect teleportation (impossible speed)
   └─ Quality score calculation

الأسبوع 2:
├─ أ) Route Optimization
│  ├─ Algorithm: Dijkstra's / A*
│  ├─ POST /api/v1/routes/optimize
│  └─ Consider traffic, distance, time
│
├─ ب) Geofencing
│  ├─ Define zones (pickup, delivery)
│  ├─ Arrival/departure detection
│  ├─ Socket events: 'geofence:entered', 'geofence:exited'
│  └─ Automatic status updates
│
├─ ج) Historical Analysis
│  ├─ GET /api/v1/analytics/route-efficiency
│  ├─ Calculate: distance, time, stops
│  └─ Driver performance metrics
│
└─ د) WebSocket Integration
   ├─ Real-time location streaming
   ├─ Location updates every 30 seconds
   └─ Supervisor live map feed

الأسبوع 3:
├─ أ) Android App Integration
│  ├─ Foreground Service for location
│  ├─ Adaptive update frequency
│  └─ Battery optimization
│
├─ ب) Frontend Map Updates
│  ├─ Animated marker movement
│  ├─ Polyline route drawing
│  └─ ETA calculation
│
├─ ج) Testing & QA
│  ├─ Load testing (1000s of concurrent updates)
│  ├─ Battery consumption tests
│  └─ Accuracy testing
│
└─ د) Deployment
   ├─ Database migration
   ├─ Monitor performance
   └─ Gradual rollout
```

#### 🔑 المفاتيح التقنية:

```javascript
// Backend: Location Storage
app.post('/api/v1/locations/batch', async (req, res) => {
    const { locations } = req.body; // Array of locations
    
    const enrichedLocations = locations.map(loc => ({
        ...loc,
        timestamp: new Date(),
        quality: calculateQuality(loc),
        source: 'gps'
    }));
    
    await Location.insertMany(enrichedLocations);
    
    // Emit WebSocket event for supervisors
    for (const loc of enrichedLocations) {
        io.to(`shipment:${loc.shipmentId}`).emit('location:updated', loc);
    }
    
    res.json({ success: true, count: locations.length });
});

// Geofencing
const checkGeofence = (location, geofence) => {
    const distance = haversineDistance(location, geofence.center);
    return distance <= geofence.radius;
};

app.post('/api/v1/locations/check-geofence', async (req, res) => {
    const { driverId, shipmentId, coordinates } = req.body;
    
    const shipment = await Shipment.findById(shipmentId);
    const previousLocation = await Location.findOne({ shipmentId }).sort({ timestamp: -1 });
    
    // Check if entered delivery zone
    if (checkGeofence(coordinates, shipment.deliveryLocation)) {
        io.to(`shipment:${shipmentId}`).emit('geofence:entered', {
            type: 'delivery',
            location: coordinates,
            timestamp: new Date()
        });
        
        // Auto-update status
        await Shipment.findByIdAndUpdate(shipmentId, { status: 'AT_DELIVERY_LOCATION' });
    }
    
    res.json({ success: true });
});

// Route Optimization (using Google Maps API)
const optimizeRoute = async (stops) => {
    const response = await axios.post('https://maps.googleapis.com/maps/api/directions/json', {
        origin: stops[0],
        destination: stops[stops.length - 1],
        waypoints: stops.slice(1, -1),
        optimizeWaypoints: true,
        key: process.env.GOOGLE_MAPS_API_KEY
    });
    
    return response.data.routes[0];
};
```

```kotlin
// Android: Foreground Service
class LocationService : Service() {
    private lateinit var locationClient: FusedLocationProviderClient
    
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        requestLocationUpdates()
        return START_STICKY
    }
    
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
            .build()
        
        locationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    sendLocationToServer(location)
                }
            },
            Looper.getMainLooper()
        )
    }
    
    private fun sendLocationToServer(location: Location) {
        val retrofitClient = RetrofitClient.getInstance()
        val call = retrofitClient.updateLocation(
            LocationUpdate(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                speed = location.speed,
                bearing = location.bearing
            )
        )
        
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                // Success
            }
            
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Retry later
            }
        })
    }
}
```

---

### **3️⃣ Firebase FCM Setup** ⏱️ 1 أسبوع

#### ✅ ما الذي يجب فعله:

```
الأسبوع 1:
├─ أ) Android Firebase Setup
│  ├─ Add Firebase to gradle
│  ├─ Add google-services.json
│  ├─ Create MyFirebaseMessagingService
│  └─ Request notification permissions
│
├─ ب) Notification Channels
│  ├─ Shipment Updates
│  ├─ Payment Notifications
│  ├─ Support Messages
│  └─ System Alerts
│
├─ ج) Backend FCM Integration
│  ├─ npm install firebase-admin
│  ├─ Store FCM tokens in database
│  ├─ POST /api/v1/notifications/send
│  └─ Batch sending for efficiency
│
├─ د) Event Triggers
│  ├─ Shipment created → Notification
│  ├─ Shipment status changed → Notification
│  ├─ Payment received → Notification
│  ├─ Driver assigned → Notification
│  └─ Delivery confirmed → Notification
│
└─ هـ) Testing & Monitoring
   ├─ Test with Firebase console
   ├─ Monitor delivery rate
   └─ Track user engagement
```

#### 🔑 المفاتيح التقنية:

```kotlin
// Android: Firebase Service
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToServer(token)
    }
    
    private fun sendTokenToServer(token: String) {
        val apiService = RetrofitClient.getInstance()
        val call = apiService.registerFCMToken(FCMTokenRequest(token))
        
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Log.d("FCM", "Token registered successfully")
            }
            
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("FCM", "Failed to register token", t)
            }
        })
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val notification = remoteMessage.notification
        val data = remoteMessage.data
        
        val notificationId = data["notification_id"]?.toIntOrNull() ?: 1
        
        showNotification(
            title = notification?.title ?: "إشعار من إدهام",
            message = notification?.body ?: "",
            data = data,
            id = notificationId
        )
    }
    
    private fun showNotification(title: String, message: String, data: Map<String, String>, id: Int) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notification_type", data["type"])
            putExtra("notification_id", data["id"])
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, "shipment_updates")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        NotificationManagerCompat.from(this).notify(id, notification)
    }
}

// Notification Channel Setup
private fun createNotificationChannels() {
    val channels = listOf(
        NotificationChannel(
            "shipment_updates",
            "تحديثات الشحنات",
            NotificationManager.IMPORTANCE_HIGH
        ),
        NotificationChannel(
            "payment_notifications",
            "إشعارات الدفع",
            NotificationManager.IMPORTANCE_HIGH
        ),
        NotificationChannel(
            "support_messages",
            "رسائل الدعم",
            NotificationManager.IMPORTANCE_DEFAULT
        ),
        NotificationChannel(
            "system_alerts",
            "تنبيهات النظام",
            NotificationManager.IMPORTANCE_LOW
        )
    )
    
    val manager = getSystemService(NotificationManager::class.java)
    manager?.createNotificationChannels(channels)
}
```

```javascript
// Backend: Send Notifications
const sendNotification = async (userId, title, body, data = {}) => {
    const user = await User.findById(userId);
    if (!user || !user.fcmToken) return;
    
    try {
        await admin.messaging().send({
            token: user.fcmToken,
            notification: {
                title,
                body
            },
            data: {
                ...data,
                timestamp: new Date().toISOString()
            },
            android: {
                priority: 'high',
                notification: {
                    channelId: data.channelId || 'shipment_updates'
                }
            }
        });
    } catch (error) {
        console.error('FCM Error:', error);
    }
};

// Event Triggers
const shipmentController = {
    createShipment: async (req, res) => {
        const shipment = new Shipment(req.body);
        await shipment.save();
        
        // Notify customer
        await sendNotification(
            shipment.customerId,
            'تم إنشاء شحنتك',
            `تم إنشاء شحنة جديدة برقم ${shipment._id}`,
            {
                channelId: 'shipment_updates',
                type: 'shipment_created',
                shipmentId: shipment._id
            }
        );
        
        res.json(shipment);
    },
    
    updateStatus: async (req, res) => {
        const { shipmentId } = req.params;
        const { status } = req.body;
        
        const shipment = await Shipment.findByIdAndUpdate(
            shipmentId,
            { status },
            { new: true }
        );
        
        // Notify all interested parties
        const statusMessages = {
            'PICKED_UP': 'تم استلام الشحنة',
            'IN_TRANSIT': 'الشحنة في الطريق',
            'DELIVERED': 'تم تسليم الشحنة',
            'FAILED': 'فشل التسليم'
        };
        
        await sendNotification(
            shipment.customerId,
            'تحديث حالة الشحنة',
            statusMessages[status] || `الحالة: ${status}`,
            { type: 'status_update', shipmentId }
        );
        
        res.json(shipment);
    }
};
```

---

## 📊 ملخص الجدول الزمني

```
الشهر 1 (الأسابيع 1-4):
├─ أسبوع 1: Payment System
├─ أسبوع 2: Firebase FCM
├─ أسبوع 3-4: GPS Backend + WebSocket

الشهر 2 (الأسابيع 5-8):
├─ أسبوع 5-6: Cold Chain Monitoring
├─ أسبوع 7-8: Offline Sync

الشهر 3 (الأسابيع 9-12):
├─ أسبوع 9-10: Advanced Analytics
├─ أسبوع 11-12: Performance Optimization

الشهر 4 (الأسابيع 13-16):
├─ أسبوع 13-14: Support System
├─ أسبوع 15-16: Polish & Testing
```

---

## 🔧 الأدوات والمكتبات المطلوبة

```bash
# Backend npm packages
npm install stripe @stripe/stripe-js
npm install firebase-admin
npm install pdfkit
npm install socket.io
npm install mongoose-timeseries
npm install joi # Validation
npm install express-rate-limit
npm install axios

# Android Gradle dependencies
// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Firebase
implementation 'com.google.firebase:firebase-messaging:23.2.1'
implementation 'com.google.firebase:firebase-analytics:21.2.0'
implementation 'com.google.firebase:firebase-crashlytics:18.3.0'

// Location
implementation 'com.google.android.gms:play-services-location:21.0.1'
implementation 'com.google.android.gms:play-services-maps:18.2.0'

// DataStore (Offline sync)
implementation 'androidx.datastore:datastore-preferences:1.0.0'
```

---

## ✅ معايير النجاح لكل ميزة

```
Payment System:
├─ ✅ جميع أنواع الدفع تعمل (Card, Bank Transfer, Wallet)
├─ ✅ الفواتير تُنشأ تلقائياً وتُرسل عبر الإيميل
├─ ✅ معدل نجاح الدفع > 99%
└─ ✅ PCI Compliance معتمد

GPS Tracking:
├─ ✅ تحديثات الموقع كل 30 ثانية
├─ ✅ دقة ± 5 متر
├─ ✅ تأخير البث < 1 ثانية
└─ ✅ استهلاك البطارية < 2%/ساعة

Firebase:
├─ ✅ معدل وصول الإشعارات > 98%
├─ ✅ وقت الوصول < 5 ثواني
├─ ✅ عدم وجود فقدان للتذاكر
└─ ✅ جميع الفئات تستقبل الإشعارات

Cold Chain:
├─ ✅ تنبيهات فورية عند تجاوز الحدود
├─ ✅ التقارير توضح نسبة الامتثال
├─ ✅ إمكانية تحميل التقارير PDF
└─ ✅ تنبؤ دقيق بتجاوز الحدود
```
