/**
 * ============================================
 * 🎬 Splash Screen - Premium Animated
 * ============================================
 */

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

const SplashScreen = ({ onComplete }) => {
  const navigate = useNavigate();
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    // Simulate loading progress
    const interval = setInterval(() => {
      setProgress(prev => {
        if (prev >= 90) {
          clearInterval(interval);
          return prev;
        }
        return prev + Math.random() * 30;
      });
    }, 300);

    // Navigate after 3 seconds
    const timer = setTimeout(() => {
      setProgress(100);
      setTimeout(() => {
        onComplete?.();
        navigate('/onboarding');
      }, 300);
    }, 3000);

    return () => {
      clearInterval(interval);
      clearTimeout(timer);
    };
  }, [navigate, onComplete]);

  // Logo animation variants
  const containerVariants = {
    initial: { opacity: 0 },
    animate: { opacity: 1, transition: { duration: 0.5 } }
  };

  const logoVariants = {
    initial: { scale: 0.8, opacity: 0 },
    animate: {
      scale: 1,
      opacity: 1,
      transition: { duration: 0.8, type: 'spring', stiffness: 100 }
    },
    pulse: {
      scale: [1, 1.05, 1],
      transition: { duration: 2, repeat: Infinity }
    }
  };

  const textVariants = {
    initial: { opacity: 0, y: 10 },
    animate: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.6, delay: 0.3 }
    }
  };

  const glowVariants = {
    animate: {
      opacity: [0.5, 1, 0.5],
      transition: { duration: 2, repeat: Infinity }
    }
  };

  return (
    <motion.div
      variants={containerVariants}
      initial="initial"
      animate="animate"
      className="fixed inset-0 bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 flex flex-col items-center justify-center overflow-hidden"
    >
      {/* Background animated elements */}
      <motion.div
        className="absolute inset-0 opacity-20"
        animate={{
          backgroundPosition: ['0% 0%', '100% 100%'],
        }}
        transition={{ duration: 20, repeat: Infinity }}
        style={{
          backgroundImage: 'radial-gradient(circle, rgba(59,130,246,0.1) 1px, transparent 1px)',
          backgroundSize: '50px 50px',
        }}
      />

      {/* Floating orbs */}
      <motion.div
        className="absolute w-96 h-96 bg-blue-500 rounded-full opacity-20 blur-3xl"
        animate={{
          y: [0, -50, 0],
          x: [0, 50, 0],
        }}
        transition={{ duration: 6, repeat: Infinity }}
        style={{ top: '10%', left: '-10%' }}
      />
      <motion.div
        className="absolute w-96 h-96 bg-purple-500 rounded-full opacity-20 blur-3xl"
        animate={{
          y: [0, 50, 0],
          x: [0, -50, 0],
        }}
        transition={{ duration: 8, repeat: Infinity }}
        style={{ bottom: '10%', right: '-10%' }}
      />

      {/* Main content */}
      <div className="relative z-10 flex flex-col items-center justify-center">
        {/* Glow background */}
        <motion.div
          variants={glowVariants}
          animate="animate"
          className="absolute w-64 h-64 bg-blue-500 rounded-full blur-3xl opacity-0"
        />

        {/* Logo */}
        <motion.div
          variants={logoVariants}
          initial="initial"
          animate={['animate', 'pulse']}
          className="mb-8 relative"
        >
          <div className="w-24 h-24 bg-gradient-to-br from-blue-400 to-blue-600 rounded-2xl flex items-center justify-center shadow-2xl">
            <svg
              className="w-16 h-16 text-white"
              fill="currentColor"
              viewBox="0 0 24 24"
            >
              <path d="M13 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V9z" />
              <polyline points="13 2 13 9 20 9" />
              <path d="M9 13h6M9 17h6" />
            </svg>
          </div>
        </motion.div>

        {/* Brand name */}
        <motion.div variants={textVariants} initial="initial" animate="animate">
          <h1 className="text-4xl font-bold text-white mb-2 text-center">EDHAM</h1>
          <p className="text-blue-300 text-center text-sm tracking-widest">
            LOGISTICS PLATFORM
          </p>
        </motion.div>

        {/* Tagline */}
        <motion.p
          variants={textVariants}
          initial="initial"
          animate="animate"
          transition={{ delay: 0.5 }}
          className="text-gray-400 text-center mt-6 text-sm max-w-xs"
        >
          Enterprise logistics management redefined
        </motion.p>

        {/* Loading indicator */}
        <motion.div
          className="mt-12 w-64 h-1 bg-gray-700 rounded-full overflow-hidden"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
        >
          <motion.div
            className="h-full bg-gradient-to-r from-blue-400 to-blue-600 rounded-full"
            animate={{ width: `${progress}%` }}
            transition={{ duration: 0.3 }}
          />
        </motion.div>

        {/* Loading text */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.8 }}
          className="text-gray-500 text-xs mt-3"
        >
          Loading...
        </motion.p>
      </div>

      {/* Floating particles */}
      {[...Array(20)].map((_, i) => (
        <motion.div
          key={i}
          className="absolute w-1 h-1 bg-blue-400 rounded-full"
          animate={{
            y: [0, -100],
            opacity: [1, 0],
            x: Math.cos(i) * 100,
          }}
          transition={{
            duration: 3 + Math.random() * 2,
            repeat: Infinity,
            delay: Math.random() * 2,
          }}
          style={{
            top: '50%',
            left: '50%',
          }}
        />
      ))}
    </motion.div>
  );
};

export default SplashScreen;
