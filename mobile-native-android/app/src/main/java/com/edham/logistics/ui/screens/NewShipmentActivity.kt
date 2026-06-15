package com.edham.logistics.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.core.di.ServiceLocator
import com.edham.logistics.core.network.api.CreateShipmentRequest
import com.edham.logistics.core.network.api.ShipmentApi
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Four-step new-shipment wizard for customers:
 *   1) Category    (6 cargo type cards)
 *   2) Addresses   (pickup, drop-off, preferred date)
 *   3) Cargo       (description, weight, optional notes)
 *   4) Review      (summary card before placing the order)
 */
class NewShipmentActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var flipper: ViewFlipper
    private lateinit var progress: ProgressBar
    private lateinit var stepIndicator: TextView
    private lateinit var btnPrimary: MaterialButton

    private var selectedCategoryId: Int = -1
    private var pickupCityStr: String = ""
    private var pickupAddress: String = ""
    private var dropCityStr: String = ""
    private var dropAddress: String = ""
    private var pickupDateStr: String = ""
    private var pickupTimeStr: String = ""
    private var recipientNameStr: String = ""
    private var recipientPhoneStr: String = ""
    private var cargoDescription: String = ""
    private var cargoWeightValue: Double = 0.0
    private var pieceCountValue: Int = 0
    private var vehicleTypeStr: String = "small"
    private var cargoNotesText: String = ""
    private var estimatedPrice: Double = 0.0

    // Photos & map coordinates
    private val photoUris = mutableListOf<String>()
    private var pickupLat: Double? = null
    private var pickupLng: Double? = null
    private var dropLat: Double? = null
    private var dropLng: Double? = null

    private val shipmentApi: ShipmentApi = ServiceLocator.api<ShipmentApi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_shipment)

        toolbar         = findViewById(R.id.toolbar)
        flipper         = findViewById(R.id.wizardFlipper)
        progress        = findViewById(R.id.wizardProgress)
        stepIndicator   = findViewById(R.id.stepIndicator)
        btnPrimary      = findViewById(R.id.btnPrimary)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        btnPrimary.setOnClickListener { advance() }

        // Map buttons
        findViewById<MaterialButton>(R.id.btnMapPickup).setOnClickListener { openMapPicker(true) }
        findViewById<MaterialButton>(R.id.btnMapDrop).setOnClickListener { openMapPicker(false) }

        // Photo upload button
        findViewById<MaterialButton>(R.id.btnUploadPhotos).setOnClickListener {
            Toast.makeText(this, "اختر صور الحمولة (سيتم رفعها لاحقاً)", Toast.LENGTH_SHORT).show()
            photoUris.add("mock_photo_${photoUris.size + 1}.jpg")
        }

        buildStep1()
        renderStep()
    }

    override fun onBackPressed() {
        if (flipper.displayedChild > 0) {
            flipper.displayedChild -= 1
            renderStep()
        } else {
            super.onBackPressed()
        }
    }

    private fun renderStep() {
        val step = flipper.displayedChild + 1
        progress.progress = step
        stepIndicator.text = "خطوة $step / $TOTAL_STEPS"
        toolbar.title = when (step) {
            1 -> "نوع الحمولة"
            2 -> "العنوانين"
            3 -> "الحجم والوزن"
            4 -> "مراجعة وتأكيد"
            else -> "طلب حمولة"
        }
        if (step == TOTAL_STEPS) {
            buildReview()
            btnPrimary.text = "تأكيد الطلب"
        } else {
            btnPrimary.text = "التالي"
        }
    }

    private fun advance() {
        val step = flipper.displayedChild
        val ok = when (step) {
            0 -> validateCategory()      // Step 1 — Category
            1 -> validateAddresses()     // Step 2 — Pickup & Drop-off
            2 -> validateSize()          // Step 3 — Cargo details
            3 -> { submitOrder(); return } // Step 4 — Review
            else -> false
        }
        if (ok && step < TOTAL_STEPS - 1) {
            flipper.displayedChild = step + 1
            renderStep()
        }
    }

    // ------------------------------------------------------------------
    // Step 1 — Cargo Type Grid (6 cards)
    // ------------------------------------------------------------------
    private fun buildStep1() {
        val container = flipper.getChildAt(0) as? LinearLayout ?: return
        container.removeAllViews()

        // Step dots header
        container.addView(buildStepDots(1))

        // Grid of 6 cards
        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val row1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row1.addView(buildCargoCard(0, "مبرد", "أغذية وأدوية", "❄️"))
        row1.addView(buildCargoCard(1, "جاف", "بضائع عامة", "📦"))
        grid.addView(row1)

        val row2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row2.addView(buildCargoCard(2, "سريع", "خلال 24 ساعة", "⚡"))
        row2.addView(buildCargoCard(3, "ثقيل", "أكثر من 500 كيلو", "🚛"))
        grid.addView(row2)

        val row3 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row3.addView(buildCargoCard(4, "دولي", "خارج البلاد", "🌍"))
        row3.addView(buildCargoCard(5, "قابل للكسر", "زجاج وإلكترونيات", "🍷"))
        grid.addView(row3)

        container.addView(grid)
    }

    private fun buildStepDots(activeStep: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(24))

            for (i in 1..TOTAL_STEPS) {
                val isActive = i == activeStep
                val isPast = i < activeStep
                val bgColor = when {
                    isActive -> Color.parseColor("#7BBDE8")
                    isPast -> Color.parseColor("#4A6FA5")
                    else -> Color.parseColor("#1A3A5C")
                }
                val textColor = if (isActive || isPast) Color.WHITE else Color.parseColor("#80FFFFFF")

                addView(TextView(this@NewShipmentActivity).apply {
                    text = "$i"
                    textSize = 16f
                    setTextColor(textColor)
                    setTypeface(typeface, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(bgColor)
                    }
                    layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
                        .apply { setMargins(dp(8), 0, dp(8), 0) }
                })
            }
        }
    }

    private fun buildCargoCard(id: Int, title: String, subtitle: String, emoji: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                setColor(Color.parseColor("#062E54"))
                setStroke(dp(1), Color.parseColor("#22FFFFFF"))
            }
            setPadding(dp(16), dp(20), dp(16), dp(20))
            layoutParams = LinearLayout.LayoutParams(0, dp(140), 1f)
                .apply { setMargins(dp(6), dp(6), dp(6), dp(6)) }

            addView(TextView(this@NewShipmentActivity).apply {
                text = emoji
                textSize = 28f
                gravity = Gravity.CENTER
            })
            addView(TextView(this@NewShipmentActivity).apply {
                text = title
                setTextColor(Color.WHITE)
                textSize = 15f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, 0)
            })
            addView(TextView(this@NewShipmentActivity).apply {
                text = subtitle
                setTextColor(Color.parseColor("#80FFFFFF"))
                textSize = 11f
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, 0)
            })

            setOnClickListener {
                selectedCategoryId = id
                // Visual feedback — could highlight selected card
                Toast.makeText(this@NewShipmentActivity, "تم اختيار: $title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCategory(): Boolean {
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "اختر نوع الحمولة", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateSize(): Boolean {
        val weightInput = findViewById<TextInputEditText>(R.id.cargoWeight)
        val descInput   = findViewById<TextInputEditText>(R.id.cargoDesc)
        val notesInput  = findViewById<TextInputEditText>(R.id.cargoNotes)
        val piecesInput = findViewById<TextInputEditText>(R.id.pieceCount)
        val vehicleRg   = findViewById<android.widget.RadioGroup>(R.id.vehicleTypeGroup)

        cargoDescription = descInput.text?.toString()?.trim() ?: ""
        cargoNotesText   = notesInput.text?.toString()?.trim() ?: ""
        val weightStr    = weightInput.text?.toString()?.trim() ?: ""
        val piecesStr    = piecesInput.text?.toString()?.trim() ?: ""

        if (cargoDescription.isEmpty()) {
            Toast.makeText(this, "أدخل وصف البضاعة", Toast.LENGTH_SHORT).show()
            return false
        }
        if (piecesStr.isEmpty()) {
            Toast.makeText(this, "أدخل عدد القطع", Toast.LENGTH_SHORT).show()
            return false
        }
        pieceCountValue = piecesStr.toIntOrNull() ?: 0
        if (pieceCountValue <= 0) {
            Toast.makeText(this, "عدد القطع غير صالح", Toast.LENGTH_SHORT).show()
            return false
        }
        if (weightStr.isEmpty()) {
            Toast.makeText(this, "أدخل الوزن بالكيلوجرام", Toast.LENGTH_SHORT).show()
            return false
        }
        cargoWeightValue = weightStr.toDoubleOrNull() ?: 0.0
        if (cargoWeightValue <= 0) {
            Toast.makeText(this, "الوزن غير صالح", Toast.LENGTH_SHORT).show()
            return false
        }

        vehicleTypeStr = when (vehicleRg.checkedRadioButtonId) {
            R.id.vtSmall -> "small"
            R.id.vtLarge -> "large"
            R.id.vtRefrigerated -> "refrigerated"
            R.id.vtExpress -> "express"
            else -> "small"
        }

        // Auto-calculate price based on weight, distance (mock), and vehicle
        val basePrice = when (vehicleTypeStr) {
            "small" -> 150.0
            "large" -> 300.0
            "refrigerated" -> 400.0
            "express" -> 500.0
            else -> 150.0
        }
        val distanceKm = 250.0 // TODO: real distance from cities
        estimatedPrice = basePrice + (cargoWeightValue * 0.5) + (distanceKm * 2.0)

        return true
    }

    private fun validateAddresses(): Boolean {
        val pickupCityInput  = findViewById<TextInputEditText>(R.id.pickupCity)
        val pickupInput      = findViewById<TextInputEditText>(R.id.pickupAddr)
        val dropCityInput    = findViewById<TextInputEditText>(R.id.dropCity)
        val dropInput        = findViewById<TextInputEditText>(R.id.dropAddr)
        val dateInput        = findViewById<TextInputEditText>(R.id.pickupDate)
        val timeInput        = findViewById<TextInputEditText>(R.id.pickupTime)
        val nameInput        = findViewById<TextInputEditText>(R.id.recipientName)
        val phoneInput       = findViewById<TextInputEditText>(R.id.recipientPhone)

        pickupCityStr   = pickupCityInput.text?.toString()?.trim() ?: ""
        pickupAddress   = pickupInput.text?.toString()?.trim() ?: ""
        dropCityStr     = dropCityInput.text?.toString()?.trim() ?: ""
        dropAddress     = dropInput.text?.toString()?.trim() ?: ""
        pickupDateStr   = dateInput.text?.toString()?.trim() ?: ""
        pickupTimeStr   = timeInput.text?.toString()?.trim() ?: ""
        recipientNameStr  = nameInput.text?.toString()?.trim() ?: ""
        recipientPhoneStr = phoneInput.text?.toString()?.trim() ?: ""

        if (pickupCityStr.isEmpty()) {
            Toast.makeText(this, "أدخل مدينة الاستلام", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pickupAddress.isEmpty()) {
            Toast.makeText(this, "أدخل عنوان الاستلام", Toast.LENGTH_SHORT).show()
            return false
        }
        if (dropCityStr.isEmpty()) {
            Toast.makeText(this, "أدخل مدينة التسليم", Toast.LENGTH_SHORT).show()
            return false
        }
        if (dropAddress.isEmpty()) {
            Toast.makeText(this, "أدخل عنوان التسليم", Toast.LENGTH_SHORT).show()
            return false
        }
        if (recipientNameStr.isEmpty()) {
            Toast.makeText(this, "أدخل اسم المستلم", Toast.LENGTH_SHORT).show()
            return false
        }
        if (recipientPhoneStr.isEmpty()) {
            Toast.makeText(this, "أدخل رقم هاتف المستلم", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pickupDateStr.isEmpty()) {
            Toast.makeText(this, "أدخل تاريخ التحميل", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pickupTimeStr.isEmpty()) {
            Toast.makeText(this, "أدخل الوقت المناسب", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun buildReview() {
        val container = flipper.getChildAt(3) as? LinearLayout ?: return
        container.removeAllViews()

        val categoryNames = listOf("مبرد", "جاف", "سريع", "ثقيل", "دولي", "قابل للكسر")
        val categoryName = categoryNames.getOrNull(selectedCategoryId) ?: "—"
        val vehicleLabel = when (vehicleTypeStr) {
            "small" -> "شاحنة صغيرة"
            "large" -> "شاحنة كبيرة"
            "refrigerated" -> "تبريد"
            "express" -> "نقل سريع"
            else -> vehicleTypeStr
        }

        container.addView(TextView(this).apply {
            text = "مراجعة البيانات"
            setTextColor(Color.parseColor("#7BBDE8"))
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(16))
        })

        container.addView(buildSummaryRow("نوع الحمولة", categoryName))
        container.addView(buildSummaryRow("عدد القطع", pieceCountValue.toString()))
        container.addView(buildSummaryRow("الوزن", "${cargoWeightValue.toInt()} كجم"))
        container.addView(buildSummaryRow("المركبة", vehicleLabel))
        container.addView(buildSummaryRow("الاستلام", "${pickupCityStr} — ${pickupAddress}"))
        if (pickupLat != null && pickupLng != null) {
            container.addView(buildSummaryRow("موقع الاستلام", "${String.format("%.5f", pickupLat)}, ${String.format("%.5f", pickupLng)}"))
        }
        container.addView(buildSummaryRow("التسليم", "${dropCityStr} — ${dropAddress}"))
        if (dropLat != null && dropLng != null) {
            container.addView(buildSummaryRow("موقع التسليم", "${String.format("%.5f", dropLat)}, ${String.format("%.5f", dropLng)}"))
        }
        container.addView(buildSummaryRow("المستلم", "$recipientNameStr — $recipientPhoneStr"))
        container.addView(buildSummaryRow("الموعد", "$pickupDateStr ${pickupTimeStr}"))
        if (photoUris.isNotEmpty()) {
            container.addView(buildSummaryRow("الصور المرفقة", "${photoUris.size} صورة"))
        }
        if (cargoNotesText.isNotEmpty()) {
            container.addView(buildSummaryRow("ملاحظات", cargoNotesText))
        }

        // Estimated Price Card
        val priceCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                cornerRadius = dp(16).toFloat()
                colors = intArrayOf(Color.parseColor("#0A4174"), Color.parseColor("#7BBDE8"))
                orientation = GradientDrawable.Orientation.TL_BR
            }
            setPadding(dp(18), dp(18), dp(18), dp(18))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(16) }

            addView(TextView(this@NewShipmentActivity).apply {
                text = "السعر المتوقع"
                setTextColor(Color.parseColor("#CCFFFFFF"))
                textSize = 13f
            })
            addView(TextView(this@NewShipmentActivity).apply {
                text = "${estimatedPrice.toInt()} ج.م"
                setTextColor(Color.WHITE)
                textSize = 28f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, dp(4), 0, 0)
            })
            addView(TextView(this@NewShipmentActivity).apply {
                text = "يشمل: الوزن + المسافة + نوع المركبة"
                setTextColor(Color.parseColor("#CCFFFFFF"))
                textSize = 11f
                setPadding(0, dp(4), 0, 0)
            })
        }
        container.addView(priceCard)
    }

    private fun buildSummaryRow(label: String, value: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = GradientDrawable().apply {
                cornerRadius = dp(12).toFloat()
                setColor(Color.parseColor("#062E54"))
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }

            addView(TextView(this@NewShipmentActivity).apply {
                text = label
                setTextColor(Color.parseColor("#A0FFFFFF"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            addView(TextView(this@NewShipmentActivity).apply {
                text = value
                setTextColor(Color.WHITE)
                textSize = 13f
                setTypeface(typeface, Typeface.BOLD)
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.4f)
            })
        }
    }

    private fun submitOrder() {
        btnPrimary.isEnabled = false
        btnPrimary.text = "جاري الإرسال..."

        val cargoType = when (selectedCategoryId) {
            0 -> "refrigerated"
            1 -> "dry"
            2 -> "express"
            3 -> "heavy"
            4 -> "international"
            5 -> "fragile"
            else -> "dry"
        }

        val request = CreateShipmentRequest(
            pickupLocation = pickupAddress,
            deliveryLocation = dropAddress,
            cargoType = cargoType,
            weightKg = cargoWeightValue,
            priority = if (selectedCategoryId == 2) "urgent" else "normal",
            notes = cargoNotesText,
            pickupCity = pickupCityStr.ifEmpty { null },
            dropCity = dropCityStr.ifEmpty { null },
            pickupDate = pickupDateStr.ifEmpty { null },
            pickupTime = pickupTimeStr.ifEmpty { null },
            recipientName = recipientNameStr.ifEmpty { null },
            recipientPhone = recipientPhoneStr.ifEmpty { null },
            pieceCount = pieceCountValue.takeIf { it > 0 },
            vehicleType = vehicleTypeStr,
            estimatedPrice = estimatedPrice.takeIf { it > 0 },
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            dropLat = dropLat,
            dropLng = dropLng,
            photoUris = photoUris.takeIf { it.isNotEmpty() }
        )

        lifecycleScope.launch {
            try {
                val response = shipmentApi.createShipment(request)
                if (response.isSuccessful && response.body() != null) {
                    AlertDialog.Builder(this@NewShipmentActivity)
                        .setTitle("تم استلام طلبك ✅")
                        .setMessage("تم إنشاء الشحنة بنجاح. رقم التتبع: ${response.body()!!.data?.id}")
                        .setPositiveButton("حسناً") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    btnPrimary.isEnabled = true
                    btnPrimary.text = "تأكيد الطلب"
                    Toast.makeText(this@NewShipmentActivity, "فشل في إنشاء الشحنة", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                btnPrimary.isEnabled = true
                btnPrimary.text = "تأكيد الطلب"
                Toast.makeText(this@NewShipmentActivity, "خطأ في الاتصال: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMapPicker(isPickup: Boolean) {
        val title = if (isPickup) "تحديد موقع الاستلام" else "تحديد موقع التسليم"
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(16))

            val latInput = com.google.android.material.textfield.TextInputEditText(this@NewShipmentActivity).apply {
                hint = "خط العرض (مثل 24.7136)"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#80FFFFFF"))
                inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_CLASS_NUMBER
            }
            val lngInput = com.google.android.material.textfield.TextInputEditText(this@NewShipmentActivity).apply {
                hint = "خط الطول (مثل 46.6753)"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#80FFFFFF"))
                inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_CLASS_NUMBER
            }

            addView(com.google.android.material.textfield.TextInputLayout(this@NewShipmentActivity).apply {
                this.hint = "خط العرض"
                addView(latInput)
            })
            addView(com.google.android.material.textfield.TextInputLayout(this@NewShipmentActivity).apply {
                this.hint = "خط الطول"
                setPadding(0, dp(8), 0, 0)
                addView(lngInput)
            })

            // Store refs to read later
            tag = Pair(latInput, lngInput)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage("أدخل الإحداثيات يدوياً أو استخدم الخريطة لاحقاً:")
            .setView(dialogView)
            .setPositiveButton("حفظ") { _, _ ->
                val (latInput, lngInput) = dialogView.tag as Pair<com.google.android.material.textfield.TextInputEditText, com.google.android.material.textfield.TextInputEditText>
                val lat = latInput.text?.toString()?.toDoubleOrNull()
                val lng = lngInput.text?.toString()?.toDoubleOrNull()
                if (lat != null && lng != null) {
                    if (isPickup) {
                        pickupLat = lat; pickupLng = lng
                    } else {
                        dropLat = lat; dropLng = lng
                    }
                    Toast.makeText(this, "تم حفظ الموقع", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()

    companion object { private const val TOTAL_STEPS = 4 }
}
