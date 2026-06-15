/**
 * ============================================
 * 🛡️ Risk Management Page - نظام إدهام الاحترافي
 * Edham Logistics - Risk & Compliance Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Shield, AlertTriangle, CheckCircle, Clock, TrendingUp,
  FileText, Download, Filter, Search, Plus, Eye,
  Edit, Trash2, BarChart3, PieChart, Activity,
  Users, Truck, MapPin, Calendar, Settings
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const RiskManagementPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [activeTab, setActiveTab] = useState('dashboard');
  const [risks, setRisks] = useState([]);
  const [complianceItems, setComplianceItems] = useState([]);
  const [incidents, setIncidents] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [assessments, setAssessments] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const [riskForm, setRiskForm] = useState({
    title: '',
    description: '',
    category: 'operational',
    probability: 'medium',
    impact: 'medium',
    riskLevel: 'medium',
    mitigationPlan: '',
    owner: '',
    status: 'open',
    dueDate: ''
  });

  const [filters, setFilters] = useState({
    category: '',
    severity: '',
    status: '',
    dateRange: ''
  });

  useEffect(() => {
    fetchData();
  }, [activeTab, filters]);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      const [statsRes, risksRes, complianceRes, incidentsRes] = await Promise.all([
        api.get('/risk-management/statistics'),
        api.get('/risk-management/risks'),
        api.get('/risk-management/compliance'),
        api.get('/risk-management/incidents')
      ]);

      setStatistics(statsRes.data.statistics);
      setRisks(risksRes.data.risks || []);
      setComplianceItems(complianceRes.data.compliance || []);
      setIncidents(incidentsRes.data.incidents || []);
    } catch (error) {
      console.error('Error fetching risk management data:', error);
      showToast('فشل تحميل بيانات إدارة المخاطر', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRisk = async () => {
    try {
      await api.post('/risk-management/risks', riskForm);
      showToast('تم إضافة المخاطر بنجاح', 'success');
      setShowModal(false);
      resetRiskForm();
      fetchData();
    } catch (error) {
      console.error('Error creating risk:', error);
      showToast('فشل إضافة المخاطر', 'error');
    }
  };

  const handleUpdateRiskStatus = async (riskId, status) => {
    try {
      await api.patch(`/risk-management/risks/${riskId}/status`, { status });
      showToast('تم تحديث حالة المخاطر بنجاح', 'success');
      fetchData();
    } catch (error) {
      console.error('Error updating risk status:', error);
      showToast('فشل تحديث الحالة', 'error');
    }
  };

  const resetRiskForm = () => {
    setRiskForm({
      title: '',
      description: '',
      category: 'operational',
      probability: 'medium',
      impact: 'medium',
      riskLevel: 'medium',
      mitigationPlan: '',
      owner: '',
      status: 'open',
      dueDate: ''
    });
  };

  const getRiskLevelColor = (level) => {
    const colors = {
      low: 'text-green-600 bg-green-100',
      medium: 'text-yellow-600 bg-yellow-100',
      high: 'text-orange-600 bg-orange-100',
      critical: 'text-red-600 bg-red-100'
    };
    return colors[level] || colors.medium;
  };

  const getComplianceStatusColor = (status) => {
    const colors = {
      compliant: 'text-green-600 bg-green-100',
      non_compliant: 'text-red-600 bg-red-100',
      pending: 'text-yellow-600 bg-yellow-100',
      exempt: 'text-gray-600 bg-gray-100'
    };
    return colors[status] || colors.pending;
  };

  const renderDashboard = () => (
    <div className="space-y-6">
      {/* Risk Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">المخاطر النشطة</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.activeRisks || 0}
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.criticalRisks || 0} حرجة
              </p>
            </div>
            <AlertTriangle className="w-8 h-8 text-red-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">الامتثال التنظيمي</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.complianceRate || 0}%
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.compliantItems || 0} عنصر
              </p>
            </div>
            <Shield className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">الحوادث هذا الشهر</p>
              <p className="text-2xl font-bold text-yellow-600">
                {statistics?.incidentsThisMonth || 0}
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.resolvedIncidents || 0} تم حلها
              </p>
            </div>
            <FileText className="w-8 h-8 text-yellow-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">معدل الاستجابة</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.responseTime || 0}h
              </p>
              <p className="text-xs text-gray-500">
                متوسط الوقت
              </p>
            </div>
            <Clock className="w-8 h-8 text-blue-600" />
          </div>
        </motion.div>
      </div>

      {/* Risk Matrix */}
      <div className="bg-white rounded-lg shadow-sm p-6 border">
        <h3 className="text-lg font-semibold mb-4">مصفوفة المخاطر</h3>
        <div className="grid grid-cols-5 gap-2">
          <div></div>
          <div className="text-center text-sm font-medium">منخفض</div>
          <div className="text-center text-sm font-medium">متوسط</div>
          <div className="text-center text-sm font-medium">مرتفع</div>
          <div className="text-center text-sm font-medium">حرج</div>
          
          {['منخفض', 'متوسط', 'مرتفع', 'حرج'].map((impact, i) => (
            <React.Fragment key={i}>
              <div className="text-sm font-medium">{impact}</div>
              {['منخفض', 'متوسط', 'مرتفع', 'حرج'].map((probability, j) => {
                const riskLevel = Math.max(i, j) + 1;
                const count = statistics?.riskMatrix?.[i]?.[j] || 0;
                const colorClass = riskLevel <= 1 ? 'bg-green-100' :
                                  riskLevel === 2 ? 'bg-yellow-100' :
                                  riskLevel === 3 ? 'bg-orange-100' : 'bg-red-100';
                
                return (
                  <div key={j} className={`${colorClass} p-4 rounded text-center`}>
                    <div className="text-2xl font-bold">{count}</div>
                  </div>
                );
              })}
            </React.Fragment>
          ))}
        </div>
      </div>

      {/* Recent Incidents */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">الحوادث الأخيرة</h3>
            <button
              onClick={() => setActiveTab('incidents')}
              className="text-blue-600 hover:text-blue-800 text-sm"
            >
              عرض الكل
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التاريخ
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  النوع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الوصف
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {incidents.slice(0, 5).map((incident, index) => (
                <motion.tr
                  key={incident._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(incident.createdAt).toLocaleDateString('ar-SA')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {incident.type}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {incident.description}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      incident.status === 'resolved' ? 'bg-green-100 text-green-800' :
                      incident.status === 'investigating' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {incident.status === 'resolved' ? 'تم الحل' :
                       incident.status === 'investigating' ? 'قيد التحقيق' : 'مفتوح'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedItem(incident)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  const renderRisks = () => (
    <div className="space-y-6">
      {/* Filters and Actions */}
      <div className="bg-white rounded-lg shadow-sm p-4 border">
        <div className="flex flex-col lg:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="ابحث في المخاطر..."
              className="w-full pr-10 pl-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div className="flex items-center space-x-2 space-x-reverse">
            <select
              value={filters.category}
              onChange={(e) => setFilters({ ...filters, category: e.target.value })}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">جميع الفئات</option>
              <option value="operational">تشغيلية</option>
              <option value="financial">مالية</option>
              <option value="compliance">امتثال</option>
              <option value="strategic">استراتيجية</option>
            </select>

            <select
              value={filters.severity}
              onChange={(e) => setFilters({ ...filters, severity: e.target.value })}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">جميع الخطورات</option>
              <option value="low">منخفضة</option>
              <option value="medium">متوسطة</option>
              <option value="high">مرتفعة</option>
              <option value="critical">حرجة</option>
            </select>

            <button
              onClick={() => setShowModal(true)}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إضافة مخاطر</span>
            </button>
          </div>
        </div>
      </div>

      {/* Risks Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  العنوان
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الفئة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  مستوى المخاطر
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسؤول
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  تاريخ الاستحقاق
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {risks.map((risk, index) => (
                <motion.tr
                  key={risk._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {risk.title}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {risk.category}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getRiskLevelColor(risk.riskLevel)}`}>
                      {risk.riskLevel}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {risk.owner}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      risk.status === 'closed' ? 'bg-green-100 text-green-800' :
                      risk.status === 'mitigating' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {risk.status === 'closed' ? 'مغلقة' :
                       risk.status === 'mitigating' ? 'قيد المعالجة' : 'مفتوحة'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(risk.dueDate).toLocaleDateString('ar-SA')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedItem(risk)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      {risk.status === 'open' && (
                        <button
                          onClick={() => handleUpdateRiskStatus(risk._id, 'mitigating')}
                          className="text-yellow-600 hover:text-yellow-900"
                        >
                          <Shield className="w-4 h-4" />
                        </button>
                      )}
                      {risk.status === 'mitigating' && (
                        <button
                          onClick={() => handleUpdateRiskStatus(risk._id, 'closed')}
                          className="text-green-600 hover:text-green-900"
                        >
                          <CheckCircle className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  const renderCompliance = () => (
    <div className="space-y-6">
      {/* Compliance Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">معدل الامتثال</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.complianceRate || 0}%
              </p>
            </div>
            <Shield className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">عناصر غير ملتزمة</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.nonCompliantItems || 0}
              </p>
            </div>
            <AlertTriangle className="w-8 h-8 text-red-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">عمليات التدقيق</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.auditsCompleted || 0}
              </p>
            </div>
            <FileText className="w-8 h-8 text-blue-600" />
          </div>
        </motion.div>
      </div>

      {/* Compliance Items */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <h3 className="text-lg font-semibold">عناصر الامتثال</h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 p-4">
          {complianceItems.map((item, index) => (
            <motion.div
              key={item._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="border rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-3">
                <h4 className="font-semibold text-gray-900">{item.title}</h4>
                <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getComplianceStatusColor(item.status)}`}>
                  {item.status === 'compliant' ? 'ملتزم' :
                   item.status === 'non_compliant' ? 'غير ملتزم' :
                   item.status === 'pending' ? 'معلق' : 'معفى'}
                </span>
              </div>
              
              <p className="text-sm text-gray-600 mb-3">{item.description}</p>
              
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">الفئة:</span>
                  <span className="font-medium">{item.category}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">آخر مراجعة:</span>
                  <span className="font-medium">
                    {new Date(item.lastReview).toLocaleDateString('ar-SA')}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">المسؤول:</span>
                  <span className="font-medium">{item.owner}</span>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );

  const renderIncidents = () => (
    <div className="space-y-6">
      {/* Incident Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">إجمالي الحوادث</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.totalIncidents || 0}
              </p>
            </div>
            <AlertTriangle className="w-8 h-8 text-red-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">تم حلها</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.resolvedIncidents || 0}
              </p>
            </div>
            <CheckCircle className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">قيد التحقيق</p>
              <p className="text-2xl font-bold text-yellow-600">
                {statistics?.investigatingIncidents || 0}
              </p>
            </div>
            <Clock className="w-8 h-8 text-yellow-600" />
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">متوسط وقت الحل</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.averageResolutionTime || 0}h
              </p>
            </div>
            <Activity className="w-8 h-8 text-blue-600" />
          </div>
        </motion.div>
      </div>

      {/* Incidents Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">سجل الحوادث</h3>
            <button className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              <Plus className="w-4 h-4" />
              <span>إبلاغ عن حادث</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التاريخ والوقت
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  النوع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الخطورة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الوصف
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {incidents.map((incident, index) => (
                <motion.tr
                  key={incident._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {new Date(incident.createdAt).toLocaleString('ar-SA')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {incident.type}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      incident.severity === 'low' ? 'bg-green-100 text-green-800' :
                      incident.severity === 'medium' ? 'bg-yellow-100 text-yellow-800' :
                      incident.severity === 'high' ? 'bg-orange-100 text-orange-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {incident.severity}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {incident.description}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      incident.status === 'resolved' ? 'bg-green-100 text-green-800' :
                      incident.status === 'investigating' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {incident.status === 'resolved' ? 'تم الحل' :
                       incident.status === 'investigating' ? 'قيد التحقيق' : 'مفتوح'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedItem(incident)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Edit incident */}}
                        className="text-yellow-600 hover:text-yellow-900"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل بيانات إدارة المخاطر...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50" dir="rtl">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center space-x-4 space-x-reverse">
            <Shield className="w-8 h-8 text-blue-600" />
            <div>
              <h1 className="text-2xl font-bold text-gray-900">إدارة المخاطر والامتثال</h1>
              <p className="text-sm text-gray-600">مراقبة وإدارة المخاطر والامتثال التنظيمي</p>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8 space-x-reverse">
            {[
              { id: 'dashboard', label: 'لوحة التحكم', icon: BarChart3 },
              { id: 'risks', label: 'المخاطر', icon: AlertTriangle },
              { id: 'compliance', label: 'الامتثال', icon: Shield },
              { id: 'incidents', label: 'الحوادث', icon: FileText }
            ].map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center space-x-2 space-x-reverse py-4 border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{tab.label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {activeTab === 'dashboard' && renderDashboard()}
        {activeTab === 'risks' && renderRisks()}
        {activeTab === 'compliance' && renderCompliance()}
        {activeTab === 'incidents' && renderIncidents()}
      </div>

      {/* Risk Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">إضافة مخاطر جديدة</h2>
                <button
                  onClick={() => setShowModal(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    عنوان المخاطر
                  </label>
                  <input
                    type="text"
                    value={riskForm.title}
                    onChange={(e) => setRiskForm({ ...riskForm, title: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    الفئة
                  </label>
                  <select
                    value={riskForm.category}
                    onChange={(e) => setRiskForm({ ...riskForm, category: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="operational">تشغيلية</option>
                    <option value="financial">مالية</option>
                    <option value="compliance">امتثال</option>
                    <option value="strategic">استراتيجية</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    مستوى المخاطر
                  </label>
                  <select
                    value={riskForm.riskLevel}
                    onChange={(e) => setRiskForm({ ...riskForm, riskLevel: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="low">منخفض</option>
                    <option value="medium">متوسط</option>
                    <option value="high">مرتفع</option>
                    <option value="critical">حرج</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    المسؤول
                  </label>
                  <input
                    type="text"
                    value={riskForm.owner}
                    onChange={(e) => setRiskForm({ ...riskForm, owner: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    الوصف
                  </label>
                  <textarea
                    value={riskForm.description}
                    onChange={(e) => setRiskForm({ ...riskForm, description: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    rows="3"
                    required
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    خطة التخفيف
                  </label>
                  <textarea
                    value={riskForm.mitigationPlan}
                    onChange={(e) => setRiskForm({ ...riskForm, mitigationPlan: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    rows="3"
                    required
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    تاريخ الاستحقاق
                  </label>
                  <input
                    type="date"
                    value={riskForm.dueDate}
                    onChange={(e) => setRiskForm({ ...riskForm, dueDate: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>
              </div>

              <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
                <button
                  onClick={() => setShowModal(false)}
                  className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  إلغاء
                </button>
                <button
                  onClick={handleCreateRisk}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  حفظ المخاطر
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}

      {/* Details Modal */}
      {selectedItem && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">التفاصيل</h2>
                <button
                  onClick={() => setSelectedItem(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="space-y-4">
                {Object.entries(selectedItem).map(([key, value]) => {
                  if (['_id', '__v', 'createdAt', 'updatedAt'].includes(key)) return null;
                  
                  let displayValue = value;
                  if (typeof value === 'object' && value !== null) {
                    displayValue = JSON.stringify(value);
                  } else if (key.includes('Date') || key.includes('date')) {
                    displayValue = new Date(value).toLocaleDateString('ar-SA');
                  }

                  return (
                    <div key={key} className="flex justify-between">
                      <span className="text-gray-600 capitalize">{key}:</span>
                      <span className="font-medium">{displayValue}</span>
                    </div>
                  );
                })}
              </div>

              <div className="flex items-center justify-end mt-6">
                <button
                  onClick={() => setSelectedItem(null)}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  إغلاق
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default RiskManagementPage;
