/**
 * ============================================
 * 🚀 Onboarding Flow - 6 Premium Screens
 * ============================================
 */

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronRight, Globe } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const OnboardingFlow = () => {
  const navigate = useNavigate();
  const [currentScreen, setCurrentScreen] = useState(0);
  const [language, setLanguage] = useState('en');

  const screens = [
    {
      title: language === 'en' ? 'Live Shipment Tracking' : 'تتبع الشحنات المباشر',
      description: language === 'en' 
        ? 'Track every shipment in real-time with precise GPS location and status updates'
        : 'تتبع كل شحنة بدقة فائقة مع تحديثات الموقع والحالة الفورية',
      image: '📍',
      color: 'from-blue-600 to-cyan-600'
    },
    {
      title: language === 'en' ? 'Fleet Management' : 'إدارة الأسطول',
      description: language === 'en'
        ? 'Manage your entire fleet efficiently with real-time vehicle insights and maintenance tracking'
        : 'أدر أسطولك بكفاءة مع رؤى المركبات الفورية ومتابعة الصيانة',
      image: '🚚',
      color: 'from-purple-600 to-pink-600'
    },
    {
      title: language === 'en' ? 'Smart Logistics Operations' : 'العمليات اللوجستية الذكية',
      description: language === 'en'
        ? 'Optimize routes, manage resources, and automate workflows for maximum efficiency'
        : 'حسّن المسارات وأدر الموارد وأتمتة سير العمل للحد الأقصى من الكفاءة',
      image: '⚡',
      color: 'from-green-600 to-emerald-600'
    },
    {
      title: language === 'en' ? 'Real-Time Monitoring' : 'المراقبة الفورية',
      description: language === 'en'
        ? 'Get instant notifications and alerts for all critical operational events and metrics'
        : 'احصل على تنبيهات فورية لجميع الأحداث والمقاييس التشغيلية الحرجة',
      image: '📊',
      color: 'from-orange-600 to-red-600'
    },
    {
      title: language === 'en' ? 'Cold Chain Tracking' : 'تتبع السلسلة الباردة',
      description: language === 'en'
        ? 'Advanced temperature monitoring for temperature-sensitive cargo with compliance reporting'
        : 'مراقبة درجة الحرارة المتقدمة للحمولات الحساسة مع تقارير الامتثال',
      image: '❄️',
      color: 'from-blue-600 to-cyan-600'
    },
    {
      title: language === 'en' ? 'Enterprise Platform' : 'منصة المؤسسات',
      description: language === 'en'
        ? 'Secure, scalable, and trusted by leading logistics companies worldwide'
        : 'آمنة وقابلة للتوسع وموثوقة من قبل شركات لوجستيات رائدة في جميع أنحاء العالم',
      image: '🏢',
      color: 'from-indigo-600 to-blue-600'
    }
  ];

  const screen = screens[currentScreen];

  const handleNext = () => {
    if (currentScreen < screens.length - 1) {
      setCurrentScreen(currentScreen + 1);
    } else {
      navigate('/auth');
    }
  };

  const handlePrev = () => {
    if (currentScreen > 0) {
      setCurrentScreen(currentScreen - 1);
    }
  };

  const slideVariants = {
    enter: (direction) => ({
      x: direction > 0 ? 1000 : -1000,
      opacity: 0
    }),
    center: {
      zIndex: 1,
      x: 0,
      opacity: 1
    },
    exit: (direction) => ({
      zIndex: 0,
      x: direction < 0 ? 1000 : -1000,
      opacity: 0
    })
  };

  return (
    <div className="fixed inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900">
      {/* Language selector */}
      <motion.button
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        onClick={() => setLanguage(language === 'en' ? 'ar' : 'en')}
        className="absolute top-6 right-6 z-50 flex items-center gap-2 px-4 py-2 bg-white/10 hover:bg-white/20 rounded-lg text-white transition-all backdrop-blur-md"
      >
        <Globe className="w-4 h-4" />
        <span className="text-sm font-semibold">{language === 'en' ? 'EN' : 'AR'}</span>
      </motion.button>

      {/* Main content */}
      <div className="h-screen flex items-center justify-center px-6">
        <div className="max-w-2xl w-full">
          <AnimatePresence mode="wait" custom={currentScreen}>
            <motion.div
              key={currentScreen}
              custom={currentScreen}
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ type: 'spring', stiffness: 300, damping: 30 }}
              className="space-y-8"
            >
              {/* Image section */}
              <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.2 }}
                className={`bg-gradient-to-br ${screen.color} rounded-3xl p-1 shadow-2xl`}
              >
                <div className="bg-gray-900 rounded-3xl p-16 flex items-center justify-center relative overflow-hidden">
                  {/* Animated background elements */}
                  <motion.div
                    className="absolute inset-0 opacity-10"
                    animate={{
                      backgroundPosition: ['0% 0%', '100% 100%'],
                    }}
                    transition={{ duration: 20, repeat: Infinity }}
                    style={{
                      backgroundImage: 'radial-gradient(circle, currentColor 1px, transparent 1px)',
                      backgroundSize: '50px 50px',
                    }}
                  />

                  {/* Main icon */}
                  <motion.div
                    animate={{
                      y: [0, -20, 0],
                      rotate: [0, 5, 0],
                    }}
                    transition={{ duration: 4, repeat: Infinity }}
                    className="text-9xl relative z-10"
                  >
                    {screen.image}
                  </motion.div>
                </div>
              </motion.div>

              {/* Text section */}
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 }}
                className="text-center space-y-4"
              >
                <h2 className="text-4xl md:text-5xl font-bold text-white">
                  {screen.title}
                </h2>
                <p className="text-gray-400 text-lg md:text-xl leading-relaxed">
                  {screen.description}
                </p>
              </motion.div>

              {/* Progress indicators */}
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.4 }}
                className="flex justify-center gap-2"
              >
                {screens.map((_, idx) => (
                  <motion.button
                    key={idx}
                    onClick={() => setCurrentScreen(idx)}
                    animate={{
                      width: idx === currentScreen ? 32 : 12,
                      opacity: idx === currentScreen ? 1 : 0.4
                    }}
                    className="h-2 rounded-full bg-white transition-all"
                  />
                ))}
              </motion.div>
            </motion.div>
          </AnimatePresence>

          {/* Navigation controls */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="mt-12 flex items-center justify-between"
          >
            <button
              onClick={handlePrev}
              disabled={currentScreen === 0}
              className="px-6 py-3 text-white disabled:opacity-30 disabled:cursor-not-allowed hover:bg-white/10 rounded-lg transition-all"
            >
              {language === 'en' ? '← Back' : 'رجوع →'}
            </button>

            <div className="text-center">
              <p className="text-gray-400 text-sm">
                {currentScreen + 1} {language === 'en' ? 'of' : 'من'} {screens.length}
              </p>
            </div>

            <motion.button
              onClick={handleNext}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="px-8 py-3 bg-gradient-to-r from-blue-500 to-cyan-500 text-white rounded-lg font-semibold flex items-center gap-2 hover:shadow-lg transition-all"
            >
              {currentScreen === screens.length - 1
                ? language === 'en' ? 'Get Started' : 'ابدأ الآن'
                : language === 'en' ? 'Next' : 'التالي'}
              <ChevronRight className="w-5 h-5" />
            </motion.button>
          </motion.div>
        </div>
      </div>

      {/* Floating background elements */}
      <motion.div
        className="absolute top-20 left-10 w-96 h-96 bg-blue-500 rounded-full opacity-10 blur-3xl"
        animate={{
          y: [0, -50, 0],
        }}
        transition={{ duration: 10, repeat: Infinity }}
      />
      <motion.div
        className="absolute bottom-20 right-10 w-96 h-96 bg-purple-500 rounded-full opacity-10 blur-3xl"
        animate={{
          y: [0, 50, 0],
        }}
        transition={{ duration: 12, repeat: Infinity }}
      />
    </div>
  );
};

export default OnboardingFlow;
