/**
 * ============================================
 * 📊 Reports Page - نظام إدهام
 * Edham Logistics - Reports & Export Page
 * ============================================
 */

import React, { useState } from 'react';
import api from '../services/api';
import { FileText, Download, Calendar, Filter, Printer, FileSpreadsheet } from 'lucide-react';
import Button from '../components/UI/Button';
import Select from '../components/UI/Input';

export default function ReportsPage() {
  const [reportType, setReportType] = useState('shipments');
  const [dateRange, setDateRange] = useState('month');
  const [format, setFormat] = useState('pdf');
  const [loading, setLoading] = useState(false);

  const reportTypes = [
    { value: 'shipments', label: 'تقرير الشحنات' },
    { value: 'trucks', label: 'تقرير الشاحنات' },
    { value: 'invoices', label: 'تقرير الفواتير' },
    { value: 'drivers', label: 'تقرير السائقين' },
    { value: 'maintenance', label: 'تقرير الصيانة' },
    { value: 'payments', label: 'تقرير المدفوعات' },
    { value: 'revenue', label: 'تقرير الإيرادات' },
    { value: 'performance', label: 'تقرير الأداء' }
  ];

  const dateRanges = [
    { value: 'today', label: 'اليوم' },
    { value: 'week', label: 'آخر أسبوع' },
    { value: 'month', label: 'آخر شهر' },
    { value: 'quarter', label: 'آخر ربع سنة' },
    { value: 'year', label: 'آخر سنة' },
    { value: 'custom', label: 'فترة مخصصة' }
  ];

  const formats = [
    { value: 'pdf', label: 'PDF' },
    { value: 'csv', label: 'CSV' },
    { value: 'excel', label: 'Excel' }
  ];

  const handleGenerateReport = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/reports/${reportType}`, {
        params: { dateRange, format },
        responseType: format === 'pdf' ? 'blob' : 'json'
      });

      if (format === 'pdf') {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${reportType}-report.pdf`);
        document.body.appendChild(link);
        link.click();
        link.remove();
      } else {
        // Handle CSV/Excel
        const url = window.URL.createObjectURL(new Blob([JSON.stringify(response.data)]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `${reportType}-report.${format}`);
        document.body.appendChild(link);
        link.click();
        link.remove();
      }
    } catch (err) {
      console.error('Report generation error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-white">التقارير والتصدير</h1>
        <p className="text-gray-400 mt-1">إنشاء وتصدير التقارير المختلفة</p>
      </div>

      {/* Report Generator */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <FileText className="w-5 h-5" />
          إنشاء تقرير
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <Select
            label="نوع التقرير"
            value={reportType}
            onChange={(e) => setReportType(e.target.value)}
            options={reportTypes}
          />
          <Select
            label="الفترة الزمنية"
            value={dateRange}
            onChange={(e) => setDateRange(e.target.value)}
            options={dateRanges}
          />
          <Select
            label="الصيغة"
            value={format}
            onChange={(e) => setFormat(e.target.value)}
            options={formats}
          />
        </div>

        <div className="flex gap-2">
          <Button
            onClick={handleGenerateReport}
            loading={loading}
            icon={<Download className="w-4 h-4" />}
          >
            إنشاء التقرير
          </Button>
          <Button
            variant="secondary"
            onClick={handlePrint}
            icon={<Printer className="w-4 h-4" />}
          >
            طباعة
          </Button>
        </div>
      </div>

      {/* Quick Reports */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Calendar className="w-5 h-5" />
          تقارير سريعة
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <QuickReportCard
            title="الشحنات اليومية"
            description="تقرير الشحنات المنشأة اليوم"
            icon={<FileText className="w-6 h-6" />}
            color="blue"
          />
          <QuickReportCard
            title="الفواتير المستحقة"
            description="الفواتير غير المدفوعة"
            icon={<FileSpreadsheet className="w-6 h-6" />}
            color="yellow"
          />
          <QuickReportCard
            title="أداء السائقين"
            description="تقييم أداء السائقين"
            icon={<FileText className="w-6 h-6" />}
            color="green"
          />
          <QuickReportCard
            title="صيانة الشاحنات"
            description="جدول الصيانة القادم"
            icon={<FileText className="w-6 h-6" />}
            color="purple"
          />
          <QuickReportCard
            title="الإيرادات الشهرية"
            description="ملخص الإيرادات الشهرية"
            icon={<FileSpreadsheet className="w-6 h-6" />}
            color="gold"
          />
          <QuickReportCard
            title="الرحلات المكتملة"
            description="تقرير الرحلات المنتهية"
            icon={<FileText className="w-6 h-6" />}
            color="red"
          />
        </div>
      </div>

      {/* Scheduled Reports */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Calendar className="w-5 h-5" />
          التقارير المجدولة
        </h2>

        <div className="space-y-3">
          <ScheduledReportItem
            name="تقرير الإيرادات الأسبوعي"
            frequency="كل أسبوع"
            nextRun="الأحد القادم"
            status="active"
          />
          <ScheduledReportItem
            name="تقرير الشحنات الشهري"
            frequency="كل شهر"
            nextRun="1 الشهر القادم"
            status="active"
          />
          <ScheduledReportItem
            name="تقرير الصيانة الربع سنوي"
            frequency="كل ربع سنة"
            nextRun="بعد 3 أشهر"
            status="active"
          />
        </div>
      </div>
    </div>
  );
}

function QuickReportCard({ title, description, icon, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600',
    purple: 'bg-purple-600',
    gold: 'bg-yellow-500',
    red: 'bg-red-600'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white cursor-pointer hover:opacity-90 transition`}>
      <div className="flex items-center gap-3 mb-3">
        {icon}
        <h3 className="font-semibold">{title}</h3>
      </div>
      <p className="text-sm opacity-75">{description}</p>
    </div>
  );
}

function ScheduledReportItem({ name, frequency, nextRun, status }) {
  return (
    <div className="flex items-center justify-between p-4 bg-gray-700 rounded">
      <div>
        <p className="text-white font-semibold">{name}</p>
        <p className="text-gray-400 text-sm">{frequency}</p>
      </div>
      <div className="text-right">
        <p className="text-gray-400 text-sm">التشغيل القادم: {nextRun}</p>
        <span className={`text-xs px-2 py-1 rounded ${status === 'active' ? 'bg-green-600' : 'bg-gray-600'}`}>
          {status === 'active' ? 'نشط' : 'معطل'}
        </span>
      </div>
    </div>
  );
}
