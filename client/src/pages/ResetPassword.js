import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Lock, ArrowLeft } from 'lucide-react';
import { motion } from 'framer-motion';
import Logo from '../components/Logo';
import { api } from '../context/AuthContext';

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setMessage('كلمات المرور غير متطابقة');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      await api.post('/auth/reset-password', { token, password });
      setMessage('تم إعادة تعيين كلمة المرور بنجاح');
      setTimeout(() => navigate('/login'), 2000);
    } catch (error) {
      setMessage(error.response?.data?.message || 'حدث خطأ');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-edham-black via-edham-dark to-edham-primary/20 flex items-center justify-center px-4 py-12">
      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="max-w-md w-full card p-8"
      >
        <button 
          onClick={() => navigate('/login')}
          className="flex items-center gap-2 text-edham-white/60 hover:text-edham-white mb-8 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          العودة
        </button>

        <div className="text-center mb-8">
          <div className="w-20 h-20 bg-edham-primary/20 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <Lock className="w-10 h-10 text-edham-primary" />
          </div>
          <h1 className="text-2xl font-bold text-edham-white mb-2">إعادة تعيين كلمة المرور</h1>
          <p className="text-edham-white/60">أدخل كلمة المرور الجديدة</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="input-label">كلمة المرور الجديدة</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
              className="input-field"
              placeholder="********"
            />
          </div>

          <div>
            <label className="input-label">تأكيد كلمة المرور</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              className="input-field"
              placeholder="********"
            />
          </div>

          {message && (
            <motion.div 
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className={`p-4 rounded-2xl text-sm ${
                message.includes('نجاح') ? 'status-success' : 'status-error'
              }`}
            >
              {message}
            </motion.div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full"
          >
            {loading ? 'جار الحفظ...' : 'حفظ كلمة المرور'}
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default ResetPassword;
