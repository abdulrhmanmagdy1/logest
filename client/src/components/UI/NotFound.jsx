/**
 * ============================================
 * 🔍 Not Found Component - نظام إدهام
 * Edham Logistics - 404 Page
 * ============================================
 */

import React from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, Home, ArrowRight } from 'lucide-react';

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-900 flex items-center justify-center p-4">
      <div className="text-center">
        <div className="text-blue-500 mb-6 flex justify-center">
          <AlertTriangle className="w-24 h-24" />
        </div>
        <h1 className="text-6xl font-bold text-white mb-4">404</h1>
        <h2 className="text-2xl font-bold text-gray-300 mb-4">الصفحة غير موجودة</h2>
        <p className="text-gray-400 mb-8 max-w-md mx-auto">
          عذراً، الصفحة التي تبحث عنها غير موجودة أو تم نقلها أو حذفها.
        </p>
        <div className="flex gap-4 justify-center">
          <button
            onClick={() => navigate(-1)}
            className="bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-6 rounded flex items-center gap-2 transition"
          >
            <ArrowRight className="w-4 h-4" />
            رجوع
          </button>
          <button
            onClick={() => navigate('/dashboard')}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded flex items-center gap-2 transition"
          >
            <Home className="w-4 h-4" />
            الرئيسية
          </button>
        </div>
      </div>
    </div>
  );
}
