/**
 * ============================================
 * 📝 Premium Customer Registration Form
 * ============================================
 */

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Eye, EyeOff, Mail, User, Phone, Lock, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const CustomerRegistrationForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: ''
  });

  const [validations, setValidations] = useState({
    firstName: false,
    lastName: false,
    email: false,
    phone: false,
    password: false,
    passwordMatch: false
  });

  const validateEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const validatePhone = (phone) => {
    return /^\d{7,}$/.test(phone.replace(/\D/g, ''));
  };

  const validatePassword = (password) => {
    return password.length >= 8;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Real-time validation
    if (name === 'firstName') {
      setValidations(prev => ({ ...prev, firstName: value.length >= 2 }));
    } else if (name === 'lastName') {
      setValidations(prev => ({ ...prev, lastName: value.length >= 2 }));
    } else if (name === 'email') {
      setValidations(prev => ({ ...prev, email: validateEmail(value) }));
    } else if (name === 'phone') {
      setValidations(prev => ({ ...prev, phone: validatePhone(value) }));
    } else if (name === 'password') {
      setValidations(prev => ({
        ...prev,
        password: validatePassword(value),
        passwordMatch: value === formData.confirmPassword && value.length > 0
      }));
    } else if (name === 'confirmPassword') {
      setValidations(prev => ({
        ...prev,
        passwordMatch: value === formData.password && value.length > 0
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validate all fields
    if (!validations.firstName || !validations.lastName || !validations.email || 
        !validations.phone || !validations.password || !validations.passwordMatch) {
      setError('Please fill all fields correctly');
      return;
    }

    try {
      setLoading(true);
      const apiBase = process.env.REACT_APP_API_URL || 'http://localhost:5000/api/v1';
      const response = await axios.post(`${apiBase}/auth/register`, {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        password: formData.password,
        role: 'client'
      });

      if (response.data.success) {
        setSuccess('Registration successful! Redirecting to dashboard...');
        localStorage.setItem('token', response.data.token);
        setTimeout(() => {
          navigate('/customer-dashboard');
        }, 2000);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const containerVariants = {
    initial: { opacity: 0 },
    animate: { opacity: 1, transition: { duration: 0.5 } }
  };

  const formVariants = {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0, transition: { delay: 0.2 } }
  };

  const inputVariants = (index) => ({
    initial: { opacity: 0, x: -20 },
    animate: { opacity: 1, x: 0, transition: { delay: 0.3 + index * 0.1 } }
  });

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

      <motion.div
        variants={formVariants}
        initial="initial"
        animate="animate"
        className="relative z-10 w-full max-w-md"
      >
        {/* Card */}
        <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-8 shadow-2xl">
          {/* Header */}
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="mb-8"
          >
            <h1 className="text-3xl font-bold text-white mb-2">Create Account</h1>
            <p className="text-gray-400">Join thousands of logistics companies</p>
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

          {/* Success message */}
          {success && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="mb-6 p-4 bg-green-500/20 border border-green-500/50 rounded-lg text-green-300 text-sm"
            >
              {success}
            </motion.div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Name fields */}
            <div className="grid grid-cols-2 gap-4">
              <motion.div
                variants={inputVariants(0)}
                initial="initial"
                animate="animate"
              >
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  First Name *
                </label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    placeholder="First"
                    className={`w-full pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                      validations.firstName ? 'border-green-500' : ''
                    }`}
                  />
                </div>
              </motion.div>

              <motion.div
                variants={inputVariants(1)}
                initial="initial"
                animate="animate"
              >
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Last Name *
                </label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    placeholder="Last"
                    className={`w-full pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                      validations.lastName ? 'border-green-500' : ''
                    }`}
                  />
                </div>
              </motion.div>
            </div>

            {/* Email field */}
            <motion.div
              variants={inputVariants(2)}
              initial="initial"
              animate="animate"
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Email Address *
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="you@company.com"
                  className={`w-full pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                    validations.email ? 'border-green-500' : ''
                  }`}
                />
              </div>
            </motion.div>

            {/* Phone field */}
            <motion.div
              variants={inputVariants(3)}
              initial="initial"
              animate="animate"
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Phone Number *
              </label>
              <div className="relative">
                <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                  placeholder="+966 50 xxx xxxx"
                  className={`w-full pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                    validations.phone ? 'border-green-500' : ''
                  }`}
                />
              </div>
            </motion.div>

            {/* Password field */}
            <motion.div
              variants={inputVariants(4)}
              initial="initial"
              animate="animate"
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Password *
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder="••••••••"
                  className={`w-full pl-10 pr-10 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                    validations.password ? 'border-green-500' : ''
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              <p className="text-xs text-gray-500 mt-1">Minimum 8 characters</p>
            </motion.div>

            {/* Confirm password field */}
            <motion.div
              variants={inputVariants(5)}
              initial="initial"
              animate="animate"
            >
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Confirm Password *
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type={showConfirm ? 'text' : 'password'}
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  placeholder="••••••••"
                  className={`w-full pl-10 pr-10 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all ${
                    validations.passwordMatch && formData.confirmPassword ? 'border-green-500' : ''
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirm(!showConfirm)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
                >
                  {showConfirm ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </motion.div>

            {/* Submit button */}
            <motion.button
              variants={inputVariants(6)}
              initial="initial"
              animate="animate"
              type="submit"
              disabled={loading || !validations.firstName || !validations.lastName || 
                       !validations.email || !validations.phone || !validations.password || 
                       !validations.passwordMatch}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="w-full py-3 mt-6 bg-gradient-to-r from-blue-500 to-cyan-500 text-white rounded-lg font-semibold flex items-center justify-center gap-2 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            >
              {loading ? 'Creating Account...' : 'Create Account'}
              <ArrowRight className="w-5 h-5" />
            </motion.button>
          </form>

          {/* Footer */}
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1 }}
            className="text-center text-gray-400 text-sm mt-6"
          >
            Already have an account?{' '}
            <button
              onClick={() => navigate('/auth')}
              className="text-blue-400 hover:text-blue-300 font-semibold"
            >
              Sign In
            </button>
          </motion.p>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default CustomerRegistrationForm;
