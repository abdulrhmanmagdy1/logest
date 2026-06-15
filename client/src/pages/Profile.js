import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, Mail, Phone, Building, Camera } from 'lucide-react';
import { motion } from 'framer-motion';
import Logo from '../components/Logo';

const Profile = () => {
  const { user, api } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    company: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        company: user.company || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      await api.put('/users/profile', formData);
      setMessage('تم تحديث الملف الشخصي بنجاح');
    } catch (error) {
      setMessage(error.response?.data?.message || 'حدث خطأ');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-edham-black p-6">
      <div className="max-w-3xl mx-auto">
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card"
        >
          <div className="flex items-center gap-4 mb-8 pb-6 border-b border-white/10">
            <div className="relative">
              <div className="w-24 h-24 bg-edham-primary/20 rounded-2xl flex items-center justify-center">
                <Logo size="lg" />
              </div>
              <button className="absolute -bottom-2 -right-2 w-8 h-8 bg-edham-primary rounded-full flex items-center justify-center hover:bg-edham-primary-600 transition-colors">
                <Camera className="w-4 h-4 text-white" />
              </button>
            </div>
            <div>
              <h1 className="text-2xl font-bold text-edham-white">الملف الشخصي</h1>
              <p className="text-edham-white/60">{user?.role}</p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid md:grid-cols-2 gap-6">
              <div>
                <label className="input-label">الاسم</label>
                <div className="relative">
                  <User className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className="input-field pr-12"
                  />
                </div>
              </div>

              <div>
                <label className="input-label">البريد الإلكتروني</label>
                <div className="relative">
                  <Mail className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    className="input-field pr-12"
                    disabled
                  />
                </div>
              </div>

              <div>
                <label className="input-label">الهاتف</label>
                <div className="relative">
                  <Phone className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    className="input-field pr-12"
                  />
                </div>
              </div>

              <div>
                <label className="input-label">الشركة</label>
                <div className="relative">
                  <Building className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="text"
                    name="company"
                    value={formData.company}
                    onChange={handleChange}
                    className="input-field pr-12"
                  />
                </div>
              </div>
            </div>

            {message && (
              <motion.div 
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className={`p-4 rounded-2xl text-sm ${
                  message.includes('نجاح') ? 'status-success' : 'status-error'
                }`}
              >
                {message}
              </motion.div>
            )}

            <div className="flex justify-end gap-4 pt-6 border-t border-white/10">
              <button
                type="submit"
                disabled={loading}
                className="btn-primary"
              >
                {loading ? 'جار الحفظ...' : 'حفظ التغييرات'}
              </button>
            </div>
          </form>
        </motion.div>
      </div>
    </div>
  );
};

export default Profile;
