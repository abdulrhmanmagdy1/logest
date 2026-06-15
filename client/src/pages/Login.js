/**
 * ============================================
 * 🔐 Login Page - نظام إدهام
 * Edham Logistics - Authentication
 * ============================================
 */

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { Eye, EyeOff, Truck, Mail, Lock, AlertCircle } from "lucide-react";
import { useAuth } from "../context/AuthContext";

const Login = () => {
  const navigate = useNavigate();
  const { login, isLoading } = useAuth();

  const [formData, setFormData] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.email || !formData.password) {
      setError("يرجى ملء جميع الحقول");
      return;
    }

    const result = await login(formData.email, formData.password);
    if (result.success) {
      navigate("/dashboard");
    } else {
      setError(result.message);
    }
  };

  return (
    <div className="min-h-screen bg-edham-dark flex items-center justify-center p-4">
      {/* خلفية */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 right-20 w-96 h-96 bg-edham-primary/5 rounded-full blur-3xl" />
        <div className="absolute bottom-20 left-20 w-80 h-80 bg-edham-primary-dark/5 rounded-full blur-3xl" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md relative z-10"
      >
        {/* Header */}
        <div className="text-center mb-8">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: "spring" }}
            className="inline-flex items-center justify-center w-16 h-16
                       bg-edham-primary rounded-2xl mb-4 shadow-edham"
          >
            <Truck className="w-8 h-8 text-white" />
          </motion.div>
          <h1 className="text-3xl font-black text-edham-white mb-1">
            <span className="text-gradient">إدهام</span>
          </h1>
          <p className="text-edham-text-muted text-sm">نظام النقل المبرد المتكامل</p>
        </div>

        {/* Card */}
        <div className="card-dark border border-edham-gray rounded-2xl p-8">
          <h2 className="text-xl font-bold text-edham-white mb-6 text-center">
            تسجيل الدخول
          </h2>

          {/* Error */}
          {error && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="alert-error mb-4"
            >
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span className="text-sm">{error}</span>
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* البريد الإلكتروني */}
            <div className="input-group">
              <label className="input-label">البريد الإلكتروني</label>
              <div className="relative">
                <Mail className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="example@email.com"
                  className="input-field pr-10"
                  autoComplete="email"
                  dir="ltr"
                />
              </div>
            </div>

            {/* كلمة المرور */}
            <div className="input-group">
              <label className="input-label">كلمة المرور</label>
              <div className="relative">
                <Lock className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  className="input-field pr-10 pl-10"
                  autoComplete="current-password"
                  dir="ltr"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-edham-text-muted
                             hover:text-edham-text-secondary transition-colors"
                >
                  {showPassword ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>

            {/* زر الدخول */}
            <button
              type="submit"
              disabled={isLoading}
              className="btn-primary w-full mt-6"
            >
              {isLoading ? (
                <>
                  <div className="spinner-sm" />
                  <span>جاري الدخول...</span>
                </>
              ) : (
                <span>دخول</span>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="mt-6 text-center">
            <p className="text-edham-text-muted text-sm">
              ليس لديك حساب؟{" "}
              <Link to="/register" className="text-edham-primary hover:text-edham-primary-light font-medium transition-colors">
                إنشاء حساب
              </Link>
            </p>
          </div>

          {/* Demo Accounts */}
          <div className="mt-6 pt-6 border-t border-edham-gray">
            <p className="text-xs text-edham-text-muted text-center mb-3">حسابات تجريبية</p>
            <div className="grid grid-cols-2 gap-2">
              {[
                { role: "مشرف",   email: "supervisor@edham.com",  color: "bg-blue-900/50 text-blue-400" },
                { role: "محاسب",  email: "accountant@edham.com",  color: "bg-green-900/50 text-green-400" },
                { role: "سائق",   email: "driver@edham.com",      color: "bg-orange-900/50 text-orange-400" },
                { role: "عميل",   email: "client@edham.com",      color: "bg-purple-900/50 text-purple-400" },
              ].map((acc) => (
                <button
                  key={acc.role}
                  type="button"
                  onClick={() =>
                    setFormData({ email: acc.email, password: "password123" })
                  }
                  className={`text-xs px-3 py-2 rounded-lg transition-all ${acc.color}
                              hover:opacity-80 text-right`}
                >
                  {acc.role}
                </button>
              ))}
            </div>
          </div>
        </div>

        <p className="text-center text-edham-text-muted text-xs mt-6">
          © 2024 إدهام للنقل المبرد - جميع الحقوق محفوظة
        </p>
      </motion.div>
    </div>
  );
};

export default Login;
