/**
 * ============================================
 * 🧾 Client Invoices Page - نظام إدهام
 * Edham Logistics - Client Invoice Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FileText, Search, Download, CheckCircle, DollarSign, Calendar,
  ChevronDown, RefreshCw, Eye, CreditCard, X
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

const STATUS_CONFIG = {
  draft:     { label: "مسودة",     color: "bg-gray-500/20 text-gray-400 border-gray-500/30" },
  sent:      { label: "مرسلة",     color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  paid:      { label: "مدفوعة",    color: "bg-green-500/20 text-green-400 border-green-500/30" },
  overdue:   { label: "متأخرة",    color: "bg-red-500/20 text-red-400 border-red-500/30" },
};

const ClientInvoicesPage = () => {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [stats, setStats] = useState({ total: 0, paid: 0, pending: 0 });
  const [selectedInvoice, setSelectedInvoice] = useState(null);

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });
      const res = await api.get(`/invoices/my-invoices?${params}`);
      setInvoices(res.data.data?.invoices || []);
      setStats(res.data.data?.stats || { total: 0, paid: 0, pending: 0 });
    } catch (err) {
      logger.error("خطأ في جلب الفواتير:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, [statusFilter, searchQuery]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">فواتيري</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة فواتيرك والمدفوعات</p>
        </div>
        <button
          onClick={fetchInvoices}
          className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
          disabled={loading}
        >
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        <div className="card">
          <p className="text-2xl font-bold text-edham-white">{stats.total}</p>
          <p className="text-xs text-edham-text-muted">إجمالي الفواتير</p>
        </div>
        <div className="card">
          <p className="text-2xl font-bold text-green-400">{stats.paid}</p>
          <p className="text-xs text-edham-text-muted">مدفوعة</p>
        </div>
        <div className="card">
          <p className="text-2xl font-bold text-yellow-400">{stats.pending}</p>
          <p className="text-xs text-edham-text-muted">معلقة</p>
        </div>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث برقم الفاتورة..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
            />
          </div>
          <div className="relative">
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-4 pl-8 py-2.5 text-edham-white outline-none focus:border-edham-primary cursor-pointer min-w-[140px]"
            >
              <option value="all">جميع الحالات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Invoices List */}
      <div className="space-y-4">
        {loading ? (
          [...Array(4)].map((_, i) => (
            <div key={i} className="card"><div className="skeleton h-20 rounded-xl" /></div>
          ))
        ) : invoices.length === 0 ? (
          <div className="card py-16 text-center">
            <FileText className="w-12 h-12 text-edham-text-muted mx-auto mb-3" />
            <p className="text-edham-text-muted">لا توجد فواتير</p>
          </div>
        ) : (
          invoices.map((invoice) => (
            <motion.div
              key={invoice._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="card hover-lift"
            >
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                  <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                    invoice.status === "paid"
                      ? "bg-green-500/20 text-green-400"
                      : "bg-edham-primary/20 text-edham-primary"
                  }`}>
                    <FileText className="w-6 h-6" />
                  </div>
                  <div>
                    <h4 className="text-lg font-bold text-edham-white">فاتورة #{invoice.invoiceNumber}</h4>
                    <div className="flex items-center gap-3 text-sm">
                      <span className="text-edham-text-muted">
                        {new Date(invoice.issueDate).toLocaleDateString("ar-EG")}
                      </span>
                      <span className={`px-2 py-0.5 rounded text-xs font-medium border ${
                        STATUS_CONFIG[invoice.status]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
                      }`}>
                        {STATUS_CONFIG[invoice.status]?.label || invoice.status}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <p className="text-edham-primary font-bold text-xl">{invoice.total?.toLocaleString()} ج.م</p>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setSelectedInvoice(invoice)}
                      className="btn-icon text-edham-text-muted hover:text-edham-primary"
                    >
                      <Eye className="w-4 h-4" />
                    </button>
                    <button className="btn-icon text-edham-text-muted hover:text-edham-primary">
                      <Download className="w-4 h-4" />
                    </button>
                    {invoice.status !== "paid" && (
                      <button className="btn-primary text-sm">
                        <CreditCard className="w-4 h-4" />
                        <span>دفع</span>
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>

      {/* Detail Modal */}
      {selectedInvoice && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4"
             onClick={() => setSelectedInvoice(null)}>
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-edham-dark border border-edham-gray rounded-2xl shadow-2xl w-full max-w-lg"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between p-5 border-b border-edham-gray">
              <h3 className="text-lg font-bold text-edham-white">فاتورة #{selectedInvoice.invoiceNumber}</h3>
              <button onClick={() => setSelectedInvoice(null)} className="btn-icon text-edham-text-muted hover:text-edham-white">
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div className="bg-edham-black/50 rounded-xl p-4">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-edham-text-muted text-sm">المجموع الفرعي</span>
                  <span className="text-edham-white">{selectedInvoice.subtotal?.toLocaleString()} ج.م</span>
                </div>
                <div className="flex justify-between items-center mb-2">
                  <span className="text-edham-text-muted text-sm">الضريبة</span>
                  <span className="text-edham-white">{selectedInvoice.taxAmount?.toLocaleString()} ج.م</span>
                </div>
                <div className="flex justify-between items-center pt-2 border-t border-edham-gray">
                  <span className="text-edham-white font-semibold">الإجمالي</span>
                  <span className="text-edham-primary font-bold text-xl">{selectedInvoice.total?.toLocaleString()} ج.م</span>
                </div>
              </div>
              {selectedInvoice.status === "paid" ? (
                <div className="bg-green-500/10 border border-green-500/30 rounded-xl p-3">
                  <div className="flex items-center gap-2 text-green-400">
                    <CheckCircle className="w-5 h-5" />
                    <span>تم الدفع بنجاح</span>
                  </div>
                </div>
              ) : (
                <button className="btn-primary w-full">
                  <CreditCard className="w-4 h-4" />
                  <span>الدفع الآن</span>
                </button>
              )}
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default ClientInvoicesPage;
