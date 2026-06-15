/**
 * ============================================
 * ⚙️ Settings Page - نظام إدهام
 * Edham Logistics - System Settings
 * ============================================
 */

import React, { useState } from "react";
import { motion } from "framer-motion";
import {
  Settings, Bell, Shield, Palette, Globe, Mail, Smartphone,
  Save, CheckCircle, AlertTriangle, User, Lock, Eye, EyeOff,
  Moon, Sun, Monitor, ChevronRight
} from "lucide-react";
import { useAuth } from "../../context/AuthContext";

// ── Section Card ──────────────────────────
const SectionCard = ({ title, description, icon: Icon, children }) => (
  <div className="card">
    <div className="flex items-start gap-4 mb-6">
      <div className="w-12 h-12 bg-edham-primary/20 rounded-xl flex items-center justify-center flex-shrink-0">
        <Icon className="w-6 h-6 text-edham-primary" />
      </div>
      <div>
        <h3 className="text-lg font-bold text-edham-white">{title}</h3>
        <p className="text-edham-text-muted text-sm">{description}</p>
      </div>
    </div>
    {children}
  </div>
);

// ── Toggle Switch ─────────────────────────
const Toggle = ({ checked, onChange, label }) => (
  <label className="flex items-center justify-between cursor-pointer group">
    <span className="text-edham-white">{label}</span>
    <div className={`relative w-12 h-6 rounded-full transition-colors ${
      checked ? "bg-edham-primary" : "bg-edham-gray"
    }`}>
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        className="sr-only"
      />
      <div className={`absolute top-1 w-4 h-4 rounded-full bg-white transition-transform ${
        checked ? "translate-x-7" : "translate-x-1"
      }`} />
    </div>
  </label>
);

// ── Input Field ───────────────────────────
const InputField = ({ label, type = "text", value, onChange, placeholder, icon: Icon }) => (
  <div className="space-y-1.5">
    <label className="block text-sm text-edham-text-muted">{label}</label>
    <div className="relative">
      {Icon && (
        <Icon className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
      )}
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        className={`w-full bg-edham-black border border-edham-gray rounded-lg py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary transition-colors ${
          Icon ? "pr-10 pl-4" : "px-4"
        }`}
      />
    </div>
  </div>
);

