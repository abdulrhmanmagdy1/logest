/**
 * ============================================
 * 🎨 Edham Logistics - Onboarding & Splash Screens
 * نظام إدهام - شاشات الاستقبال والترحيب
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronRight, MapPin, Package, Shield, Clock, Users, Star } from 'lucide-react';
import './OnboardingPage.css';

const OnboardingPage = () => {
  const [currentScreen, setCurrentScreen] = useState('splash');
  const [currentStep, setCurrentStep] = useState(0);
  const [isSwiping, setIsSwiping] = useState(false);
  const [swipeProgress, setSwipeProgress] = useState(0);

  // Splash Screen - 3 seconds then move to onboarding
  useEffect(() => {
    const timer = setTimeout(() => {
      setCurrentScreen('onboarding');
    }, 3000);
    return () => clearTimeout(timer);
  }, []);

  const onboardingSteps = [
    {
      id: 1,
      title: 'الشحن الذكي أصبح سهل',
      subtitle: 'تتبع شحناتك في الوقت الفعلي مع نظام إدهام اللوجستي',
      icon: <Package className="w-16 h-16" />,
      image: '🚛️',
      features: ['تتبع مباشر', 'إشعارات فورية', 'تقارير مفصلة']
    },
    {
      id: 2,
      title: 'إدارة الأسطول الاحترافية',
      subtitle: 'تحكم كامل بأسطولك من أي مكان وفي أي وقت',
      icon: <MapPin className="w-16 h-16" />,
      image: '🗺️',
      features: ['GPS متقدم', 'تتبع السائقين', 'صيانة ذكية']
    },
    {
      id: 3,
      title: 'أمان وموثوقية عالية',
      subtitle: 'نظام آمن بالكامل لحماية بياناتك وشحناتك',
      icon: <Shield className="w-16 h-16" />,
      image: '🔒',
      features: ['تشفير متقدم', 'مصادقة ثنائية', 'نسخ احتياطي']
    },
    {
      id: 4,
      title: 'خدمة على مدار الساعة',
      subtitle: 'فريق دعم متخصص جاهز لمساعدتك في أي وقت',
      icon: <Users className="w-16 h-16" />,
      image: '👥',
      features: ['دعم فوري', 'استشارات مجانية', 'تدريب متكامل']
    }
  ];

  const handleNext = () => {
    if (currentStep < onboardingSteps.length - 1) {
      setCurrentStep(currentStep + 1);
    } else {
      // Navigate to login or main app
      window.location.href = '/login';
    }
  };

  const handleSwipe = (direction) => {
    if (direction === 'right' && currentStep === onboardingSteps.length - 1) {
      // Complete onboarding
      window.location.href = '/login';
    }
  };

  const handleSwipeStart = () => {
    setIsSwiping(true);
  };

  const handleSwipeMove = (e) => {
    if (!isSwiping) return;
    const progress = (e.clientX || e.touches[0].clientX) / window.innerWidth;
    setSwipeProgress(Math.max(0, Math.min(1, progress)));
  };

  const handleSwipeEnd = () => {
    if (swipeProgress > 0.7) {
      handleSwipe('right');
    }
    setIsSwiping(false);
    setSwipeProgress(0);
  };

  // Splash Screen Component - Matching reference image
  const SplashScreen = () => (
    <div className="splash-screen">
      {/* Full screen truck image background */}
      <div className="truck-background">
        <img 
          src="/api/placeholder/400/300" 
          alt="Truck on road"
          className="truck-image"
        />
        <div className="overlay-gradient"></div>
      </div>
      
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, ease: 'easeOut' }}
        className="splash-content"
      >
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.3, duration: 0.8 }}
          className="splash-text"
        >
          <h1 className="splash-title">
            Smart Shipping Made Simple
          </h1>
          <p className="splash-subtitle">
            Lorem Ipsum is simply dummy text of the typesetting industry. Lorem Ipsum is simply dummy.
          </p>
        </motion.div>

        <motion.button
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6, duration: 0.6 }}
          onClick={() => setCurrentScreen('onboarding')}
          className="get-started-btn"
        >
          Get Started
        </motion.button>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1, duration: 0.8 }}
          className="login-toggle"
        >
          <div className="toggle-container">
            <div className="toggle-check">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M20 6L9 17L4 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <div className="toggle-arrows">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M15 18L9 12L15 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M9 6L15 12L9 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
          </div>
          <p className="login-text">
            Already have account? <span className="login-link">Logi</span>
          </p>
        </motion.div>
      </motion.div>
    </div>
  );

  // Onboarding Screen Component
  const OnboardingScreen = () => (
    <div className="onboarding-screen">
      <AnimatePresence mode="wait">
        <motion.div
          key={currentStep}
          initial={{ opacity: 0, x: 300 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -300 }}
          transition={{ duration: 0.5, ease: 'easeInOut' }}
          className="onboarding-content"
        >
          {/* Header with Progress */}
          <div className="onboarding-header">
            <div className="progress-dots">
              {onboardingSteps.map((_, index) => (
                <motion.div
                  key={index}
                  className={`progress-dot ${index <= currentStep ? 'active' : ''}`}
                  initial={{ scale: 0.8 }}
                  animate={{ scale: index === currentStep ? 1.2 : 1 }}
                  transition={{ duration: 0.3 }}
                />
              ))}
            </div>
            <button
              onClick={() => window.location.href = '/login'}
              className="skip-button"
            >
              تخطي
            </button>
          </div>

          {/* Main Content */}
          <div className="onboarding-main">
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.2, duration: 0.6 }}
              className="onboarding-visual"
            >
              <div className="visual-container">
                <motion.div
                  animate={{
                    y: [0, -10, 0],
                    rotate: [0, 5, -5, 0]
                  }}
                  transition={{
                    duration: 3,
                    repeat: Infinity,
                    ease: 'easeInOut'
                  }}
                  className="main-visual"
                >
                  {onboardingSteps[currentStep].image}
                </motion.div>
                <div className="visual-effects">
                  <div className="glow-effect"></div>
                  <div className="particle particle-1"></div>
                  <div className="particle particle-2"></div>
                  <div className="particle particle-3"></div>
                </div>
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 30 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4, duration: 0.6 }}
              className="onboarding-text"
            >
              <h2 className="onboarding-title">
                {onboardingSteps[currentStep].title}
              </h2>
              <p className="onboarding-subtitle">
                {onboardingSteps[currentStep].subtitle}
              </p>

              <div className="features-list">
                {onboardingSteps[currentStep].features.map((feature, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.6 + index * 0.1, duration: 0.4 }}
                    className="feature-item"
                  >
                    <Star className="w-4 h-4 text-yellow-400" />
                    <span>{feature}</span>
                  </motion.div>
                ))}
              </div>
            </motion.div>
          </div>

          {/* Navigation */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8, duration: 0.6 }}
            className="onboarding-navigation"
          >
            {currentStep === onboardingSteps.length - 1 ? (
              // Last step - Swipe button
              <div className="swipe-container">
                <p className="swipe-text">اسحب لليمين للبدء</p>
                <div
                  className="swipe-button"
                  onMouseDown={handleSwipeStart}
                  onMouseMove={handleSwipeMove}
                  onMouseUp={handleSwipeEnd}
                  onTouchStart={handleSwipeStart}
                  onTouchMove={handleSwipeMove}
                  onTouchEnd={handleSwipeEnd}
                >
                  <motion.div
                    className="swipe-knob"
                    animate={{ x: swipeProgress * 200 }}
                    transition={{ duration: 0.1 }}
                  >
                    <ChevronRight className="w-6 h-6" />
                  </motion.div>
                  <div className="swipe-track">
                    <div
                      className="swipe-progress"
                      style={{ width: `${swipeProgress * 100}%` }}
                    ></div>
                  </div>
                </div>
              </div>
            ) : (
              // Regular next button
              <button
                onClick={handleNext}
                className="next-button"
              >
                التالي
                <ChevronRight className="w-5 h-5" />
              </button>
            )}

            {/* Step indicators */}
            <div className="step-indicators">
              <span className="current-step">
                {currentStep + 1} / {onboardingSteps.length}
              </span>
            </div>
          </motion.div>
        </motion.div>
      </AnimatePresence>
    </div>
  );

  return (
    <div className="onboarding-container">
      {currentScreen === 'splash' ? <SplashScreen /> : <OnboardingScreen />}
    </div>
  );
};

export default OnboardingPage;
