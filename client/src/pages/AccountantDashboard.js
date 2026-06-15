/**
 * ============================================
 * 💰 Accountant Dashboard - نظام إدهام
 * Edham Logistics - Financial Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FileText, DollarSign, TrendingUp, AlertCircle,
  CheckCircle, Clock, Plus, Search, Download,
  CreditCard, BarChart2, LogOut, Truck,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const AccountantDashboard = () => {
  const { user, logout } = useAuth();
  const { showToast } = useNotification();
  const navigate = useNavigate();

  const [invoices, setInvoices] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("invoices");
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [paymentData, setPaymentData] = useState({
    amount: "",
    method: "cash",
    reference: "",
    note: "",
  });

  useEffect(() => {
    fetchData();
  }, [statusFilter]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      if (statusFilter) params.append("status", statusFilter);
      if (searchTerm) params.append("search", searchTerm);

      const [invoicesRes, statsRes] = await Promise.all([
        api.get(`/invoices?${params.toString()}&limit=20`),
        api.get("/analytics/dashboard"),
      ]);

      setInvoices(invoicesRes.data.data.invoices);
      setStats(invoicesRes.data.data.stats);
    } catch {
      showToast("خطأ", "فشل في جلب البيانات", "error");
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async () => {
    if (!paymentData.amount || !paymentData.method) {
      showToast("خطأ", "يرجى إدخال المبلغ وطريقة الدفع", "error");
      return;
    }

    try {
      await api.post(`/invoices/${selectedInvoice._id}/payment`, {
        amount: parseFloat(paymentData.amount),
        method: paymentData.method,
        reference: paymentData.reference,
        note: paymentData.note,
      });

      showToast("✅ تم", `تم تسجيل دفعة ${paymentData.amount} ج.م`, "success");
      setShowPaymentModal(false);
      setSelectedInvoice(null);
      setPaymentData({ amount: "", method: "cash", reference: "", note: "" });
      fetchData();
    } catch (err) {
      showToast("خطأ", err.response?.data?.message || "فشل تسجيل الدفعة", "error");
    }
  };

  const statusConfig = {
    draft:     { label: "مسودة",   class: "badge-gray" },
    pending:   { label: "معلقة",   class: "badge-warning" },
    partial:   { label: "جزئية",  class: "badge-info" },
    paid:      { label: "مدفوعة", class: "badge-success" },
    overdue:   { label: "متأخرة", class: "badge-danger" },
    cancelled: { label: "ملغية",  class: "badge-gray" },
  };

  const paymentMethods = [
    { value: "cash",          label: "نقداً" },
    { value: "card",          label: "بطاقة" },
    { value: "bank_transfer", label: "تحويل بنكي" },
    { value: "check",         label: "شيك" },
    { value: "stripe",        label: "Stripe" },
  ];

  const filteredInvoices = invoices.filter((inv) => {
    if (!searchTerm) return true;
    return (
      inv.invoiceNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      inv.client?.name?.toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Navbar */}
      <header className="navbar">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-red-600 rounded-lg flex items-center justify-center">
            <Truck className="w-4 h-4 text-white" />
          </div>
          <div>
            <span className="font-black text-gradient text-sm">إدهام</span>
            <span className="text-gray-500 text-xs mr-2">| محاسب</span>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <p className="text-sm text-white hidden sm:block">{user?.name}</p>
          <button
            onClick={() => { logout(); navigate("/login"); }}
            className="btn-icon text-gray-400 hover:text-red-400"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </header>

      <main className="pt-16 p-4 md:p-6 max-w-6xl mx-auto space-y-6">

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[
            {
              label: "إجمالي الإيرادات",
              value: `${(stats?.totalRevenue || 0).toLocaleString()} ج.م`,
              icon: TrendingUp,
              color: "bg-green-600/20 text-green-400",
            },
            {
              label: "المحصّل",
              value: `${(stats?.totalPaid || 0).toLocaleString()} ج.م`,
              icon: CheckCircle,
              color: "bg-blue-600/20 text-blue-400",
            },
            {
              label: "المتبقي",
              value: `${(stats?.totalPending || 0).toLocaleString()} ج.م`,
              icon: AlertCircle,
              color: "bg-red-600/20 text-red-400",
            },
            {
              label: "عدد الفواتير",
              value: stats?.count || 0,
              icon: FileText,
              color: "bg-purple-600/20 text-purple-400",
            },
          ].map((stat, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
              className="card"
            >
              <div className={`inline-flex items-center justify-center w-10 h-10
                              rounded-xl mb-3 ${stat.color}`}>
                <stat.icon className="w-5 h-5" />
              </div>
              <div className="text-xl font-bold text-white">{stat.value}</div>
              <div className="text-xs text-gray-400 mt-1">{stat.label}</div>
            </motion.div>
          ))}
        </div>

        {/* Controls */}
        <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && fetchData()}
              placeholder="بحث برقم الفاتورة أو اسم العميل..."
              className="input-field pr-10"
            />
          </div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="input-field sm:w-48"
          >
            <option value="">كل الحالات</option>
            <option value="pending">معلقة</option>
            <option value="partial">جزئية</option>
            <option value="paid">مدفوعة</option>
            <option value="overdue">متأخرة</option>
          </select>
        </div>

        {/* Invoice Table */}
        <div className="card p-0 overflow-hidden">
          <div className="p-4 border-b border-gray-700">
            <h3 className="text-lg font-semibold text-white">الفواتير</h3>
          </div>

          {loading ? (
            <div className="p-6 space-y-3">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="skeleton h-12 rounded-xl" />
              ))}
            </div>
          ) : (
            <div className="table-container rounded-none border-0">
              <table className="table">
                <thead>
                  <tr>
                    <th>رقم الفاتورة</th>
                    <th>العميل</th>
                    <th>الإجمالي</th>
                    <th>المدفوع</th>
                    <th>المتبقي</th>
                    <th>الحالة</th>
                    <th>إجراء</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredInvoices.length === 0 ? (
                    <tr>
                      <td colSpan={7} className="text-center py-10 text-gray-500">
                        لا توجد فواتير
                      </td>
                    </tr>
                  ) : (
                    filteredInvoices.map((invoice) => (
                      <tr key={invoice._id}>
                        <td>
                          <span className="font-mono text-xs text-red-400 font-bold">
                            {invoice.invoiceNumber}
                          </span>
                        </td>
                        <td className="text-white">
                          {invoice.client?.companyName || invoice.client?.name || "—"}
                        </td>
                        <td className="text-white font-medium">
                          {invoice.total?.toLocaleString()} ج.م
                        </td>
                        <td className="text-green-400">
                          {invoice.amountPaid?.toLocaleString()} ج.م
                        </td>
                        <td className={invoice.balanceDue > 0 ? "text-red-400" : "text-green-400"}>
                          {invoice.balanceDue?.toLocaleString()} ج.م
                        </td>
                        <td>
                          <span className={statusConfig[invoice.status]?.class || "badge-gray"}>
                            {statusConfig[invoice.status]?.label || invoice.status}
                          </span>
                        </td>
                        <td>
                          {invoice.status !== "paid" && invoice.status !== "cancelled" && (
                            <button
                              onClick={() => {
                                setSelectedInvoice(invoice);
                                setPaymentData({
                                  ...paymentData,
                                  amount: invoice.balanceDue?.toString(),
                                });
                                setShowPaymentModal(true);
                              }}
                              className="btn-primary btn-sm text-xs"
                            >
                              <CreditCard className="w-3 h-3" />
                              تسجيل دفعة
                            </button>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>

      {/* Modal: تسجيل دفعة */}
      {showPaymentModal && selectedInvoice && (
        <div className="modal-overlay">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="modal-content w-full max-w-md"
          >
            <div className="modal-header">
              <div>
                <h3 className="text-lg font-bold text-white">تسجيل دفعة</h3>
                <p className="text-xs text-gray-500 mt-0.5">
                  فاتورة: {selectedInvoice.invoiceNumber}
                </p>
              </div>
              <button
                onClick={() => setShowPaymentModal(false)}
                className="btn-icon text-gray-400 hover:text-white"
              >
                ✕
              </button>
            </div>

            <div className="modal-body space-y-4">
              <div className="p-4 bg-gray-900 rounded-xl border border-gray-800">
                <div className="flex justify-between text-sm mb-2">
                  <span className="text-gray-400">الإجمالي</span>
                  <span className="text-white font-medium">
                    {selectedInvoice.total?.toLocaleString()} ج.م
                  </span>
                </div>
                <div className="flex justify-between text-sm mb-2">
                  <span className="text-gray-400">المدفوع</span>
                  <span className="text-green-400">
                    {selectedInvoice.amountPaid?.toLocaleString()} ج.م
                  </span>
                </div>
                <div className="flex justify-between text-sm font-bold border-t border-gray-700 pt-2">
                  <span className="text-gray-300">المتبقي</span>
                  <span className="text-red-400">
                    {selectedInvoice.balanceDue?.toLocaleString()} ج.م
                  </span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="input-group">
                  <label className="input-label">المبلغ (ج.م)</label>
                  <input
                    type="number"
                    value={paymentData.amount}
                    onChange={(e) => setPaymentData({ ...paymentData, amount: e.target.value })}
                    max={selectedInvoice.balanceDue}
                    min="1"
                    className="input-field"
                    dir="ltr"
                  />
                </div>
                <div className="input-group">
                  <label className="input-label">طريقة الدفع</label>
                  <select
                    value={paymentData.method}
                    onChange={(e) => setPaymentData({ ...paymentData, method: e.target.value })}
                    className="input-field"
                  >
                    {paymentMethods.map((m) => (
                      <option key={m.value} value={m.value}>{m.label}</option>
                    ))}
                  </select>
                </div>
              </div>

              {["bank_transfer", "check"].includes(paymentData.method) && (
                <div className="input-group">
                  <label className="input-label">رقم المرجع</label>
                  <input
                    type="text"
                    value={paymentData.reference}
                    onChange={(e) => setPaymentData({ ...paymentData, reference: e.target.value })}
                    placeholder="رقم التحويل / الشيك"
                    className="input-field"
                    dir="ltr"
                  />
                </div>
              )}

              <div className="input-group">
                <label className="input-label">ملاحظة</label>
                <input
                  type="text"
                  value={paymentData.note}
                  onChange={(e) => setPaymentData({ ...paymentData, note: e.target.value })}
                  placeholder="ملاحظة اختيارية"
                  className="input-field"
                />
              </div>
            </div>

            <div className="modal-footer">
              <button
                onClick={() => setShowPaymentModal(false)}
                className="btn-secondary"
              >
                إلغاء
              </button>
              <button onClick={handlePayment} className="btn-primary">
                <CreditCard className="w-4 h-4" />
                تأكيد الدفعة
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default AccountantDashboard;
