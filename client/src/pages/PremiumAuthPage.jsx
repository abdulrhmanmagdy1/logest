/**
 * ============================================
 * 🔐 Premium Authentication - Auto Role Detection
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Mail, Lock, Eye, EyeOff, ArrowRight, Loader } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const PremiumAuthPage = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isNewUser, setIsNewUser] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    if (!email || !password) {
      setError('Please enter email and password');
      return;
    }

    try {
      setLoading(true);
      const apiBase = process.env.REACT_APP_API_URL || 'http://localhost:5000/api/v1';
      const response = await axios.post(`${apiBase}/auth/login`, {
        email,
        password
      });

      if (response.data.success) {
        const { token, user } = response.data;
        
        // Store token
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));

        // Auto-route based on role
        const dashboardRoutes = {
          'supervisor': '/supervisor-dashboard',
          'driver': '/driver-dashboard',
          'accountant': '/accountant-dashboard',
          'admin': '/admin-dashboard',
          'fleet_manager': '/fleet-manager-dashboard',
          'workshop': '/workshop-dashboard',
          'client': '/customer-dashboard'
        };

        const redirectTo = dashboardRoutes[user.role] || '/dashboard';
        
        // Animate success
        motion.div({ opacity: 1 });
        setTimeout(() => {
          navigate(redirectTo);
        }, 500);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const containerVariants = {
    initial: { opacity: 0 },
    animate: { opacity: 1, transition: { duration: 0.5 } }
  };

  const cardVariants = {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0, transition: { delay: 0.2 } }
  };

  return (
    <motion.div
      variants={containerVariants}
      initial="initial"
      animate="animate"
      className="min-h-screen bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 flex items-center justify-center px-6 py-12"
    >
      {/* Background elements */}
      <motion.div
        className="absolute top-20 left-10 w-96 h-96 bg-blue-500 rounded-full opacity-10 blur-3xl"
        animate={{ y: [0, -50, 0] }}
        transition={{ duration: 10, repeat: Infinity }}
      />
      <motion.div
        className="absolute bottom-20 right-10 w-96 h-96 bg-purple-500 rounded-full opacity-10 blur-3xl"
        animate={{ y: [0, 50, 0] }}
        transition={{ duration: 12, repeat: Infinity }}
      />

      <div className="w-full max-w-md">
        {/* Main card */}
        <motion.div
          variants={cardVariants}
          initial="initial"
          animate="animate"
          className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-8 shadow-2xl relative z-10"
        >
          {/* Logo section */}
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ delay: 0.3 }}
            className="mb-8 text-center"
          >
            <div className="w-16 h-16 bg-gradient-to-br from-blue-400 to-cyan-500 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
              <svg className="w-10 h-10 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M13 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V9z" />
                <polyline points="13 2 13 9 20 9" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-white">EDHAM</h1>
            <p className="text-gray-400 text-sm mt-1">Logistics Platform</p>
          </motion.div>

          {/* Header */}
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="mb-8"
          >
            <h2 className="text-2xl font-bold text-white mb-1">Welcome Back</h2>
            <p className="text-gray-400 text-sm">Sign in to your account</p>
          </motion.div>

          {/* Error message */}
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-300 text-sm"
            >
              {error}
            </motion.div>
          )}

          {/* Form */}
          <form onSubmit={handleLogin} className="space-y-5">
            {/* Email field */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.5 }}
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Email Address
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@company.com"
                  className="w-full pl-10 pr-4 py-2.5 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                />
              </div>
            </motion.div>

            {/* Password field */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.6 }}
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Password
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-10 pr-10 py-2.5 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300 transition-colors"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </motion.div>

            {/* Remember me & Forgot password */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.7 }}
              className="flex items-center justify-between text-sm"
            >
              <label className="flex items-center gap-2 text-gray-300 cursor-pointer hover:text-white transition-colors">
                <input type="checkbox" className="w-4 h-4 rounded" />
                Remember me
              </label>
              <button
                type="button"
                className="text-blue-400 hover:text-blue-300 transition-colors"
              >
                Forgot password?
              </button>
            </motion.div>

            {/* Sign in button */}
            <motion.button
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.8 }}
              type="submit"
              disabled={loading}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="w-full py-2.5 mt-6 bg-gradient-to-r from-blue-500 to-cyan-500 text-white rounded-lg font-semibold flex items-center justify-center gap-2 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              {loading ? (
                <>
                  <Loader className="w-5 h-5 animate-spin" />
                  Signing in...
                </>
              ) : (
                <>
                  Sign In
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </motion.button>
          </form>

          {/* Divider */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.9 }}
            className="my-6 flex items-center gap-3"
          >
            <div className="flex-1 h-px bg-white/10" />
            <span className="text-gray-500 text-xs">OR</span>
            <div className="flex-1 h-px bg-white/10" />
          </motion.div>

          {/* Sign up link */}
          <motion.button
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1 }}
            onClick={() => navigate('/register')}
            className="w-full py-2.5 border border-white/20 text-white rounded-lg font-semibold hover:bg-white/10 transition-all"
          >
            Create New Account
          </motion.button>

          {/* Footer text */}
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.1 }}
            className="text-center text-gray-500 text-xs mt-6"
          >
            Protected by SSL encryption • Secure login
          </motion.p>
        </motion.div>

        {/* Info box */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1.2 }}
          className="mt-6 bg-white/5 border border-white/10 rounded-lg p-4 text-center text-gray-400 text-sm"
        >
          <p>
            <strong className="text-white">Employee Login:</strong> Your role is automatically detected
          </p>
        </motion.div>
      </div>
    </motion.div>
  );
};

export default PremiumAuthPage;
