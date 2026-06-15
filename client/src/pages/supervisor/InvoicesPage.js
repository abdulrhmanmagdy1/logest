/**
 * ============================================
 * 🧾 Invoices Page - نظام إدهام
 * Edham Logistics - Invoice Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FileText, Search, Plus, Filter, RefreshCw, Eye, Download, CheckCircle,
  DollarSign, Calendar, User, Package, X, ChevronDown, CreditCard, AlertCircle
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  draft:     { label: "مسودة",     color: "bg-gray-500/20 text-gray-400 border-gray-500/30" },
  sent:      { label: "مرسلة",     color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  paid:      { label: "مدفوعة",    color: "bg-green-500/20 text-green-400 border-green-500/30" },
  overdue:   { label: "متأخرة",    color: "bg-red-500/20 text-red-400 border-red-500/30" },
  cancelled: { label: "ملغاة",     color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
};

// ── Modal Component ───────────────────────
const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;
  return (
    <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4"
         onClick={onClose}>
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        className="bg-edham-dark border border-edham-gray rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between p-5 border-b border-edham-gray">
          <h3 className="text-lg font-bold text-edham-white">{title}</h3>
          <button onClick={onClose} className="btn-icon text-edham-text-muted hover:text-edham-white">
            <X className="w-5 h-5" />
          </button>
        </div>
        <div className="p-5 overflow-y-auto max-h-[70vh]">
          {children}
        </div>
      </motion.div>
    </div>
  );
};

// ── Invoice Detail Modal ──────────────────
const InvoiceDetailModal = ({ invoice, isOpen, onClose, onMarkAsPaid }) => {
  if (!invoice) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`فاتورة #${invoice.invoiceNumber}`}>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-start justify-between bg-edham-black/50 rounded-xl p-4">
          <div>
            <div className="flex items-center gap-3 mb-2">
              <div className="w-14 h-14 bg-edham-primary/20 rounded-2xl flex items-center justify-center">
                <FileText className="w-7 h-7 text-edham-primary" />
              </div>
              <div>
                <h4 className="text-xl font-bold text-edham-white">فاتورة #{invoice.invoiceNumber}</h4>
                <p className="text-edham-text-muted text-sm">
                  تاريخ الإصدار: {new Date(invoice.issueDate).toLocaleDateString("ar-EG")}
                </p>
              </div>
            </div>
          </div>
          <span className={`px-3 py-1.5 rounded-lg text-sm font-medium border ${
            STATUS_CONFIG[invoice.status]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
          }`}>
            {STATUS_CONFIG[invoice.status]?.label || invoice.status}
          </span>
        </div>

        {/* Client Info */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-edham-black/50 rounded-xl p-4">
            <h5 className="text-sm font-semibold text-edham-white mb-3">من</h5>
            <p className="text-edham-white font-medium">إدهام للنقل المبرد</p>
            <p className="text-edham-text-muted text-sm">القاهرة، مصر</p>
            <p className="text-edham-text-muted text-sm">info@edham.com</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <h5 className="text-sm font-semibold text-edham-white mb-3">إلى</h5>
            <p className="text-edham-white font-medium">{invoice.client?.name || "—"}</p>
            <p className="text-edham-text-muted text-sm">{invoice.client?.email || "—"}</p>
            <p className="text-edham-text-muted text-sm">{invoice.client?.phone || "—"}</p>
          </div>
        </div>

        {/* Items Table */}
        <div className="bg-edham-black/50 rounded-xl p-4">
          <h5 className="text-sm font-semibold text-edham-white mb-3">تفاصيل الفاتورة</h5>
          <table className="w-full">
            <thead>
              <tr className="text-edham-text-muted text-xs border-b border-edham-gray">
                <th className="text-right py-2">البيان</th>
                <th className="text-center py-2">الكمية</th>
                <th className="text-center py-2">السعر</th>
                <th className="text-left py-2">المجموع</th>
              </tr>
            </thead>
            <tbody>
              {(invoice.items || []).map((item, index) => (
                <tr key={index} className="text-edham-white text-sm border-b border-edham-gray/30 last:border-0">
                  <td className="py-3">{item.description}</td>
                  <td className="text-center py-3">{item.quantity}</td>
                  <td className="text-center py-3">{item.price?.toLocaleString()} ج.م</td>
                  <td className="text-left py-3">{(item.quantity * item.price)?.toLocaleString()} ج.م</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Totals */}
        <div className="bg-edham-black/50 rounded-xl p-4">
          <div className="space-y-2">
            <div className="flex justify-between text-edham-text-muted text-sm">
              <span>المجموع الفرعي</span>
              <span>{invoice.subtotal?.toLocaleString()} ج.م</span>
            </div>
            <div className="flex justify-between text-edham-text-muted text-sm">
              <span>الضريبة ({invoice.taxRate || 14}%)</span>
              <span>{invoice.taxAmount?.toLocaleString()} ج.م</span>
            </div>
            <div className="flex justify-between text-edham-white font-bold text-lg pt-2 border-t border-edham-gray">
              <span>الإجمالي</span>
              <span className="text-edham-primary">{invoice.total?.toLocaleString()} ج.م</span>
            </div>
          </div>
        </div>

        {/* Payment Info */}
        {invoice.payment && (
          <div className="bg-green-500/10 border border-green-500/30 rounded-xl p-4">
            <div className="flex items-center gap-2 text-green-400 mb-2">
              <CheckCircle className="w-5 h-5" />
              <span className="font-semibold">تم الدفع</span>
            </div>
            <p className="text-edham-text-muted text-sm">
              تاريخ الدفع: {new Date(invoice.payment.date).toLocaleDateString("ar-EG")}
            </p>
            <p className="text-edham-text-muted text-sm">
              طريقة الدفع: {invoice.payment.method}
            </p>
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-3 pt-4 border-t border-edham-gray">
          {invoice.status !== "paid" && invoice.status !== "cancelled" && (
            <button
              onClick={() => onMarkAsPaid(invoice._id)}
              className="btn-primary flex-1"
            >
              <CheckCircle className="w-4 h-4" />
              <span>تأكيد الدفع</span>
            </button>
          )}
          <button className="btn-secondary flex-1">
            <Download className="w-4 h-4" />
            <span>تحميل PDF</span>
          </button>
          <button onClick={onClose} className="btn-secondary">
            إغلاق
          </button>
        </div>
      </div>
    </Modal>
  );
};

// ── Main InvoicesPage ─────────────────────
const InvoicesPage = () => {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [stats, setStats] = useState({ total: 0, paid: 0, pending: 0, overdue: 0 });
  const [pagination, setPagination] = useState({ page: 1, total: 0, pages: 1 });

  const fetchInvoices = async (page = 1) => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: "10",
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });

      const res = await api.get(`/invoices?${params}`);
      setInvoices(res.data.data.invoices);
      setPagination(res.data.data.pagination);
      setStats(res.data.data.stats || { total: 0, paid: 0, pending: 0, overdue: 0 });
    } catch (err) {
      logger.error("خطأ في جلب الفواتير:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices(1);
  }, [statusFilter, searchQuery]);

  const handleMarkAsPaid = async (id) => {
    try {
      await api.patch(`/invoices/${id}/pay`);
      fetchInvoices(pagination.page);
      setIsDetailOpen(false);
    } catch (err) {
      logger.error("خطأ في تأكيد الدفع:", err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">الفواتير</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة الفواتير والمدفوعات</p>
        </div>
        <button className="btn-primary">
          <Plus className="w-4 h-4" />
          <span>فاتورة جديدة</span>
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-blue-500/20 flex items-center justify-center text-blue-400">
              <FileText className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">{stats.total}</p>
              <p className="text-xs text-edham-text-muted">إجمالي الفواتير</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-green-500/20 flex items-center justify-center text-green-400">
              <CheckCircle className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">{stats.paid}</p>
              <p className="text-xs text-edham-text-muted">مدفوعة</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-yellow-500/20 flex items-center justify-center text-yellow-400">
              <DollarSign className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">{stats.pending}</p>
              <p className="text-xs text-edham-text-muted">معلقة</p>
            </div>
          </div>
        </div>
        <div className="card cursor-pointer" onClick={() => setStatusFilter("overdue")}>
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-red-500/20 flex items-center justify-center text-red-400">
              <AlertCircle className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">{stats.overdue}</p>
              <p className="text-xs text-edham-text-muted">متأخرة</p>
            </div>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث برقم الفاتورة أو العميل..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
            />
          </div>
          <div className="relative">
            <Filter className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-10 pl-8 py-2.5 text-edham-white outline-none focus:border-edham-primary cursor-pointer min-w-[140px]"
            >
              <option value="all">جميع الحالات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>
          <button
            onClick={() => fetchInvoices(pagination.page)}
            className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
            disabled={loading}
          >
            <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="card overflow-hidden">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>رقم الفاتورة</th>
                <th>العميل</th>
                <th>التاريخ</th>
                <th>الإجمالي</th>
                <th>الحالة</th>
                <th>الإجراءات</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [...Array(5)].map((_, i) => (
                  <tr key={i}><td colSpan={6}><div className="skeleton h-12 rounded-lg" /></td></tr>
                ))
              ) : invoices.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center py-12">
                    <div className="flex flex-col items-center gap-3">
                      <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                        <FileText className="w-8 h-8 text-edham-text-muted" />
                      </div>
                      <p className="text-edham-text-muted">لا توجد فواتير</p>
                    </div>
                  </td>
                </tr>
              ) : (
                invoices.map((invoice) => (
                  <tr key={invoice._id} className="hover:bg-edham-black/30 transition-colors">
                    <td>
                      <span className="text-edham-primary font-mono text-sm font-medium">
                        #{invoice.invoiceNumber}
                      </span>
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 bg-edham-primary/20 rounded-full flex items-center justify-center">
                          <span className="text-xs font-bold text-edham-primary">
                            {invoice.client?.name?.charAt(0) || "?"}
                          </span>
                        </div>
                        <span className="text-edham-white">{invoice.client?.name || "—"}</span>
                      </div>
                    </td>
                    <td className="text-edham-text-muted text-sm">
                      {new Date(invoice.issueDate).toLocaleDateString("ar-EG")}
                    </td>
                    <td className="text-edham-white font-medium">
                      {invoice.total?.toLocaleString()} ج.م
                    </td>
                    <td>
                      <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                        STATUS_CONFIG[invoice.status]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
                      }`}>
                        {STATUS_CONFIG[invoice.status]?.label || invoice.status}
                      </span>
                    </td>
                    <td>
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => {
                            setSelectedInvoice(invoice);
                            setIsDetailOpen(true);
                          }}
                          className="btn-icon text-edham-text-muted hover:text-edham-primary"
                          title="عرض"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button className="btn-icon text-edham-text-muted hover:text-blue-400" title="تحميل">
                          <Download className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {pagination.pages > 1 && (
          <div className="flex items-center justify-between p-4 border-t border-edham-gray">
            <p className="text-sm text-edham-text-muted">صفحة {pagination.page} من {pagination.pages}</p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => fetchInvoices(pagination.page - 1)}
                disabled={pagination.page === 1}
                className="btn-sm disabled:opacity-50"
              >
                السابق
              </button>
              <button
                onClick={() => fetchInvoices(pagination.page + 1)}
                disabled={pagination.page === pagination.pages}
                className="btn-sm disabled:opacity-50"
              >
                التالي
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Detail Modal */}
      <InvoiceDetailModal
        invoice={selectedInvoice}
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelectedInvoice(null);
        }}
        onMarkAsPaid={handleMarkAsPaid}
      />
    </div>
  );
};

export default InvoicesPage;
