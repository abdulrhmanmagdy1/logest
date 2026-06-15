import React, { useState } from 'react';
import { FileText, Download, Calendar, Filter } from 'lucide-react';
import { motion } from 'framer-motion';
import { api } from '../context/AuthContext';

const Reports = () => {
  const [dateRange, setDateRange] = useState({
    start: '',
    end: ''
  });
  const [reportType, setReportType] = useState('shipments');
  const [loading, setLoading] = useState(false);

  const reportTypes = [
    { value: 'shipments', label: 'تقرير الشحنات', icon: FileText },
    { value: 'financial', label: 'التقرير المالي', icon: FileText },
    { value: 'drivers', label: 'أداء السائقين', icon: FileText },
    { value: 'maintenance', label: 'حالة الصيانة', icon: FileText }
  ];

  const downloadReport = async (format) => {
    setLoading(true);
    try {
      const response = await api.get(`/reports/${reportType}/${format}`, {
        params: dateRange,
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${reportType}-report.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      logger.error('Error downloading report:', error);
      alert('حدث خطأ أثناء تحميل التقرير');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 bg-edham-black min-h-screen">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-4xl mx-auto"
      >
        <h1 className="text-3xl font-bold text-edham-white mb-8">التقارير</h1>

        <div className="card mb-6">
          <div className="flex items-center gap-3 mb-6">
            <Filter className="w-6 h-6 text-edham-primary" />
            <h2 className="text-xl font-semibold text-edham-white">خيارات التقرير</h2>
          </div>

          <div className="grid md:grid-cols-3 gap-6">
            <div>
              <label className="input-label">نوع التقرير</label>
              <select
                value={reportType}
                onChange={(e) => setReportType(e.target.value)}
                className="input-field"
              >
                {reportTypes.map(type => (
                  <option key={type.value} value={type.value}>{type.label}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="input-label">من تاريخ</label>
              <div className="relative">
                <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type="date"
                  value={dateRange.start}
                  onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                  className="input-field pr-12"
                />
              </div>
            </div>

            <div>
              <label className="input-label">إلى تاريخ</label>
              <div className="relative">
                <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  type="date"
                  value={dateRange.end}
                  onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                  className="input-field pr-12"
                />
              </div>
            </div>
          </div>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => downloadReport('pdf')}
            disabled={loading}
            className="card hover:border-edham-primary transition-colors group"
          >
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 bg-red-500/20 rounded-2xl flex items-center justify-center group-hover:bg-red-500/30 transition-colors">
                <FileText className="w-7 h-7 text-red-400" />
              </div>
              <div className="text-right">
                <h3 className="text-lg font-semibold text-edham-white">تصدير PDF</h3>
                <p className="text-edham-white/60 text-sm">تحميل التقرير بصيغة PDF</p>
              </div>
              <Download className="w-5 h-5 text-edham-white/40 mr-auto" />
            </div>
          </motion.button>

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => downloadReport('excel')}
            disabled={loading}
            className="card hover:border-green-500/50 transition-colors group"
          >
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 bg-green-500/20 rounded-2xl flex items-center justify-center group-hover:bg-green-500/30 transition-colors">
                <FileText className="w-7 h-7 text-green-400" />
              </div>
              <div className="text-right">
                <h3 className="text-lg font-semibold text-edham-white">تصدير Excel</h3>
                <p className="text-edham-white/60 text-sm">تحميل التقرير بصيغة Excel</p>
              </div>
              <Download className="w-5 h-5 text-edham-white/40 mr-auto" />
            </div>
          </motion.button>
        </div>

        <div className="card mt-6">
          <h3 className="text-lg font-semibold text-edham-white mb-4">التقارير المحفوظة</h3>
          <div className="space-y-3">
            <div className="flex items-center justify-between p-4 bg-edham-dark rounded-xl">
              <div>
                <p className="text-edham-white font-medium">تقرير الشحنات - يناير 2024</p>
                <p className="text-edham-white/50 text-sm">تم إنشاؤه: 2024-01-31</p>
              </div>
              <div className="flex gap-2">
                <button className="p-2 hover:bg-white/10 rounded-lg transition-colors">
                  <Download className="w-5 h-5 text-edham-white/60" />
                </button>
              </div>
            </div>
            <div className="flex items-center justify-between p-4 bg-edham-dark rounded-xl">
              <div>
                <p className="text-edham-white font-medium">التقرير المالي - الربع الأول</p>
                <p className="text-edham-white/50 text-sm">تم إنشاؤه: 2024-03-31</p>
              </div>
              <div className="flex gap-2">
                <button className="p-2 hover:bg-white/10 rounded-lg transition-colors">
                  <Download className="w-5 h-5 text-edham-white/60" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default Reports;
