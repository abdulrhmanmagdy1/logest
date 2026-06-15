import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';
import logger from '../utils/logger';
import { 
  ArrowLeft,
  FileText,
  Download,
  Calendar,
  Filter,
  BarChart3,
  PieChart,
  TrendingUp,
  CheckCircle
} from 'lucide-react';

const ReportGenerator = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [reportType, setReportType] = useState('shipments');
  const [dateRange, setDateRange] = useState('month');
  const [format, setFormat] = useState('pdf');
  const [filters, setFilters] = useState({
    status: 'all',
    truck: '',
    driver: '',
    client: ''
  });
  const [generating, setGenerating] = useState(false);

  const reportTypes = [
    { id: 'shipments', name: 'تقرير الشحنات', icon: FileText },
    { id: 'maintenance', name: 'تقرير الصيانة', icon: TrendingUp },
    { id: 'performance', name: 'تقرير الأداء', icon: BarChart3 },
    { id: 'financial', name: 'تقرير المالي', icon: PieChart }
  ];

  const handleGenerateReport = async () => {
    setGenerating(true);
    try {
      const response = await axios.post('http://192.168.1.12:5000/api/reports/generate', {
        type: reportType,
        dateRange,
        format,
        filters
      }, {
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `report-${reportType}-${dateRange}.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      logger.error('Error generating report:', error);
      alert('حدث خطأ أثناء إنشاء التقرير');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="min-h-screen bg-edham-black">
      <div className="bg-edham-dark border-b border-edham-gray">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <button onClick={() => navigate('/dashboard')} className="text-edham-white hover:text-edham-gold transition-colors">
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex items-center gap-2">
              <div className="bg-edham-white p-1.5 rounded-full">
                <FileText className="w-5 h-5 text-edham-black" />
              </div>
              <h1 className="text-xl font-bold text-edham-white">مولد التقارير</h1>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-edham-dark rounded-2xl p-6 border border-edham-gray mb-6">
            <h2 className="text-lg font-bold text-edham-white mb-4 flex items-center gap-2">
              <Filter className="w-5 h-5" />
              نوع التقرير
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {reportTypes.map((type) => (
                <button
                  key={type.id}
                  onClick={() => setReportType(type.id)}
                  className={`p-4 rounded-xl border-2 transition-all ${
                    reportType === type.id
                      ? 'border-edham-primary bg-edham-primary/10'
                      : 'border-edham-gray bg-edham-black hover:border-edham-white/50'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <type.icon className={`w-6 h-6 ${reportType === type.id ? 'text-edham-primary' : 'text-edham-white/50'}`} />
                    <span className={`text-edham-white ${reportType === type.id ? 'font-semibold' : ''}`}>
                      {type.name}
                    </span>
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div className="bg-edham-dark rounded-2xl p-6 border border-edham-gray mb-6">
            <h2 className="text-lg font-bold text-edham-white mb-4 flex items-center gap-2">
              <Calendar className="w-5 h-5" />
              النطاق الزمني
            </h2>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {['today', 'week', 'month', 'year'].map((range) => (
                <button
                  key={range}
                  onClick={() => setDateRange(range)}
                  className={`p-4 rounded-xl border-2 transition-all ${
                    dateRange === range
                      ? 'border-edham-primary bg-edham-primary/10'
                      : 'border-edham-gray bg-edham-black hover:border-edham-white/50'
                  }`}
                >
                  <span className={`text-edham-white ${dateRange === range ? 'font-semibold' : ''}`}>
                    {range === 'today' ? 'اليوم' : range === 'week' ? 'الأسبوع' : range === 'month' ? 'الشهر' : 'السنة'}
                  </span>
                </button>
              ))}
            </div>
          </div>

          <div className="bg-edham-dark rounded-2xl p-6 border border-edham-gray mb-6">
            <h2 className="text-lg font-bold text-edham-white mb-2">صيغة التقرير</h2>
            <div className="flex gap-4">
              {['pdf', 'excel', 'csv'].map((fmt) => (
                <button
                  key={fmt}
                  onClick={() => setFormat(fmt)}
                  className={`flex-1 p-4 rounded-xl border-2 transition-all ${
                    format === fmt
                      ? 'border-edham-primary bg-edham-primary/10'
                      : 'border-edham-gray bg-edham-black hover:border-edham-white/50'
                  }`}
                >
                  <span className={`text-edham-white ${format === fmt ? 'font-semibold' : ''}`}>
                    {fmt.toUpperCase()}
                  </span>
                </button>
              ))}
            </div>
          </div>

          <button
            onClick={handleGenerateReport}
            disabled={generating}
            className="w-full bg-edham-primary text-white font-bold py-4 rounded-xl hover:bg-edham-primaryLight transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {generating ? (
              <>
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                جاري إنشاء التقرير...
              </>
            ) : (
              <>
                <Download className="w-5 h-5" />
                إنشاء التقرير
              </>
            )}
          </button>

          <div className="mt-8 bg-edham-dark rounded-2xl p-6 border border-edham-gray">
            <h2 className="text-lg font-bold text-edham-white mb-4 flex items-center gap-2">
              <CheckCircle className="w-5 h-5 text-edham-green" />
              معاينة التقرير
            </h2>
            <div className="bg-edham-black rounded-xl p-6">
              <div className="text-edham-white/50 text-center">
                <FileText className="w-16 h-16 mx-auto mb-4 opacity-50" />
                <p>سيظهر معاينة التقرير هنا بعد الإنشاء</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReportGenerator;
