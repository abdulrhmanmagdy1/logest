import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, FileText, CheckCircle, XCircle, ArrowLeft, Shield, ArrowRight } from 'lucide-react';
import Logo from '../components/Logo';

const InvoiceVerification = () => {
  const navigate = useNavigate();
  const [serialNumber, setSerialNumber] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleVerify = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    setLoading(true);

    try {
      const response = await fetch('http://192.168.1.12:5000/api/invoices/verify', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          serialNumber: serialNumber || undefined,
          verificationCode: verificationCode || undefined
        })
      });

      const data = await response.json();

      if (response.ok) {
        setResult(data);
      } else {
        setError(data.message || 'فشل التحقق من الفاتورة');
      }
    } catch (err) {
      setError('حدث خطأ أثناء التحقق');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-edham-black flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-gradient-to-br from-edham-black via-edham-dark to-edham-gray"></div>
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-20 w-32 h-32 bg-edham-white rounded-full blur-3xl"></div>
        <div className="absolute bottom-20 right-20 w-48 h-48 bg-edham-primary rounded-full blur-3xl"></div>
      </div>
      
      <div className="relative bg-edham-dark/80 backdrop-blur-lg rounded-3xl p-8 w-full max-w-2xl border border-edham-gray shadow-2xl">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <div className="bg-edham-white p-4 rounded-full">
              <Logo size="lg" />
            </div>
          </div>
          <h1 className="text-3xl font-bold text-edham-white mb-2">التحقق من الفواتير</h1>
          <p className="text-edham-white/70">تحقق من صحة الفاتورة باستخدام الرقم التسلسلي أو رمز التحقق</p>
        </div>

        <form onSubmit={handleVerify} className="space-y-6">
          {error && (
            <div className="bg-edham-error/20 border border-edham-error/50 text-white px-4 py-3 rounded-lg flex items-center gap-2">
              <XCircle className="w-5 h-5" />
              {error}
            </div>
          )}

          <div>
            <label className="block text-edham-white mb-2 font-medium">الرقم التسلسلي</label>
            <div className="relative">
              <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="text"
                value={serialNumber}
                onChange={(e) => setSerialNumber(e.target.value.toUpperCase())}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary font-mono"
                placeholder="أدخل الرقم التسلسلي (مثل: SN123456789ABC)"
              />
            </div>
          </div>

          <div className="text-center text-edham-white/50">أو</div>

          <div>
            <label className="block text-edham-white mb-2 font-medium">رمز التحقق</label>
            <div className="relative">
              <Shield className="absolute right-3 top-1/2 transform -translate-y-1/2 text-edham-white/60 w-5 h-5" />
              <input
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value.toUpperCase())}
                className="w-full pr-12 pl-4 py-3 bg-edham-black border border-edham-gray rounded-lg text-edham-white placeholder-edham-white/50 focus:outline-none focus:ring-2 focus:ring-edham-primary font-mono"
                placeholder="أدخل رمز التحقق (8 أحرف)"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading || (!serialNumber && !verificationCode)}
            className="w-full bg-edham-primary text-white font-bold py-3 rounded-lg hover:bg-edham-primaryLight transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? 'جاري التحقق...' : 'تحقق من الفاتورة'}
            <Shield className="w-5 h-5" />
          </button>
        </form>

        {result && result.valid && (
          <div className="mt-8 bg-green-500/10 border border-green-500/30 rounded-2xl p-6">
            <div className="flex items-center gap-3 mb-4">
              <div className="bg-green-500/30 p-3 rounded-full">
                <CheckCircle className="w-6 h-6 text-green-400" />
              </div>
              <h3 className="text-xl font-bold text-green-400">فاتورة صالحة</h3>
            </div>
            <div className="space-y-3">
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">رقم الفاتورة</span>
                <span className="text-edham-white font-mono">{result.invoice.invoiceNumber}</span>
              </div>
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">الرقم التسلسلي</span>
                <span className="text-edham-white font-mono">{result.invoice.serialNumber}</span>
              </div>
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">المبلغ</span>
                <span className="text-edham-white font-bold">${result.invoice.amount.toLocaleString()}</span>
              </div>
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">الحالة</span>
                <span className={`px-3 py-1 rounded-full text-sm ${
                  result.invoice.status === 'paid' ? 'bg-green-500/20 text-green-400' :
                  result.invoice.status === 'pending' ? 'bg-yellow-500/20 text-yellow-400' :
                  'bg-red-500/20 text-red-400'
                }`}>
                  {result.invoice.status === 'paid' ? 'مدفوعة' :
                   result.invoice.status === 'pending' ? 'قيد الانتظار' :
                   result.invoice.status}
                </span>
              </div>
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">العميل</span>
                <span className="text-edham-white">{result.invoice.client}</span>
              </div>
              <div className="flex justify-between items-center bg-edham-black/50 rounded-lg p-3">
                <span className="text-edham-white/70">تاريخ الإصدار</span>
                <span className="text-edham-white">{new Date(result.invoice.createdAt).toLocaleDateString('ar-EG')}</span>
              </div>
              {result.invoice.isVerified && (
                <div className="flex items-center gap-2 bg-blue-500/10 rounded-lg p-3 mt-4">
                  <Shield className="w-5 h-5 text-blue-400" />
                  <span className="text-blue-400">هذه الفاتورة موثقة ومعتمدة</span>
                </div>
              )}
            </div>
          </div>
        )}

        <div className="mt-8 text-center">
          <button
            onClick={() => navigate('/')}
            className="text-edham-white/70 hover:text-edham-white transition-colors flex items-center justify-center gap-2 mx-auto"
          >
            العودة للصفحة الرئيسية
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default InvoiceVerification;
