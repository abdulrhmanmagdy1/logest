package com.edham.logistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Arabic titles
        view.findViewById<TextView>(R.id.tv_settings_title).text = "الإعدادات"
        view.findViewById<TextView>(R.id.tv_general_settings).text = "إعدادات عامة"
        view.findViewById<TextView>(R.id.tv_account_settings).text = "إعدادات الحساب"
        view.findViewById<TextView>(R.id.tv_notification_settings).text = "إعدادات الإشعارات"
        view.findViewById<TextView>(R.id.tv_map_settings).text = "إعدادات الخرائط"
        view.findViewById<TextView>(R.id.tv_cold_chain_settings).text = "إعدادات التبريد"
        view.findViewById<TextView>(R.id.tv_security_settings).text = "إعدادات الأمان"
        view.findViewById<TextView>(R.id.tv_privacy_settings).text = "إعدادات الخصوصية"
        
        // Set general settings items
        view.findViewById<TextView>(R.id.tv_language).text = "اللغة"
        view.findViewById<TextView>(R.id.tv_theme).text = "المظهر"
        view.findViewById<TextView>(R.id.tv_font).text = "الخط"
        view.findViewById<TextView>(R.id.tv_timezone).text = "المنطقة الزمنية"
        
        // Set account settings items
        view.findViewById<TextView>(R.id.tv_change_password).text = "تغيير كلمة المرور"
        view.findViewById<TextView>(R.id.tv_two_factor).text = "المصادقة الثنائية"
        view.findViewById<TextView>(R.id.tv_devices).text = "الأجهزة المسجلة"
        
        // Set notification settings items
        view.findViewById<TextView>(R.id.tv_shipment_notifications).text = "إشعارات الشحنات"
        view.findViewById<TextView>(R.id.tv_maintenance_notifications).text = "إشعارات الصيانة"
        view.findViewById<TextView>(R.id.tv_payment_notifications).text = "إشعارات المدفوعات"
        view.findViewById<TextView>(R.id.tv_sound_settings).text = "إعدادات الصوت"
        
        // Set map settings items
        view.findViewById<TextView>(R.id.tv_map_type).text = "نوع الخريطة"
        view.findViewById<TextView>(R.id.tv_live_tracking).text = "التتبع المباشر"
        view.findViewById<TextView>(R.id.tv_route_display).text = "عرض المسار"
        
        // Set cold chain settings items
        view.findViewById<TextView>(R.id.tv_temperature_limits).text = "حدود درجة الحرارة"
        view.findViewById<TextView>(R.id.tv_humidity_limits).text = "حدود الرطوبة"
        view.findViewById<TextView>(R.id.tv_alert_thresholds).text = "عتبات التنبيه"
        
        // Set security settings items
        view.findViewById<TextView>(R.id.tv_biometric).text = "المصادقة البيومترية"
        view.findViewById<TextView>(R.id.tv_session_timeout).text = "انتهاء الجلسة"
        view.findViewById<TextView>(R.id.tv_data_encryption).text = "تشفير البيانات"
        
        // Set privacy settings items
        view.findViewById<TextView>(R.id.tv_location_access).text = "الوصول للموقع"
        view.findViewById<TextView>(R.id.tv_data_collection).text = "جمع البيانات"
        view.findViewById<TextView>(R.id.tv_analytics).text = "التحليلات"
    }
}
