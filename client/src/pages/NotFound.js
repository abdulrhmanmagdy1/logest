import React from 'react';
import { Link } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';
import { motion } from 'framer-motion';
import Logo from '../components/Logo';

const NotFound = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-edham-black via-edham-dark to-edham-primary/20 flex items-center justify-center px-4 py-12">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        className="max-w-md w-full bg-edham-gray/50 backdrop-blur-xl rounded-3xl border border-white/10 p-8 text-center shadow-2xl"
      >
        <div className="w-24 h-24 bg-white/10 rounded-2xl flex items-center justify-center mx-auto mb-8">
          <Logo size="xl" />
        </div>
        
        <h1 className="text-6xl font-bold text-gradient mb-4">404</h1>
        <p className="text-xl text-edham-white/80 mb-8">
          الصفحة التي تبحث عنها غير موجودة
        </p>
        
        <div className="space-y-4">
          <Link
            to="/"
            className="w-full btn-primary flex items-center justify-center gap-2"
          >
            <Home className="w-5 h-5" />
            العودة للرئيسية
          </Link>
          
          <Link
            to="/dashboard"
            className="w-full btn-secondary flex items-center justify-center gap-2"
          >
            <ArrowLeft className="w-5 h-5" />
            لوحة التحكم
          </Link>
        </div>
        
        <p className="text-sm text-edham-white/50 mt-8">
          إدهام للخدمات اللوجستية © 2024
        </p>
      </motion.div>
    </div>
  );
};

export default NotFound;
