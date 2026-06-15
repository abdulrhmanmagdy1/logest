import React from "react";
import { CheckCircle, Clock, AlertTriangle, XCircle, Package, Truck, User } from "lucide-react";

const StatusBadge = ({ status, type = "shipment" }) => {
  const config = {
    shipment: {
      pending:     { label: "معلقة",     icon: Clock,         color: "text-yellow-400",   bg: "bg-yellow-900/30" },
      confirmed:   { label: "مؤكدة",     icon: CheckCircle,   color: "text-blue-400",    bg: "bg-blue-900/30" },
      assigned:    { label: "مُسندة",    icon: Truck,         color: "text-purple-400",  bg: "bg-purple-900/30" },
      in_transit:  { label: "قيد التوصيل", icon: Package,       color: "text-orange-400",  bg: "bg-orange-900/30" },
      delivered:   { label: "مُسلّمة",   icon: CheckCircle,   color: "text-green-400",   bg: "bg-green-900/30" },
      cancelled:   { label: "ملغية",     icon: XCircle,       color: "text-red-400",     bg: "bg-red-900/30" },
      returned:    { label: "مرتجعة",    icon: AlertTriangle,  color: "text-gray-400",    bg: "bg-gray-800" },
    },
    truck: {
      active:         { label: "نشط",      icon: CheckCircle,   color: "text-green-400",   bg: "bg-green-900/30" },
      maintenance:    { label: "صيانة",    icon: Wrench,        color: "text-yellow-400",  bg: "bg-yellow-900/30" },
      idle:           { label: "خامل",     icon: Clock,         color: "text-gray-400",    bg: "bg-gray-800" },
      out_of_service: { label: "خارج الخدمة", icon: XCircle,   color: "text-red-400",     bg: "bg-red-900/30" },
    },
    trip: {
      scheduled:   { label: "مجدولة",    icon: Clock,         color: "text-blue-400",    bg: "bg-blue-900/30" },
      started:     { label: "جارية",     icon: CheckCircle,   color: "text-green-400",   bg: "bg-green-900/30" },
      in_progress: { label: "قيد التنفيذ", icon: Truck,       color: "text-orange-400",  bg: "bg-orange-900/30" },
      completed:   { label: "مكتملة",   icon: CheckCircle,   color: "text-gray-400",    bg: "bg-gray-800" },
      cancelled:   { label: "ملغية",    icon: XCircle,       color: "text-red-400",     bg: "bg-red-900/30" },
    },
    invoice: {
      draft:     { label: "مسودة",   icon: Clock,         color: "text-gray-400",    bg: "bg-gray-800" },
      pending:   { label: "معلقة",   icon: AlertTriangle,  color: "text-yellow-400",  bg: "bg-yellow-900/30" },
      partial:   { label: "جزئية",  icon: Clock,         color: "text-blue-400",    bg: "bg-blue-900/30" },
      paid:      { label: "مدفوعة", icon: CheckCircle,   color: "text-green-400",   bg: "bg-green-900/30" },
      overdue:   { label: "متأخرة", icon: AlertTriangle,  color: "text-red-400",     bg: "bg-red-900/30" },
      cancelled: { label: "ملغية",  icon: XCircle,       color: "text-gray-400",    bg: "bg-gray-800" },
    },
    maintenance: {
      reported:    { label: "مُبلَّغ",    icon: AlertTriangle,  color: "text-yellow-400",  bg: "bg-yellow-900/30" },
      scheduled:   { label: "مجدول",    icon: Clock,         color: "text-blue-400",    bg: "bg-blue-900/30" },
      in_progress: { label: "جارٍ",     icon: Wrench,        color: "text-orange-400",  bg: "bg-orange-900/30" },
      completed:   { label: "مكتمل",   icon: CheckCircle,   color: "text-green-400",   bg: "bg-green-900/30" },
      cancelled:   { label: "ملغي",    icon: XCircle,       color: "text-gray-400",    bg: "bg-gray-800" },
    },
  };

  const typeConfig = config[type] || config.shipment;
  const statusConfig = typeConfig[status] || { label: status, icon: AlertTriangle, color: "text-gray-400", bg: "bg-gray-800" };
  const StatusIcon = statusConfig.icon;

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${statusConfig.bg} ${statusConfig.color}`}>
      <StatusIcon className="w-3.5 h-3.5" />
      <span>{statusConfig.label}</span>
    </span>
  );
};

export default StatusBadge;