// ── Main SettingsPage ─────────────────────
const SettingsPage = () => {
  const { user } = useAuth();
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  // Profile Settings
  const [profile, setProfile] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phone: user?.phone || "",
  });

  // Notification Settings
  const [notifications, setNotifications] = useState({
    emailAlerts: true,
    pushNotifications: true,
    shipmentUpdates: true,
    maintenanceAlerts: true,
    paymentReminders: true,
    marketingEmails: false,
  });

  // Security Settings
  const [security, setSecurity] = useState({
    twoFactor: false,
    loginAlerts: true,
    sessionTimeout: "30",
  });

  // Appearance Settings
  const [appearance, setAppearance] = useState({
    theme: "dark",
    compactMode: false,
    animations: true,
  });

  const handleSave = async () => {
    setSaving(true);
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setSaving(false);
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">الإعدادات</h2>
          <p className="text-edham-text-muted text-sm mt-1">تخصيص إعدادات النظام والحساب</p>
        </div>
        <button
          onClick={handleSave}
          disabled={saving}
          className="btn-primary disabled:opacity-50"
        >
          {saving ? (
            <><div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" /><span>جاري الحفظ...</span></>
          ) : saved ? (
            <><CheckCircle className="w-4 h-4" /><span>تم الحفظ</span></>
          ) : (
            <><Save className="w-4 h-4" /><span>حفظ التغييرات</span></>
          )}
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Profile Settings */}
        <SectionCard
          title="الملف الشخصي"
          description="إدارة معلومات حسابك الشخصي"
          icon={User}
        >
          <div className="space-y-4">
            <div className="flex items-center gap-4 mb-6">
              <div className="w-20 h-20 bg-edham-primary/20 rounded-full flex items-center justify-center">
                <span className="text-2xl font-bold text-edham-primary">
                  {profile.name.charAt(0) || "?"}
                </span>
              </div>
              <button className="btn-sm">
                تغيير الصورة
              </button>
            </div>
            <InputField
              label="الاسم الكامل"
              value={profile.name}
              onChange={(v) => setProfile({ ...profile, name: v })}
              icon={User}
            />
            <InputField
              label="البريد الإلكتروني"
              type="email"
              value={profile.email}
              onChange={(v) => setProfile({ ...profile, email: v })}
              icon={Mail}
            />
            <InputField
              label="رقم الهاتف"
              value={profile.phone}
              onChange={(v) => setProfile({ ...profile, phone: v })}
              icon={Smartphone}
            />
          </div>
        </SectionCard>

        {/* Notification Settings */}
        <SectionCard
          title="الإشعارات"
          description="تحكم في الإشعارات التي تتلقاها"
          icon={Bell}
        >
          <div className="space-y-4">
            <Toggle
              label="تنبيهات البريد الإلكتروني"
              checked={notifications.emailAlerts}
              onChange={(v) => setNotifications({ ...notifications, emailAlerts: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="الإشعارات الفورية"
              checked={notifications.pushNotifications}
              onChange={(v) => setNotifications({ ...notifications, pushNotifications: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="تحديثات الشحنات"
              checked={notifications.shipmentUpdates}
              onChange={(v) => setNotifications({ ...notifications, shipmentUpdates: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="تنبيهات الصيانة"
              checked={notifications.maintenanceAlerts}
              onChange={(v) => setNotifications({ ...notifications, maintenanceAlerts: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="تذكيرات المدفوعات"
              checked={notifications.paymentReminders}
              onChange={(v) => setNotifications({ ...notifications, paymentReminders: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="رسائل تسويقية"
              checked={notifications.marketingEmails}
              onChange={(v) => setNotifications({ ...notifications, marketingEmails: v })}
            />
          </div>
        </SectionCard>

        {/* Security Settings */}
        <SectionCard
          title="الأمان"
          description="إعدادات الأمان وخصوصية الحساب"
          icon={Shield}
        >
          <div className="space-y-4">
            <Toggle
              label="المصادقة الثنائية (2FA)"
              checked={security.twoFactor}
              onChange={(v) => setSecurity({ ...security, twoFactor: v })}
            />
            <p className="text-edham-text-muted text-xs pr-16">
              تفعيل المصادقة الثنائية لزيادة أمان حسابك
            </p>
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="تنبيهات تسجيل الدخول"
              checked={security.loginAlerts}
              onChange={(v) => setSecurity({ ...security, loginAlerts: v })}
            />
            <div className="h-px bg-edham-gray" />
            <div className="space-y-1.5">
              <label className="block text-sm text-edham-text-muted">مهلة الجلسة (دقيقة)</label>
              <select
                value={security.sessionTimeout}
                onChange={(e) => setSecurity({ ...security, sessionTimeout: e.target.value })}
                className="w-full bg-edham-black border border-edham-gray rounded-lg px-4 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              >
                <option value="15">15 دقيقة</option>
                <option value="30">30 دقيقة</option>
                <option value="60">ساعة</option>
                <option value="120">ساعتين</option>
              </select>
            </div>
            <div className="h-px bg-edham-gray" />
            <button className="w-full py-3 px-4 bg-edham-black hover:bg-red-500/20 border border-edham-gray hover:border-red-500/50 rounded-xl text-edham-text-muted hover:text-red-400 transition-colors text-sm flex items-center gap-2">
              <Lock className="w-4 h-4" />
              <span>تغيير كلمة المرور</span>
            </button>
          </div>
        </SectionCard>

        {/* Appearance Settings */}
        <SectionCard
          title="المظهر"
          description="تخصيص مظهر واجهة المستخدم"
          icon={Palette}
        >
          <div className="space-y-4">
            <div>
              <label className="block text-sm text-edham-text-muted mb-3">السمة</label>
              <div className="grid grid-cols-3 gap-3">
                <button
                  onClick={() => setAppearance({ ...appearance, theme: "light" })}
                  className={`p-3 rounded-xl border transition-colors ${
                    appearance.theme === "light"
                      ? "bg-yellow-500/20 border-yellow-500 text-yellow-400"
                      : "bg-edham-black border-edham-gray text-edham-text-muted hover:border-edham-gray/50"
                  }`}
                >
                  <Sun className="w-5 h-5 mx-auto mb-1" />
                  <span className="text-xs">فاتح</span>
                </button>
                <button
                  onClick={() => setAppearance({ ...appearance, theme: "dark" })}
                  className={`p-3 rounded-xl border transition-colors ${
                    appearance.theme === "dark"
                      ? "bg-edham-primary/20 border-edham-primary text-edham-primary"
                      : "bg-edham-black border-edham-gray text-edham-text-muted hover:border-edham-gray/50"
                  }`}
                >
                  <Moon className="w-5 h-5 mx-auto mb-1" />
                  <span className="text-xs">داكن</span>
                </button>
                <button
                  onClick={() => setAppearance({ ...appearance, theme: "system" })}
                  className={`p-3 rounded-xl border transition-colors ${
                    appearance.theme === "system"
                      ? "bg-blue-500/20 border-blue-500 text-blue-400"
                      : "bg-edham-black border-edham-gray text-edham-text-muted hover:border-edham-gray/50"
                  }`}
                >
                  <Monitor className="w-5 h-5 mx-auto mb-1" />
                  <span className="text-xs">تلقائي</span>
                </button>
              </div>
            </div>
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="الوضع المضغوط"
              checked={appearance.compactMode}
              onChange={(v) => setAppearance({ ...appearance, compactMode: v })}
            />
            <div className="h-px bg-edham-gray" />
            <Toggle
              label="الرسوم المتحركة"
              checked={appearance.animations}
              onChange={(v) => setAppearance({ ...appearance, animations: v })}
            />
          </div>
        </SectionCard>

        {/* Language Settings */}
        <SectionCard
          title="اللغة والمنطقة"
          description="إعدادات اللغة والتنسيق الإقليمي"
          icon={Globe}
        >
          <div className="space-y-4">
            <div className="space-y-1.5">
              <label className="block text-sm text-edham-text-muted">اللغة</label>
              <select className="w-full bg-edham-black border border-edham-gray rounded-lg px-4 py-2.5 text-edham-white outline-none focus:border-edham-primary">
                <option value="ar">العربية</option>
                <option value="en">English</option>
              </select>
            </div>
            <div className="h-px bg-edham-gray" />
            <div className="space-y-1.5">
              <label className="block text-sm text-edham-text-muted">المنطقة الزمنية</label>
              <select className="w-full bg-edham-black border border-edham-gray rounded-lg px-4 py-2.5 text-edham-white outline-none focus:border-edham-primary">
                <option value="Africa/Cairo">القاهرة (GMT+2)</option>
                <option value="Asia/Riyadh">الرياض (GMT+3)</option>
                <option value="Asia/Dubai">دبي (GMT+4)</option>
              </select>
            </div>
            <div className="h-px bg-edham-gray" />
            <div className="space-y-1.5">
              <label className="block text-sm text-edham-text-muted">تنسيق التاريخ</label>
              <select className="w-full bg-edham-black border border-edham-gray rounded-lg px-4 py-2.5 text-edham-white outline-none focus:border-edham-primary">
                <option value="DD/MM/YYYY">31/12/2024</option>
                <option value="YYYY/MM/DD">2024/12/31</option>
                <option value="MM/DD/YYYY">12/31/2024</option>
              </select>
            </div>
          </div>
        </SectionCard>

        {/* Danger Zone */}
        <SectionCard
          title="منطقة الخطر"
          description="إجراءات حساسة لا يمكن التراجع عنها"
          icon={AlertTriangle}
        >
          <div className="space-y-3">
            <button className="w-full py-3 px-4 bg-edham-black hover:bg-yellow-500/20 border border-edham-gray hover:border-yellow-500/50 rounded-xl text-edham-text-muted hover:text-yellow-400 transition-colors text-sm flex items-center justify-between group">
              <span>تصدير بيانات الحساب</span>
              <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </button>
            <button className="w-full py-3 px-4 bg-edham-black hover:bg-red-500/20 border border-edham-gray hover:border-red-500/50 rounded-xl text-red-400 transition-colors text-sm flex items-center justify-between group">
              <span>حذف الحساب نهائياً</span>
              <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </button>
          </div>
        </SectionCard>
      </div>
    </div>
  );
};

export default SettingsPage;
