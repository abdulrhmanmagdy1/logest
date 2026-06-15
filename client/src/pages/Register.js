import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, Lock, Phone, Building, Truck, MapPin, ArrowLeft, Shield } from 'lucide-react';
import Logo from '../components/Logo';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    if (formData.password !== formData.confirmPassword) {
      setError('كلمات المرور غير متطابقة');
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError('كلمة المرور يجب أن تكون 6 أحرف على الأقل');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch('http://192.168.1.12:5000/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: formData.name,
          email: formData.email,
          password: formData.password,
          phone: formData.phone,
          role: 'client'
        })
      });

      const data = await response.json();

      if (response.ok) {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        navigate('/login');
      } else {
        setError(data.message || 'فشل التسجيل');
      }
    } catch (err) {
      setError('فشل الاتصال بالخادم');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-edham-black p-4">
      <div className="absolute inset-0 bg-gradient-to-br from-edham-black via-edham-dark to-edham-gray"></div>
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-20 w-32 h-32 bg-edham-white rounded-full blur-3xl"></div>
        <div className="absolute bottom-20 right-20 w-48 h-48 bg-edham-primary rounded-full blur-3xl"></div>
      </div>
      
      <div className="relative bg-edham-dark/80 backdrop-blur-lg rounded-3xl p-8 w-full max-w-lg border border-edham-gray shadow-2xl">
        <button 
          onClick={() => navigate('/')}
          className="absolute top-4 left-4 text-edham-white/60 hover:text-edham-white transition-colors"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>

        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="bg-edham-white p-4 rounded-full">
              <Logo size="lg" />
            </div>
          </div>
          <h1 className="text-3xl font-bold text-edham-white mb-2">إنشاء حساب عميل</h1>
          <p className="text-edham-white/60">انضم إلى نظام إدهام للوجستيات كعميل</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="bg-edham-error/20 border border-edham-error/50 text-white px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          <div>
            <label className="block text-edham-white mb-2 font-medium">الاسم الكامل</label>
            <div className="relative">
              <User className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
                placeholder="أدخل اسمك الكامل"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-edham-white mb-2 font-medium">البريد الإلكتروني</label>
            <div className="relative">
              <Mail className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
                placeholder="أدخل بريدك الإلكتروني"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-edham-white mb-2 font-medium">رقم الهاتف</label>
            <div className="relative">
              <Phone className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="tel"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
                placeholder="أدخل رقم هاتفك"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-edham-white mb-2 font-medium">كلمة المرور</label>
            <div className="relative">
              <Lock className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
                placeholder="أدخل كلمة المرور (6 أحرف على الأقل)"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-edham-white mb-2 font-medium">تأكيد كلمة المرور</label>
            <div className="relative">
              <Lock className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary"
                placeholder="أعد إدخال كلمة المرور"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-edham-primary text-white font-bold py-3 rounded-lg hover:bg-edham-primaryLight transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'جاري إنشاء الحساب...' : 'إنشاء حساب'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-edham-white/60 text-sm">
            لديك حساب بالفعل؟{' '}
            <button
              onClick={() => navigate('/login')}
              className="text-edham-gold font-semibold hover:underline"
            >
              تسجيل الدخول
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
