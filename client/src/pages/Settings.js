import React, { useState } from 'react';
import { Save, Bell, Shield, Globe, Truck } from 'lucide-react';
import { motion } from 'framer-motion';

const Settings = () => {
  const [settings, setSettings] = useState({
    companyName: 'إدهام للخدمات اللوجستية',
    supportEmail: 'info@edham.com',
    supportPhone: '+966 50 XXX XXXX',
    defaultLanguage: 'ar',
    notifications: true,
    emailAlerts: true,
    smsAlerts: false,
    autoAssign: true,
    temperatureAlerts: true,
    maintenanceReminder: true
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    setSettings(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    setMessage('تم حفظ الإعدادات بنجاح');
    setLoading(false);
  };

  return (
    <div className="p-6 bg-edham-black min-h-screen">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-4xl mx-auto"
      >
        <h1 className="text-3xl font-bold text-edham-white mb-8">إعدادات النظام</h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Company Settings */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <Truck className="w-6 h-6 text-edham-primary" />
              <h2 className="text-xl font-semibold text-edham-white">إعدادات الشركة</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              <div>
                <label className="input-label">اسم الشركة</label>
                <input
                  type="text"
                  name="companyName"
                  value={settings.companyName}
                  onChange={handleChange}
                  className="input-field"
                />
              </div>
              <div>
                <label className="input-label">بريد الدعم</label>
                <input
                  type="email"
                  name="supportEmail"
                  value={settings.supportEmail}
                  onChange={handleChange}
                  className="input-field"
                />
              </div>
              <div>
                <label className="input-label">هاتف الدعم</label>
                <input
                  type="tel"
                  name="supportPhone"
                  value={settings.supportPhone}
                  onChange={handleChange}
                  className="input-field"
                />
              </div>
              <div>
                <label className="input-label">اللغة الافتراضية</label>
                <select
                  name="defaultLanguage"
                  value={settings.defaultLanguage}
                  onChange={handleChange}
                  className="input-field"
                >
                  <option value="ar">العربية</option>
                  <option value="en">English</option>
                </select>
              </div>
            </div>
          </div>

          {/* Notifications */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <Bell className="w-6 h-6 text-edham-primary" />
              <h2 className="text-xl font-semibold text-edham-white">الإشعارات</h2>
            </div>
            
            <div className="space-y-4">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="notifications"
                  checked={settings.notifications}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تفعيل الإشعارات</span>
              </label>
              
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="emailAlerts"
                  checked={settings.emailAlerts}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تنبيهات البريد الإلكتروني</span>
              </label>
              
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="smsAlerts"
                  checked={settings.smsAlerts}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تنبيهات الرسائل النصية</span>
              </label>
            </div>
          </div>

          {/* System Settings */}
          <div className="card">
            <div className="flex items-center gap-3 mb-6">
              <Shield className="w-6 h-6 text-edham-primary" />
              <h2 className="text-xl font-semibold text-edham-white">إعدادات النظام</h2>
            </div>
            
            <div className="space-y-4">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="autoAssign"
                  checked={settings.autoAssign}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تعيين السائقين تلقائياً</span>
              </label>
              
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="temperatureAlerts"
                  checked={settings.temperatureAlerts}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تنبيهات درجة الحرارة</span>
              </label>
              
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  name="maintenanceReminder"
                  checked={settings.maintenanceReminder}
                  onChange={handleChange}
                  className="w-5 h-5 rounded border-gray-600 bg-gray-700 text-edham-primary focus:ring-edham-primary"
                />
                <span className="text-edham-white">تذكير الصيانة الدورية</span>
              </label>
            </div>
          </div>

          {message && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="status-success"
            >
              {message}
            </motion.div>
          )}

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="btn-primary flex items-center gap-2"
            >
              <Save className="w-5 h-5" />
              {loading ? 'جار الحفظ...' : 'حفظ الإعدادات'}
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  );
};

export default Settings;
