// ============================================
// 🚀 Edham Logistics - Advanced Localization Manager
// Premium Dark Theme with Multi-Language Support
// ============================================

package com.edham.logistics.ui.localization

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.edham.logistics.ui.theme.EdhamOrange
import kotlinx.coroutines.*
import java.util.*

/**
 * ============================================
 * Advanced Localization Manager
 * ============================================
 * مدير الترجمة المتقدم مع دعم متعدد اللغات
 */
class LocalizationManager(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs: SharedPreferences = context.getSharedPreferences("localization", Context.MODE_PRIVATE)
    
    companion object {
        const val ARABIC = "ar"
        const val ENGLISH = "en"
        const val FRENCH = "fr"
        const val SPANISH = "es"
        const val CHINESE = "zh"
        const val HINDI = "hi"
        const val URDU = "ur"
        const val TURKISH = "tr"
        const val INDONESIAN = "id"
        const val MALAY = "ms"
        
        val SUPPORTED_LANGUAGES = listOf(
            LanguageInfo(ARABIC, "العربية", "🇸🇦", true, true),
            LanguageInfo(ENGLISH, "English", "🇺🇸", true, true),
            LanguageInfo(FRENCH, "Français", "🇫🇷", false, false),
            LanguageInfo(SPANISH, "Español", "🇪🇸", false, false),
            LanguageInfo(CHINESE, "中文", "🇨🇳", false, false),
            LanguageInfo(HINDI, "हिन्दी", "🇮🇳", false, false),
            LanguageInfo(URDU, "اردو", "🇵🇰", false, false),
            LanguageInfo(TURKISH, "Türkçe", "🇹🇷", false, false),
            LanguageInfo(INDONESIAN, "Bahasa Indonesia", "🇮🇩", false, false),
            LanguageInfo(MALAY, "Bahasa Melayu", "🇲🇾", false, false)
        )
    }
    
    data class LanguageInfo(
        val code: String,
        val name: String,
        val flag: String,
        val isRTL: Boolean,
        val isDefault: Boolean
    )
    
    data class LocalizedString(
        val key: String,
        val translations: Map<String, String>
    )
    
    /**
     * ============================================
     * Language Management
     * ============================================
     */
    fun getCurrentLanguage(): String {
        return prefs.getString("current_language", ARABIC) ?: ARABIC
    }
    
    fun setLanguage(languageCode: String) {
        val languageInfo = SUPPORTED_LANGUAGES.find { it.code == languageCode }
            ?: SUPPORTED_LANGUAGES.first()
        
        prefs.edit {
            putString("current_language", languageCode)
            putBoolean("is_rtl", languageInfo.isRTL)
        }
        
        applyLanguage(languageCode)
    }
    
    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        // Update app direction
        val languageInfo = SUPPORTED_LANGUAGES.find { it.code == languageCode }
        if (languageInfo?.isRTL == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
    
    fun getLanguageInfo(languageCode: String): LanguageInfo? {
        return SUPPORTED_LANGUAGES.find { it.code == languageCode }
    }
    
    fun isRTL(): Boolean {
        return prefs.getBoolean("is_rtl", true)
    }
    
    fun getSupportedLanguages(): List<LanguageInfo> {
        return SUPPORTED_LANGUAGES
    }
    
    /**
     * ============================================
     * String Localization
     * ============================================
     */
    fun getString(key: String, vararg args: Any): String {
        val languageCode = getCurrentLanguage()
        val translation = getTranslation(key, languageCode)
        
        return if (args.isNotEmpty()) {
            String.format(translation, *args)
        } else {
            translation
        }
    }
    
    fun getStringArray(key: String): Array<String> {
        val languageCode = getCurrentLanguage()
        val translation = getTranslation(key, languageCode)
        
        return translation.split("|").toTypedArray()
    }
    
    private fun getTranslation(key: String, languageCode: String): String {
        val translations = getTranslations()
        val localizedString = translations.find { it.key == key }
        
        return localizedString?.translations?.get(languageCode)
            ?: localizedString?.translations?.get(ARABIC)
            ?: key
    }
    
    private fun getTranslations(): List<LocalizedString> {
        return listOf(
            // Navigation
            LocalizedString("nav_home", mapOf(
                ARABIC to "الرئيسية",
                ENGLISH to "Home",
                FRENCH to "Accueil",
                SPANISH to "Inicio",
                CHINESE to "首页",
                HINDI to "होम",
                URDU to "ہوم",
                TURKISH to "Ana Sayfa",
                INDONESIAN to "Beranda",
                MALAY to "Utama"
            )),
            
            LocalizedString("nav_shipments", mapOf(
                ARABIC to "الشحنات",
                ENGLISH to "Shipments",
                FRENCH to "Expéditions",
                SPANISH to "Envíos",
                CHINESE to "货运",
                HINDI to "शिपमेंट",
                URDU to "شپمنٹس",
                TURKISH to "Sevkiyatlar",
                INDONESIAN to "Pengiriman",
                MALAY to "Penghantaran"
            )),
            
            LocalizedString("nav_tracking", mapOf(
                ARABIC to "التتبع",
                ENGLISH to "Tracking",
                FRENCH to "Suivi",
                SPANISH to "Seguimiento",
                CHINESE to "跟踪",
                HINDI to "ट्रैकिंग",
                URDU to "ٹریکنگ",
                TURKISH to "Takip",
                INDONESIAN to "Pelacakan",
                MALAY to "Penjejakan"
            )),
            
            LocalizedString("nav_payments", mapOf(
                ARABIC to "المدفوعات",
                ENGLISH to "Payments",
                FRENCH to "Paiements",
                SPANISH to "Pagos",
                CHINESE to "付款",
                HINDI to "भुगतानियाँ",
                URDU to "ادائیگیاں",
                TURKISH to "Ödemeler",
                INDONESIAN to "Pembayaran",
                MALAY to "Pembayaran"
            )),
            
            LocalizedString("nav_profile", mapOf(
                ARABIC to "الملف الشخصي",
                ENGLISH to "Profile",
                FRENCH to "Profil",
                SPANISH to "Perfil",
                CHINESE to "个人资料",
                HINDI to "प्रोफ़ाइल",
                URDU to "پروفائل",
                TURKISH to "Profil",
                INDONESIAN to "Profil",
                MALAY to "Profil"
            )),
            
            // Actions
            LocalizedString("action_create", mapOf(
                ARABIC to "إنشاء",
                ENGLISH to "Create",
                FRENCH to "Créer",
                SPANISH to "Crear",
                CHINESE to "创建",
                HINDI to "बनाएं",
                URDU to "بنائیں",
                TURKISH to "Oluştur",
                INDONESIAN to "Buat",
                MALAY to "Cipta"
            )),
            
            LocalizedString("action_edit", mapOf(
                ARABIC to "تعديل",
                ENGLISH to "Edit",
                FRENCH to "Modifier",
                SPANISH to "Editar",
                CHINESE to "编辑",
                HINDI to "संपादित करें",
                URDU to "ترمیم کریں",
                TURKISH to "Düzenle",
                INDONESIAN to "Edit",
                MALAY to "Edit"
            )),
            
            LocalizedString("action_delete", mapOf(
                ARABIC to "حذف",
                ENGLISH to "Delete",
                FRENCH to "Supprimer",
                SPANISH to "Eliminar",
                CHINESE to "删除",
                HINDI to "हटाएं",
                URDU to "حذف کریں",
                TURKISH to "Sil",
                INDONESIAN to "Hapus",
                MALAY to "Padam"
            )),
            
            LocalizedString("action_save", mapOf(
                ARABIC to "حفظ",
                ENGLISH to "Save",
                FRENCH to "Enregistrer",
                SPANISH to "Guardar",
                CHINESE to "保存",
                HINDI to "सहेजें",
                URDU to "محفوظ کریں",
                TURKISH to "Kaydet",
                INDONESIAN to "Simpan",
                MALAY to "Simpan"
            )),
            
            LocalizedString("action_cancel", mapOf(
                ARABIC to "إلغاء",
                ENGLISH to "Cancel",
                FRENCH to "Annuler",
                SPANISH to "Cancelar",
                CHINESE to "取消",
                HINDI to "रद्द करें",
                URDU to "منسوخ کریں",
                TURKISH to "İptal",
                INDONESIAN to "Batal",
                MALAY to "Batal"
            )),
            
            // Status
            LocalizedString("status_pending", mapOf(
                ARABIC to "في الانتظار",
                ENGLISH to "Pending",
                FRENCH to "En attente",
                SPANISH to "Pendiente",
                CHINESE to "待处理",
                HINDI to "लंबित",
                URDU to "زیر التوقع",
                TURKISH to "Beklemede",
                INDONESIAN to "Menunggu",
                MALAY to "Menunggu"
            )),
            
            LocalizedString("status_in_transit", mapOf(
                ARABIC to "في الطريق",
                ENGLISH to "In Transit",
                FRENCH to "En transit",
                SPANISH to "En tránsito",
                CHINESE to "运输中",
                HINDI to "परिवहन में",
                URDU to "راست میں",
                TURKISH to "Yolda",
                INDONESIAN to "Dalam Perjalanan",
                MALAY to "Dalam Perjalanan"
            )),
            
            LocalizedString("status_delivered", mapOf(
                ARABIC to "تم التسليم",
                ENGLISH to "Delivered",
                FRENCH to "Livré",
                SPANISH to "Entregado",
                CHINESE to "已送达",
                HINDI to "वितरित",
                URDU to "پہنچا دیا گیا",
                TURKISH to "Teslim Edildi",
                INDONESIAN to "Terkirim",
                MALAY to "Dihantar"
            )),
            
            LocalizedString("status_failed", mapOf(
                ARABIC to "فشل",
                ENGLISH to "Failed",
                FRENCH to "Échec",
                SPANISH to "Fallido",
                CHINESE to "失败",
                HINDI to "असफल",
                URDU to "ناکام ہوا",
                TURKISH to "Başarısız",
                INDONESIAN to "Gagal",
                MALAY to "Gagal"
            )),
            
            // Messages
            LocalizedString("msg_loading", mapOf(
                ARABIC to "جاري التحميل...",
                ENGLISH to "Loading...",
                FRENCH to "Chargement...",
                SPANISH to "Cargando...",
                CHINESE to "加载中...",
                HINDI to "लोड हो रहा है...",
                URDU to "لوڈ ہو رہا ہے...",
                TURKISH to "Yükleniyor...",
                INDONESIAN to "Memuat...",
                MALAY to "Memuatkan..."
            )),
            
            LocalizedString("msg_success", mapOf(
                ARABIC to "تم بنجاح!",
                ENGLISH to "Success!",
                FRENCH to "Succès!",
                SPANISH to "¡Éxito!",
                CHINESE to "成功！",
                HINDI to "सफलता!",
                URDU to "کامیابی!",
                TURKISH to "Başarılı!",
                INDONESIAN to "Berhasil!",
                MALAY to "Berjaya!"
            )),
            
            LocalizedString("msg_error", mapOf(
                ARABIC to "حدث خطأ!",
                ENGLISH to "Error!",
                FRENCH to "Erreur!",
                SPANISH to "¡Error!",
                CHINESE to "错误！",
                HINDI to "त्रुटि!",
                URDU to "خرابی!",
                TURKISH to "Hata!",
                INDONESIAN to "Kesalahan!",
                MALAY to "Ralat!"
            )),
            
            LocalizedString("msg_no_internet", mapOf(
                ARABIC to "لا يوجد اتصال بالإنترنت",
                ENGLISH to "No internet connection",
                FRENCH to "Pas de connexion Internet",
                SPANISH to "Sin conexión a Internet",
                CHINESE to "没有网络连接",
                HINDI to "कोई इंटरनेट कनेक्शन नहीं",
                URDU to "انٹرنیٹ کنکشن نہیں",
                TURKISH to "İnternet bağlantısı yok",
                INDONESIAN to "Tidak ada koneksi internet",
                MALAY to "Tiada sambungan internet"
            )),
            
            // Forms
            LocalizedString("form_email", mapOf(
                ARABIC to "البريد الإلكتروني",
                ENGLISH to "Email",
                FRENCH to "E-mail",
                SPANISH to "Correo electrónico",
                CHINESE to "电子邮件",
                HINDI to "ईमेल",
                URDU to "ای میل",
                TURKISH to "E-posta",
                INDONESIAN to "Email",
                MALAY to "E-mel"
            )),
            
            LocalizedString("form_password", mapOf(
                ARABIC to "كلمة المرور",
                ENGLISH to "Password",
                FRENCH to "Mot de passe",
                SPANISH to "Contraseña",
                CHINESE to "密码",
                HINDI to "पासवर्ड",
                URDU to "پاس ورڈ",
                TURKISH to "Şifre",
                INDONESIAN to "Kata Sandi",
                MALAY to "Kata Laluan"
            )),
            
            LocalizedString("form_phone", mapOf(
                ARABIC to "رقم الهاتف",
                ENGLISH to "Phone",
                FRENCH to "Téléphone",
                SPANISH to "Teléfono",
                CHINESE to "电话",
                HINDI to "फोन",
                URDU to "فون نمبر",
                TURKISH to "Telefon",
                INDONESIAN to "Telepon",
                MALAY to "Telefon"
            )),
            
            LocalizedString("form_address", mapOf(
                ARABIC to "العنوان",
                ENGLISH to "Address",
                FRENCH to "Adresse",
                SPANISH to "Dirección",
                CHINESE to "地址",
                HINDI to "पता",
                URDU to "پتہ",
                TURKISH to "Adres",
                INDONESIAN to "Alamat",
                MALAY to "Alamat"
            )),
            
            // Shipment
            LocalizedString("shipment_tracking", mapOf(
                ARABIC to "رقم التتبع",
                ENGLISH to "Tracking Number",
                FRENCH to "Numéro de suivi",
                SPANISH to "Número de seguimiento",
                CHINESE to "跟踪号码",
                HINDI to "ट्रैकिंग नंबर",
                URDU to "ٹریکنگ نمبر",
                TURKISH to "Takip Numarası",
                INDONESIAN to "Nomor Pelacakan",
                MALAY to "Nombor Penjejakan"
            )),
            
            LocalizedString("shipment_origin", mapOf(
                ARABIC to "المنشأ",
                ENGLISH to "Origin",
                FRENCH to "Origine",
                SPANISH to "Origen",
                CHINESE to "起始地",
                HINDI to "मूल",
                URDU to "اصل",
                TURKISH to "Menşe",
                INDONESIAN to "Asal",
                MALAY to "Asal"
            )),
            
            LocalizedString("shipment_destination", mapOf(
                ARABIC to "الوجهة",
                ENGLISH to "Destination",
                FRENCH to "Destination",
                SPANISH to "Destino",
                CHINESE to "目的地",
                HINDI to "गंतव्य",
                URDU to "منزل",
                TURKISH to "Varış",
                INDONESIAN to "Tujuan",
                MALAY to "Destinasi"
            )),
            
            LocalizedString("shipment_weight", mapOf(
                ARABIC to "الوزن",
                ENGLISH to "Weight",
                FRENCH to "Poids",
                SPANISH to "Peso",
                CHINESE to "重量",
                HINDI to "वजन",
                URDU to "وزن",
                TURKISH to "Ağırlık",
                INDONESIAN to "Berat",
                MALAY to "Berat"
            )),
            
            LocalizedString("shipment_dimensions", mapOf(
                ARABIC to "الأبعاد",
                ENGLISH to "Dimensions",
                FRENCH to "Dimensions",
                SPANISH to "Dimensiones",
                CHINESE to "尺寸",
                HINDI to "आयाम",
                URDU to "پیمائش",
                TURKISH to "Boyutlar",
                INDONESIAN to "Dimensi",
                MALAY to "Dimensi"
            )),
            
            // Payment
            LocalizedString("payment_amount", mapOf(
                ARABIC to "المبلغ",
                ENGLISH to "Amount",
                FRENCH to "Montant",
                SPANISH to "Cantidad",
                CHINESE to "金额",
                HINDI to "राशि",
                URDU to "رقم",
                TURKISH to "Tutar",
                INDONESIAN to "Jumlah",
                MALAY to "Jumlah"
            )),
            
            LocalizedString("payment_method", mapOf(
                ARABIC to "طريقة الدفع",
                ENGLISH to "Payment Method",
                FRENCH to "Méthode de paiement",
                SPANISH to "Método de pago",
                CHINESE to "支付方式",
                HINDI to "भुगतानी विधि",
                URDU to "ادائیگی کا طریقہ",
                TURKISH to "Ödeme Yöntemi",
                INDONESIAN to "Metode Pembayaran",
                MALAY to "Kaedah Pembayaran"
            )),
            
            LocalizedString("payment_status", mapOf(
                ARABIC to "حالة الدفع",
                ENGLISH to "Payment Status",
                FRENCH to "Statut de paiement",
                SPANISH to "Estado del pago",
                CHINESE to "支付状态",
                HINDI to "भुगतानी स्थिति",
                URDU to "ادائیگی کی حالت",
                TURKISH to "Ödeme Durumu",
                INDONESIAN to "Status Pembayaran",
                MALAY to "Status Pembayaran"
            )),
            
            // Time
            LocalizedString("time_today", mapOf(
                ARABIC to "اليوم",
                ENGLISH to "Today",
                FRENCH to "Aujourd'hui",
                SPANISH to "Hoy",
                CHINESE to "今天",
                HINDI to "आज",
                URDU to "آج",
                TURKISH to "Bugün",
                INDONESIAN to "Hari Ini",
                MALAY to "Hari Ini"
            )),
            
            LocalizedString("time_yesterday", mapOf(
                ARABIC to "أمس",
                ENGLISH to "Yesterday",
                FRENCH to "Hier",
                SPANISH to "Ayer",
                CHINESE to "昨天",
                HINDI to "कल",
                URDU to "کل",
                TURKISH to "Dün",
                INDONESIAN to "Kemarin",
                MALAY to "Semalam"
            )),
            
            LocalizedString("time_tomorrow", mapOf(
                ARABIC to "غداً",
                ENGLISH to "Tomorrow",
                FRENCH to "Demain",
                SPANISH to "Mañana",
                CHINESE to "明天",
                HINDI to "कल",
                URDU to "کل",
                TURKISH to "Yarın",
                INDONESIAN to "Besok",
                MALAY to "Esok"
            )),
            
            // Common Phrases
            LocalizedString("welcome", mapOf(
                ARABIC to "مرحباً بك",
                ENGLISH to "Welcome",
                FRENCH to "Bienvenue",
                SPANISH to "Bienvenido",
                CHINESE to "欢迎",
                HINDI to "स्वागत है",
                URDU to "خوش آمدید",
                TURKISH to "Hoş Geldiniz",
                INDONESIAN to "Selamat Datang",
                MALAY to "Selamat Datang"
            )),
            
            LocalizedString("thank_you", mapOf(
                ARABIC to "شكراً لك",
                ENGLISH to "Thank you",
                FRENCH to "Merci",
                SPANISH to "Gracias",
                CHINESE to "谢谢",
                HINDI to "धन्यवाद",
                URDU to "شکریہ",
                TURKISH to "Teşekkürler",
                INDONESIAN to "Terima Kasih",
                MALAY to "Terima Kasih"
            )),
            
            LocalizedString("goodbye", mapOf(
                ARABIC to "وداعاً",
                ENGLISH to "Goodbye",
                FRENCH to "Au revoir",
                SPANISH to "Adiós",
                CHINESE to "再见",
                HINDI to "अलविदा",
                URDU to "الوداع",
                TURKISH to "Hoşça kal",
                INDONESIAN to "Sampai Jumpa",
                MALAY to "Selamat Tinggal"
            )),
            
            // Error Messages
            LocalizedString("error_invalid_email", mapOf(
                ARABIC to "البريد الإلكتروني غير صالح",
                ENGLISH to "Invalid email address",
                FRENCH to "Adresse e-mail invalide",
                SPANISH to "Correo electrónico inválido",
                CHINESE to "无效的电子邮件地址",
                HINDI to "अमान्य ईमेल पता",
                URDU to "غلط ای میل پتہ",
                TURKISH to "Geçersiz e-posta adresi",
                INDONESIAN to "Alamat email tidak valid",
                MALAY to "Alamat e-mel tidak sah"
            )),
            
            LocalizedString("error_invalid_password", mapOf(
                ARABIC to "كلمة المرور غير صحيحة",
                ENGLISH to "Incorrect password",
                FRENCH to "Mot de passe incorrect",
                SPANISH to "Contraseña incorrecta",
                CHINESE to "密码不正确",
                HINDI to "गलत पासवर्ड",
                URDU to "غلط پاس ورڈ",
                TURKISH to "Yanlış şifre",
                INDONESIAN to "Kata sandi salah",
                MALAY to "Kata laluan salah"
            )),
            
            LocalizedString("error_network_error", mapOf(
                ARABIC to "خطأ في الشبكة",
                ENGLISH to "Network error",
                FRENCH to "Erreur réseau",
                SPANISH to "Error de red",
                CHINESE to "网络错误",
                HINDI to "नेटवर्क त्रुटि",
                URDU to "نیٹ ورک خرابی",
                TURKISH to "Ağ hatası",
                INDONESIAN to "Kesalahan jaringan",
                MALAY to "Ralat rangkaian"
            )),
            
            LocalizedString("error_server_error", mapOf(
                ARABIC to "خطأ في الخادم",
                ENGLISH to "Server error",
                FRENCH to "Erreur serveur",
                SPANISH to "Error del servidor",
                CHINESE to "服务器错误",
                HINDI to "सर्वर त्रुटि",
                URDU to "سرور خرابی",
                TURKISH to "Sunucu hatası",
                INDONESIAN to "Kesalahan server",
                MALAY to "Ralat pelayan"
            ))
        )
    }
    
    /**
     * ============================================
     * Date and Time Localization
     * ============================================
     */
    fun formatDate(date: Date): String {
        val languageCode = getCurrentLanguage()
        val locale = Locale(languageCode)
        
        val pattern = when (languageCode) {
            ARABIC, URDU -> "dd MMMM yyyy"
            CHINESE -> "yyyy年MM月dd日"
            else -> "MMM dd, yyyy"
        }
        
        return SimpleDateFormat(pattern, locale).format(date)
    }
    
    fun formatTime(date: Date): String {
        val languageCode = getCurrentLanguage()
        val locale = Locale(languageCode)
        
        val pattern = when (languageCode) {
            ARABIC, URDU -> "HH:mm"
            else -> "hh:mm a"
        }
        
        return SimpleDateFormat(pattern, locale).format(date)
    }
    
    fun formatDateTime(date: Date): String {
        val languageCode = getCurrentLanguage()
        val locale = Locale(languageCode)
        
        val pattern = when (languageCode) {
            ARABIC, URDU -> "dd MMMM yyyy HH:mm"
            CHINESE -> "yyyy年MM月dd日 HH:mm"
            else -> "MMM dd, yyyy hh:mm a"
        }
        
        return SimpleDateFormat(pattern, locale).format(date)
    }
    
    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        val languageCode = getCurrentLanguage()
        
        return when {
            days > 0 -> {
                when (languageCode) {
                    ARABIC -> "منذ ${days.toInt()} يوم"
                    ENGLISH -> "${days.toInt()} days ago"
                    FRENCH -> "Il y a ${days.toInt()} jours"
                    SPANISH -> "Hace ${days.toInt()} días"
                    CHINESE -> "${days.toInt()} 天前"
                    HINDI -> "${days.toInt()} दिन पहले"
                    URDU -> "${days.toInt()} دن پہلے"
                    TURKISH -> "${days.toInt()} gün önce"
                    INDONESIAN -> "${days.toInt()} hari yang lalu"
                    MALAY -> "${days.toInt()} hari yang lepas"
                    else -> "${days.toInt()} days ago"
                }
            }
            hours > 0 -> {
                when (languageCode) {
                    ARABIC -> "منذ ${hours.toInt()} ساعة"
                    ENGLISH -> "${hours.toInt()} hours ago"
                    FRENCH -> "Il y a ${hours.toInt()} heures"
                    SPANISH -> "Hace ${hours.toInt()} horas"
                    CHINESE -> "${hours.toInt()} 小时前"
                    HINDI -> "${hours.toInt()} घंटे पहले"
                    URDU -> "${hours.toInt()} گھنٹے پہلے"
                    TURKISH -> "${hours.toInt()} saat önce"
                    INDONESIAN -> "${hours.toInt()} jam yang lalu"
                    MALAY -> "${hours.toInt()} jam yang lepas"
                    else -> "${hours.toInt()} hours ago"
                }
            }
            minutes > 0 -> {
                when (languageCode) {
                    ARABIC -> "منذ ${minutes.toInt()} دقيقة"
                    ENGLISH -> "${minutes.toInt()} minutes ago"
                    FRENCH -> "Il y a ${minutes.toInt()} minutes"
                    SPANISH -> "Hace ${minutes.toInt()} minutos"
                    CHINESE -> "${minutes.toInt()} 分钟前"
                    HINDI -> "${minutes.toInt()} मिनट पहले"
                    URDU -> "${minutes.toInt()} منٹ پہلے"
                    TURKISH -> "${minutes.toInt()} dakika önce"
                    INDONESIAN -> "${minutes.toInt()} menit yang lalu"
                    MALAY -> "${minutes.toInt()} minit yang lepas"
                    else -> "${minutes.toInt()} minutes ago"
                }
            }
            else -> {
                when (languageCode) {
                    ARABIC -> "الآن"
                    ENGLISH -> "Just now"
                    FRENCH -> "À l'instant"
                    SPANISH -> "Ahora mismo"
                    CHINESE -> "刚刚"
                    HINDI -> "अभी"
                    URDU => "ابھی"
                    TURKISH -> "Şimdi"
                    INDONESIAN -> "Baru saja"
                    MALAY -> "Baru sahaja"
                    else -> "Just now"
                }
            }
        }
    }
    
    /**
     * ============================================
     * Number Localization
     * ============================================
     */
    fun formatNumber(number: Double): String {
        val languageCode = getCurrentLanguage()
        val locale = Locale(languageCode)
        
        return when (languageCode) {
            ARABIC, URDU -> {
                val formatter = DecimalFormat("#,##0.00")
                formatter.maximumFractionDigits = 2
                formatter.minimumFractionDigits = 0
                formatter.format(number)
            }
            else -> {
                String.format(locale, "%,.2f", number)
            }
        }
    }
    
    fun formatCurrency(amount: Double, currency: String = "SAR"): String {
        val languageCode = getCurrentLanguage()
        val locale = Locale(languageCode)
        
        val formattedAmount = formatNumber(amount)
        
        return when (languageCode) {
            ARABIC, URDU -> "$formattedAmount $currency"
            else -> "$currency $formattedAmount"
        }
    }
    
    /**
     * ============================================
     * Advanced Features
     * ============================================
     */
    fun getLocalizedDirections(): Map<String, String> {
        val languageCode = getCurrentLanguage()
        
        return when (languageCode) {
            ARABIC, URDU -> mapOf(
                "start" to "البداية",
                "end" to "النهاية",
                "north" to "شمال",
                "south" to "جنوب",
                "east" to "شرق",
                "west" to "غرب",
                "left" to "يسار",
                "right" to "يمين",
                "up" to "أعلى",
                "down" to "أسفل"
            )
            else -> mapOf(
                "start" to "Start",
                "end" to "End",
                "north" to "North",
                "south" to "South",
                "east" to "East",
                "west" to "West",
                "left" to "Left",
                "right" to "Right",
                "up" to "Up",
                "down" to "Down"
            )
        }
    }
    
    fun getLocalizedWeekdays(): Array<String> {
        val languageCode = getCurrentLanguage()
        
        return when (languageCode) {
            ARABIC -> arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
            URDU -> arrayOf("اتوار", "پیر", "منگل", "بدھ", "جمعرات", "جمعہ", "ہفتہ")
            FRENCH -> arrayOf("Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi")
            SPANISH -> arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            CHINESE -> arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
            HINDI -> arrayOf("रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")
            TURKISH -> arrayOf("Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi")
            INDONESIAN -> arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
            MALAY -> arrayOf("Ahad", "Isnin", "Selasa", "Rabu", "Khamis", "Jumaat", "Sabtu")
            else -> arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        }
    }
    
    fun getLocalizedMonths(): Array<String> {
        val languageCode = getCurrentLanguage()
        
        return when (languageCode) {
            ARABIC -> arrayOf("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
            URDU -> arrayOf("جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون", "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر")
            FRENCH -> arrayOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")
            SPANISH -> arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
            CHINESE -> arrayOf("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月")
            HINDI -> arrayOf("जनवरी", "फरवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")
            TURKISH -> arrayOf("Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık")
            INDONESIAN -> arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
            MALAY -> arrayOf("Januari", "Februari", "Mac", "April", "Mei", "Jun", "Julai", "Ogos", "September", "Oktober", "November", "Disember")
            else -> arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        }
    }
    
    /**
     * ============================================
     * Auto-detection
     * ============================================
     */
    fun detectAndSetLanguage() {
        val systemLocale = Locale.getDefault()
        val systemLanguage = systemLocale.language
        
        val supportedLanguage = SUPPORTED_LANGUAGES.find { 
            it.code.equals(systemLanguage, ignoreCase = true) 
        }
        
        if (supportedLanguage != null) {
            setLanguage(supportedLanguage.code)
        } else {
            // Check if system language is RTL
            val isRTL = systemLocale.textDirection == Locale.TEXT_DIRECTION_RTL
            if (isRTL) {
                setLanguage(ARABIC)
            } else {
                setLanguage(ENGLISH)
            }
        }
    }
    
    /**
     * ============================================
     * Language Learning
     * ============================================
     */
    fun getLanguageLearningProgress(): Float {
        return prefs.getFloat("language_learning_progress", 0.0f)
    }
    
    fun updateLanguageLearningProgress(progress: Float) {
        prefs.edit {
            putFloat("language_learning_progress", progress)
        }
    }
    
    fun getPreferredLanguage(): String {
        return prefs.getString("preferred_language", getCurrentLanguage()) ?: getCurrentLanguage()
    }
    
    fun setPreferredLanguage(languageCode: String) {
        prefs.edit {
            putString("preferred_language", languageCode)
        }
    }
}
