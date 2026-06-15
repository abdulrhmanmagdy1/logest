import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, ArrowLeft } from 'lucide-react';
import { motion } from 'framer-motion';
import Logo from '../components/Logo';
import { api } from '../context/AuthContext';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      await api.post('/auth/forgot-password', { email });
      setMessage('تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني');
    } catch (error) {
      setMessage(error.response?.data?.message || 'حدث خطأ، حاول مرة أخرى');
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
          العودة لتسجيل الدخول
        </button>

        <div className="text-center mb-8">
          <div className="w-20 h-20 bg-edham-primary/20 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <Mail className="w-10 h-10 text-edham-primary" />
          </div>
          <h1 className="text-2xl font-bold text-edham-white mb-2">نسيت كلمة المرور؟</h1>
          <p className="text-edham-white/60">أدخل بريدك الإلكتروني لاستعادة حسابك</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="input-label">البريد الإلكتروني</label>
            <div className="relative">
              <Mail className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="input-field pr-12"
                placeholder="your@email.com"
              />
            </div>
          </div>

          {message && (
            <motion.div 
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className={`p-4 rounded-2xl text-sm ${
                message.includes('تم') ? 'status-success' : 'status-error'
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
            {loading ? 'جار الإرسال...' : 'إرسال رابط الاستعادة'}
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default ForgotPassword;
