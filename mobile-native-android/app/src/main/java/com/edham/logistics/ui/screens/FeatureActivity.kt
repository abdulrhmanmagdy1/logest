package com.edham.logistics.ui.screens

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.edham.logistics.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

/**
 * Catch-all "feature" screen used by all drawer menu items that do not
 * have a dedicated activity yet. Receives a [FeatureKind] via intent
 * extras and renders the matching mock content.
 *
 * This single class is intentionally chunky: it lets us ship every
 * drawer destination in Phase 2 without creating ~20 nearly-identical
 * activities. Each section is straightforward to extract into its own
 * file once it grows real logic in Phase 3.
 */
class FeatureActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var content: LinearLayout
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature)

        toolbar = findViewById(R.id.featureToolbar)
        content = findViewById(R.id.featureContent)

        val rawKind = intent.getStringExtra(EXTRA_KIND).orEmpty()
        val kind = runCatching { FeatureKind.valueOf(rawKind) }.getOrDefault(FeatureKind.SETTINGS)

        toolbar.title = getString(kind.titleRes)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        render(kind)
    }

    private fun render(kind: FeatureKind) {
        when (kind) {
            // ----- Shared lists / catalogs ------------------------------------
            FeatureKind.MY_SHIPMENTS,
            FeatureKind.ALL_SHIPMENTS  -> renderShipmentsList()
            FeatureKind.TRACK          -> renderTracking()
            FeatureKind.NEW_SHIPMENT   -> { startActivity(Intent(this, NewShipmentActivity::class.java)); finish() }

            FeatureKind.OFFERS         -> renderOffers()
            FeatureKind.INVOICES       -> renderInvoices(customer = true)
            FeatureKind.INVOICES_ACC   -> renderInvoices(customer = false)
            FeatureKind.PAYMENTS       -> renderPayments()
            FeatureKind.REPORTS        -> renderReports()

            FeatureKind.DRIVERS        -> renderDriversList()
            FeatureKind.FLEET          -> renderFleet()
            FeatureKind.DISPATCH       -> renderDispatch()
            FeatureKind.CLIENTS        -> renderClients()
            FeatureKind.ROUTES         -> renderRoutes()

            FeatureKind.MY_ROUTE       -> renderMyRoute()
            FeatureKind.MY_LOADS       -> renderMyLoads()

            FeatureKind.CHAT           -> renderChat()
            FeatureKind.NOTIFICATIONS  -> renderNotifications()
            FeatureKind.SETTINGS       -> renderSettings()
            FeatureKind.SUPPORT        -> renderSupport()
        }
    }

    // ============================================================ Renderers

    private fun renderShipmentsList() {
        addStatusFilterChips(listOf("الكل", "قيد التنفيذ", "بانتظار الموافقة", "تم التسليم", "ملغية"))
        addShipmentCard("#DLV1042", "الرياض ← الدمام", "نقل مبرد • 2 طن", "قيد التنفيذ", BRAND_BLUE)
        addShipmentCard("#DLV1041", "جدة ← المدينة",   "شحن سريع • 800 كجم", "قيد التنفيذ", BRAND_BLUE)
        addShipmentCard("#DLV1040", "الدمام ← الرياض", "ثقيل • 5 طن", "بانتظار الموافقة", "#F59E0B")
        addShipmentCard("#DLV1029", "الرياض ← جدة",    "نقل مبرد • 1.5 طن", "تم التسليم", "#10B981")
        addShipmentCard("#DLV1027", "الدمام ← الرياض", "شحن سريع • 600 كجم", "تم التسليم", "#10B981")
        addShipmentCard("#DLV1024", "جدة ← المدينة",   "نقل عادي • 3 طن", "ملغية", "#EF4444")
    }

    private fun renderTracking() {
        startActivity(Intent(this, TrackShipmentActivity::class.java))
        finish()
    }

    private fun renderOffers() {
        addBigOffer("خصم 20% على الشحن المبرد", "صالح حتى 30 يونيو على شحنات الأغذية والأدوية", "20%")
        addBigOffer("شحن سريع 24 ساعة", "بين الرياض وجدة والدمام بأسعار خاصة", "24h")
        addBigOffer("اشحن 5 احصل على 1 مجاناً", "للعملاء المسجلين أكثر من 6 أشهر", "+1")
        addBigOffer("شحن دولي بسعر الشحن المحلي", "خلال شهر يوليو فقط", "INT")
    }

    private fun renderInvoices(customer: Boolean) {
        if (!customer) addStatRow(listOf("مستحقات" to "37,200 ر.س", "متأخرات" to "8,950 ر.س"))
        addSectionTitle("غير مدفوعة")
        addInvoiceCard("INV-2026-0130", "12 يونيو 2026", "5,200 ر.س", "غير مدفوعة", "#F59E0B")
        addInvoiceCard("INV-2026-0129", "10 يونيو 2026", "3,400 ر.س", "غير مدفوعة", "#F59E0B")
        addSectionTitle("مدفوعة")
        addInvoiceCard("INV-2026-0128", "08 يونيو 2026", "7,800 ر.س", "مدفوعة", "#10B981")
        addInvoiceCard("INV-2026-0127", "05 يونيو 2026", "4,100 ر.س", "مدفوعة", "#10B981")
        addInvoiceCard("INV-2026-0125", "01 يونيو 2026", "12,400 ر.س", "مدفوعة", "#10B981")
    }

    private fun renderPayments() {
        addStatRow(listOf("مقبوض هذا الشهر" to "148,300 ر.س", "عدد العمليات" to "37"))
        addSectionTitle("آخر المدفوعات")
        addListItem("INV-2026-0125", "تحويل بنكي • شركة الأغذية", "12,400 ر.س")
        addListItem("INV-2026-0124", "بطاقة ائتمان • مؤسسة التبريد", "4,800 ر.س")
        addListItem("INV-2026-0123", "كاش • الخليج للتجارة", "3,150 ر.س")
        addListItem("INV-2026-0122", "تحويل بنكي • النقل السريع", "9,750 ر.س")
        addListItem("INV-2026-0121", "كاش • أسواق المدينة", "1,200 ر.س")
    }

    private fun renderReports() {
        addSectionTitle("تقارير متاحة للتنزيل")
        addListItem("تقرير الإيرادات الشهري",      "يونيو 2026",         "PDF")
        addListItem("تقرير العملاء النشطين",       "آخر 30 يوماً",       "PDF")
        addListItem("تقرير ضريبة القيمة المضافة",   "الربع الثاني 2026",   "PDF")
        addListItem("تقرير المصروفات التشغيلية",     "يونيو 2026",         "PDF")
        addListItem("تقرير أداء السائقين",          "يونيو 2026",         "PDF")
        addListItem("تقرير حالة الأسطول",           "تاريخ آخر فحص",      "PDF")
    }

    private fun renderDriversList() {
        addStatRow(listOf("سائقون نشطون" to "18", "في إجازة" to "2"))
        addPersonCard("أحمد محمد",  "TRK-001 • نشط في الرياض",   "4.9 ⭐")
        addPersonCard("خالد علي",   "TRK-002 • متوقف للصيانة",    "4.7 ⭐")
        addPersonCard("سعد القحطاني","TRK-003 • متاح في الدمام",  "4.8 ⭐")
        addPersonCard("عمر السالم",  "TRK-004 • في رحلة لجدة",    "4.6 ⭐")
        addPersonCard("فيصل الناصر", "TRK-005 • في إجازة",        "4.9 ⭐")
        addPersonCard("ماجد العتيبي","TRK-006 • متاح في المدينة",  "4.5 ⭐")
    }

    private fun renderFleet() {
        addStatRow(listOf("إجمالي الشاحنات" to "24", "في الخدمة" to "21"))
        addStatRow(listOf("صيانة" to "2", "متوقفة" to "1"))
        addSectionTitle("شاحنات مبردة")
        addListItem("TRK-001 • مرسيدس أكتروس", "السائق أحمد • الرياض",  "نشط")
        addListItem("TRK-002 • فولفو FH",       "صيانة دورية",            "صيانة")
        addListItem("TRK-005 • سكانيا R",        "متاحة في الدمام",        "متاح")
        addSectionTitle("شاحنات ثقيلة")
        addListItem("TRK-010 • مان TGS",         "السائق ماجد • جدة",     "نشط")
        addListItem("TRK-012 • داف XF",          "متاحة في الرياض",       "متاح")
    }

    private fun renderDispatch() {
        addSectionTitle("طلبات بانتظار الإسناد")
        addListItem("#DLV1042", "الرياض ← الدمام • مبرد • 2 طن",  "إسناد")
        addListItem("#DLV1041", "جدة ← المدينة • سريع • 800 كجم", "إسناد")
        addListItem("#DLV1040", "الدمام ← الرياض • ثقيل • 5 طن",  "إسناد")
        addListItem("#DLV1039", "الرياض ← المدينة • مبرد • 1 طن", "إسناد")
        addSectionTitle("مسندة حديثاً")
        addListItem("#DLV1038", "السائق أحمد • TRK-001",          "نشط")
        addListItem("#DLV1037", "السائق خالد • TRK-002",          "نشط")
    }

    private fun renderClients() {
        addStatRow(listOf("عملاء نشطون" to "126", "جدد هذا الشهر" to "9"))
        addPersonCard("شركة الأغذية الطازجة", "37 شحنة • متوسط 12,400 ر.س", "★★★★★")
        addPersonCard("مؤسسة التبريد المتقدم", "24 شحنة • متوسط 4,800 ر.س",  "★★★★☆")
        addPersonCard("الخليج للتجارة",        "19 شحنة • متوسط 7,200 ر.س",  "★★★★☆")
        addPersonCard("النقل السريع المحدودة",  "15 شحنة • متوسط 9,750 ر.س",  "★★★★★")
        addPersonCard("أسواق المدينة",          "11 شحنة • متوسط 3,200 ر.س",  "★★★★☆")
    }

    private fun renderRoutes() {
        addSectionTitle("أكثر المسارات استخداماً")
        addListItem("الرياض ← جدة",     "983 كم • متوسط 11 ساعة",  "نشط")
        addListItem("الرياض ← الدمام",   "395 كم • متوسط 4 ساعات",   "نشط")
        addListItem("جدة ← المدينة",    "420 كم • متوسط 4.5 ساعة",  "نشط")
        addListItem("الدمام ← الخبر",   "20 كم • متوسط 25 دقيقة",   "نشط")
        addListItem("الرياض ← القصيم",  "317 كم • متوسط 3.5 ساعة",  "نشط")
        addListItem("جدة ← الطائف",     "167 كم • متوسط 2 ساعة",    "نشط")
    }

    private fun renderMyRoute() {
        addMapView { map ->
            // Mock route: Riyadh to Jeddah for driver
            val riyadh = LatLng(24.7136, 46.6753)
            val jeddah = LatLng(21.5433, 39.6780)
            val currentLocation = LatLng(23.0, 44.0)

            map.clear()
            map.addMarker(MarkerOptions()
                .position(riyadh)
                .title("مستودع الرياض - نقطة الانطلاق")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            map.addMarker(MarkerOptions()
                .position(jeddah)
                .title("مستودع جدة - الوجهة")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            map.addMarker(MarkerOptions()
                .position(currentLocation)
                .title("الموقع الحالي")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

            map.addPolyline(PolylineOptions()
                .add(riyadh, currentLocation, jeddah)
                .width(8f)
                .color(Color.parseColor("#7BBDE8"))
                .geodesic(true))

            val bounds = LatLngBounds.Builder()
                .include(riyadh)
                .include(currentLocation)
                .include(jeddah)
                .build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
        addSectionTitle("التفاصيل")
        addListItem("الانطلاق",  "مستودع الرياض • 8:00 ص",       "تم")
        addListItem("استراحة",   "محطة وقود الدوادمي • 11:30 ص",  "تمت")
        addListItem("الوصول",    "مستودع جدة • متوقع 4:30 م",    "قادم")
    }

    private fun renderMyLoads() {
        addSectionTitle("اليوم")
        addShipmentCard("#DLV1042", "الرياض ← الدمام", "مبرد • 2 طن", "قيد التنفيذ", BRAND_BLUE)
        addSectionTitle("غداً")
        addShipmentCard("#DLV1043", "الدمام ← الخبر",   "مبرد • 1.5 طن", "مجدولة", "#F59E0B")
        addShipmentCard("#DLV1044", "الخبر ← الدمام",   "مبرد • 1 طن",   "مجدولة", "#F59E0B")
        addSectionTitle("الأسبوع القادم")
        addShipmentCard("#DLV1050", "الرياض ← القصيم",  "ثقيل • 4 طن",   "مجدولة", "#F59E0B")
    }

    private fun renderChat() {
        addPersonCard("الدعم الفني",          "كيف يمكننا مساعدتك اليوم؟",     "الآن")
        addPersonCard("المشرف أحمد",          "الشحنة #DLV1042 جاهزة للتحميل", "10:24")
        addPersonCard("السائق خالد",          "وصلت لمستودع جدة",              "أمس")
        addPersonCard("المحاسبة سارة",         "الفاتورة جاهزة للمراجعة",       "أمس")
        addPersonCard("شركة الأغذية الطازجة",  "نريد جدولة شحنة جديدة",         "السبت")
    }

    private fun renderNotifications() {
        addNotificationItem("✅", "تم تأكيد شحنتك",      "#DLV1042 تم قبولها وستنطلق غداً 8 ص", "الآن")
        addNotificationItem("🚛", "السائق في الطريق",     "أحمد بدأ رحلة #DLV1041 من جدة",        "قبل 10د")
        addNotificationItem("💰", "فاتورة جديدة",         "INV-2026-0130 بقيمة 5,200 ر.س",       "قبل ساعة")
        addNotificationItem("⚠️", "تنبيه حرارة",          "الشحنة #DLV1029 تجاوزت -15°C",         "قبل ساعتين")
        addNotificationItem("⭐", "تقييم جديد",            "حصل أحمد على 5 نجوم من شركة الأغذية",  "أمس")
        addNotificationItem("📄", "وثيقة منتهية",          "رخصة السائق خالد ستنتهي خلال 7 أيام",  "أمس")
    }

    private fun renderSettings() {
        addSectionTitle("الحساب")
        addListItem("تعديل الملف الشخصي",  "الاسم، الإيميل، الهاتف",  null)
        addListItem("تغيير كلمة المرور",   "آخر تحديث منذ 30 يوماً",  null)
        addListItem("التحقق بخطوتين",      "غير مفعل",                "تفعيل")
        addSectionTitle("التطبيق")
        addListItem("اللغة",              "العربية",                  "تغيير")
        addListItem("المظهر",             "داكن (افتراضي)",            "تغيير")
        addListItem("الإشعارات",          "مفعّل",                     "إيقاف")
        addSectionTitle("الخصوصية")
        addListItem("صلاحيات الموقع",      "أثناء الاستخدام",          "إدارة")
        addListItem("صلاحيات الكاميرا",    "غير مفعّل",                 "إدارة")
        addSectionTitle("حول")
        addListItem("شروط الاستخدام",      "آخر تحديث 2026/05/01",     null)
        addListItem("سياسة الخصوصية",       "آخر تحديث 2026/05/01",     null)
        addListItem("إصدار التطبيق",        "v1.0.0",                    null)
    }

    private fun renderSupport() {
        addBigOffer("تواصل معنا", "خدمة عملاء 24/7 على 920000000", "📞")
        addBigOffer("البريد الإلكتروني", "support@edham.com — الرد خلال 24 ساعة", "✉️")
        addSectionTitle("الأسئلة الشائعة")
        addListItem("كيف أحجز شحنة؟",         "خطوات الحجز والمواعيد",  null)
        addListItem("كيف أتتبع شحنتي؟",       "استخدم رقم التتبع",       null)
        addListItem("متى تصل الفاتورة؟",       "بعد التسليم بـ 24 ساعة",  null)
        addListItem("سياسة الإلغاء والاسترداد","قبل التحميل بدون رسوم",   null)
        addListItem("الشحنات الدولية",         "قائمة الدول والأسعار",   null)
    }

    // ============================================================ Builders

    private fun addStatusFilterChips(items: List<String>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        }
        items.forEachIndexed { i, label ->
            val chip = TextView(this).apply {
                text = label
                textSize = 12f
                setTextColor(if (i == 0) Color.parseColor("#001D39") else Color.WHITE)
                setTypeface(typeface, Typeface.BOLD)
                background = GradientDrawable().apply {
                    cornerRadius = dp(20).toFloat()
                    setColor(if (i == 0)
                        ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary)
                    else Color.parseColor("#062E54"))
                }
                setPadding(dp(14), dp(8), dp(14), dp(8))
            }
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = dp(8) }
            chip.layoutParams = lp
            row.addView(chip)
        }
        content.addView(android.widget.HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(row)
        })
    }

    private fun addSectionTitle(title: String) {
        content.addView(TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 17f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(18), 0, dp(8))
        })
    }

    private fun addStatRow(items: List<Pair<String, String>>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }
        items.forEach { (title, value) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = cardBackground()
                setPadding(dp(16), dp(16), dp(16), dp(16))
            }
            card.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(dp(4), 0, dp(4), 0)
            }
            card.addView(TextView(this).apply {
                this.text = title
                setTextColor(Color.parseColor("#A0FFFFFF"))
                textSize = 12f
            })
            card.addView(TextView(this).apply {
                this.text = value
                setTextColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, dp(6), 0, 0)
            })
            row.addView(card)
        }
        content.addView(row)
    }

    private fun addShipmentCard(
        id: String, route: String, info: String, status: String, accent: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        top.addView(TextView(this).apply {
            text = id
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        top.addView(statusBadge(status, accent))
        card.addView(top)
        card.addView(TextView(this).apply {
            text = route
            setTextColor(Color.parseColor("#D0FFFFFF"))
            textSize = 13f
            setPadding(0, dp(6), 0, 0)
        })
        card.addView(TextView(this).apply {
            text = info
            setTextColor(Color.parseColor("#80FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })
        content.addView(card)
    }

    private fun addInvoiceCard(
        id: String, date: String, amount: String, status: String, accent: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = cardBackground()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(TextView(this).apply {
            text = id
            setTextColor(Color.WHITE)
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
        })
        col.addView(TextView(this).apply {
            text = date
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            setPadding(0, dp(4), 0, 0)
        })
        card.addView(col)

        val right = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }
        right.addView(TextView(this).apply {
            text = amount
            setTextColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.END
        })
        right.addView(statusBadge(status, accent).apply {
            (layoutParams as? LinearLayout.LayoutParams)?.topMargin = dp(4)
        })
        card.addView(right)
        content.addView(card)
    }

    private fun addListItem(title: String, subtitle: String, trailing: String?) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = cardBackground()
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        })
        col.addView(TextView(this).apply {
            text = subtitle
            setTextColor(Color.parseColor("#99FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })
        container.addView(col)
        if (trailing != null) {
            container.addView(TextView(this).apply {
                text = trailing
                setTextColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
                textSize = 13f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            })
        }
        content.addView(container)
    }

    private fun addPersonCard(name: String, subtitle: String, trailing: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = cardBackground()
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
            gravity = Gravity.CENTER_VERTICAL
        }
        val avatar = TextView(this).apply {
            text = name.firstOrNull()?.toString() ?: "?"
            setTextColor(Color.parseColor("#001D39"))
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
            }
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40)).apply { marginEnd = dp(12) }
        }
        card.addView(avatar)

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(TextView(this).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        })
        col.addView(TextView(this).apply {
            text = subtitle
            setTextColor(Color.parseColor("#99FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })
        card.addView(col)

        card.addView(TextView(this).apply {
            text = trailing
            setTextColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
            textSize = 12f
            setTypeface(typeface, Typeface.BOLD)
        })
        content.addView(card)
    }

    private fun addBigOffer(title: String, subtitle: String, badge: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                colors = intArrayOf(
                    Color.parseColor("#7BBDE8"),
                    Color.parseColor("#0A4174")
                )
                orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(typeface, Typeface.BOLD)
        })
        col.addView(TextView(this).apply {
            text = subtitle
            setTextColor(Color.parseColor("#E6FFFFFF"))
            textSize = 12f
            setPadding(0, dp(4), 0, 0)
        })
        card.addView(col)

        card.addView(TextView(this).apply {
            text = badge
            setTextColor(Color.parseColor("#001D39"))
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = GradientDrawable().apply {
                cornerRadius = dp(14).toFloat()
                setColor(Color.WHITE)
            }
        })
        content.addView(card)
    }

    private fun addNotificationItem(icon: String, title: String, body: String, time: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = cardBackground()
            setPadding(dp(14), dp(12), dp(14), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(10) }
        }
        card.addView(TextView(this).apply {
            text = icon
            textSize = 22f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply { marginEnd = dp(10) }
        })
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        col.addView(TextView(this).apply {
            text = title
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
        })
        col.addView(TextView(this).apply {
            text = body
            setTextColor(Color.parseColor("#A0FFFFFF"))
            textSize = 12f
            setPadding(0, dp(2), 0, 0)
        })
        card.addView(col)
        card.addView(TextView(this).apply {
            text = time
            setTextColor(ContextCompat.getColor(this@FeatureActivity, R.color.brand_primary))
            textSize = 11f
        })
        content.addView(card)
    }

    private fun addMapView(onReady: (GoogleMap) -> Unit) {
        val containerId = View.generateViewId()
        val frame = FrameLayout(this).apply {
            id = containerId
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(220)
            ).apply { topMargin = dp(10) }
            background = GradientDrawable().apply {
                cornerRadius = dp(14).toFloat()
                setColor(Color.parseColor("#0A2A4E"))
            }
        }
        content.addView(frame)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(containerId, mapFragment)
            .commit()

        mapFragment.getMapAsync { map ->
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL)
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isCompassEnabled = true
            onReady(map)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        // Handled inside addMapView lambda
    }

    private fun statusBadge(text: String, hex: String): TextView = TextView(this).apply {
        this.text = text
        setTextColor(Color.parseColor(hex))
        textSize = 11f
        setTypeface(typeface, Typeface.BOLD)
        background = GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()
            setColor(Color.parseColor(hex))
            alpha = 38
        }
        setPadding(dp(10), dp(4), dp(10), dp(4))
    }

    private fun cardBackground(): GradientDrawable = GradientDrawable().apply {
        cornerRadius = dp(14).toFloat()
        setColor(Color.parseColor("#062E54"))
        setStroke(dp(1), Color.parseColor("#22FFFFFF"))
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()

    // ============================================================ API

    enum class FeatureKind(val titleRes: Int) {
        MY_SHIPMENTS  (R.string.menu_shipments),
        ALL_SHIPMENTS (R.string.menu_shipments),
        NEW_SHIPMENT  (R.string.menu_new_shipment),
        TRACK         (R.string.menu_track),
        OFFERS        (R.string.menu_offers),
        INVOICES      (R.string.menu_invoices),
        INVOICES_ACC  (R.string.menu_invoices),
        PAYMENTS      (R.string.menu_payments),
        REPORTS       (R.string.menu_reports),
        DRIVERS       (R.string.menu_drivers),
        FLEET         (R.string.menu_fleet),
        DISPATCH      (R.string.menu_dispatch),
        CLIENTS       (R.string.menu_clients),
        ROUTES        (R.string.menu_routes_path),
        MY_ROUTE      (R.string.menu_my_route),
        MY_LOADS      (R.string.menu_my_loads),
        CHAT          (R.string.menu_chat),
        NOTIFICATIONS (R.string.menu_notifications),
        SETTINGS      (com.edham.logistics.R.string.menu_settings),
        SUPPORT       (R.string.menu_support),
    }

    companion object {
        const val EXTRA_KIND = "extra_feature_kind"
        private const val BRAND_BLUE = "#7BBDE8"

        fun intent(ctx: android.content.Context, kind: FeatureKind): Intent =
            Intent(ctx, FeatureActivity::class.java).putExtra(EXTRA_KIND, kind.name)
    }
}
