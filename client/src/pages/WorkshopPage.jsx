/**
 * ============================================
 * 🔧 Workshop Page - نظام إدهام
 * Edham Logistics - Workshop Management Interface
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Wrench, Settings, Droplet, Battery, Plus, Search, Filter,
  Calendar, Clock, AlertTriangle, CheckCircle, TrendingUp,
  Truck, FileText, Download, Edit, Trash2, Eye,
  BarChart3, PieChart, Activity
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const WorkshopPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [activeTab, setActiveTab] = useState('maintenance');
  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [tireRecords, setTireRecords] = useState([]);
  const [oilRecords, setOilRecords] = useState([]);
  const [fuelRecords, setFuelRecords] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Form states
  const [maintenanceForm, setMaintenanceForm] = useState({
    vehicleId: '',
    type: 'routine',
    description: '',
    cost: '',
    mileage: '',
    nextMaintenanceDate: '',
    notes: '',
    parts: []
  });

  const [tireForm, setTireForm] = useState({
    vehicleId: '',
    position: 'front_left',
    brand: '',
    model: '',
    purchaseDate: '',
    purchaseCost: '',
    currentMileage: '',
    expectedLifeMileage: '',
    condition: 'good',
    notes: ''
  });

  const [oilForm, setOilForm] = useState({
    vehicleId: '',
    oilType: 'synthetic',
    oilBrand: '',
    quantity: '',
    cost: '',
    mileage: '',
    nextOilChangeMileage: '',
    nextOilChangeDate: '',
    notes: ''
  });

  const [fuelForm, setFuelForm] = useState({
    vehicleId: '',
    fuelType: 'diesel',
    quantity: '',
    costPerLiter: '',
    totalCost: '',
    mileage: '',
    fuelingDate: '',
    station: '',
    notes: ''
  });

  useEffect(() => {
    fetchData();
  }, [activeTab, searchTerm]);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      const [vehiclesRes, statsRes] = await Promise.all([
        api.get('/vehicles'),
        api.get('/workshop/statistics')
      ]);

      setVehicles(vehiclesRes.data.vehicles || []);
      setStatistics(statsRes.data.statistics);

      // Fetch data based on active tab
      switch (activeTab) {
        case 'maintenance':
          const maintenanceRes = await api.get('/maintenance');
          setMaintenanceRecords(maintenanceRes.data.maintenance || []);
          break;
        case 'tires':
          const tireRes = await api.get('/tires');
          setTireRecords(tireRes.data.tires || []);
          break;
        case 'oil':
          const oilRes = await api.get('/oil-changes');
          setOilRecords(oilRes.data.oilChanges || []);
          break;
        case 'fuel':
          const fuelRes = await api.get('/fuel-records');
          setFuelRecords(fuelRes.data.fuelRecords || []);
          break;
      }
    } catch (error) {
      console.error('Error fetching workshop data:', error);
      showToast('فشل تحميل البيانات', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitMaintenance = async () => {
    try {
      await api.post('/maintenance', maintenanceForm);
      showToast('تم إضافة سجل الصيانة بنجاح', 'success');
      setShowModal(false);
      resetMaintenanceForm();
      fetchData();
    } catch (error) {
      console.error('Error adding maintenance:', error);
      showToast('فشل إضافة سجل الصيانة', 'error');
    }
  };

  const handleSubmitTire = async () => {
    try {
      await api.post('/tires', tireForm);
      showToast('تم إضافة سجل الإطارات بنجاح', 'success');
      setShowModal(false);
      resetTireForm();
      fetchData();
    } catch (error) {
      console.error('Error adding tire record:', error);
      showToast('فشل إضافة سجل الإطارات', 'error');
    }
  };

  const handleSubmitOil = async () => {
    try {
      await api.post('/oil-changes', oilForm);
      showToast('تم إضافة سجل تغيير الزيت بنجاح', 'success');
      setShowModal(false);
      resetOilForm();
      fetchData();
    } catch (error) {
      console.error('Error adding oil change:', error);
      showToast('فشل إضافة سجل تغيير الزيت', 'error');
    }
  };

  const handleSubmitFuel = async () => {
    try {
      await api.post('/fuel-records', fuelForm);
      showToast('تم إضافة سجل التزود بالوقود بنجاح', 'success');
      setShowModal(false);
      resetFuelForm();
      fetchData();
    } catch (error) {
      console.error('Error adding fuel record:', error);
      showToast('فشل إضافة سجل التزود بالوقود', 'error');
    }
  };

  const resetMaintenanceForm = () => {
    setMaintenanceForm({
      vehicleId: '',
      type: 'routine',
      description: '',
      cost: '',
      mileage: '',
      nextMaintenanceDate: '',
      notes: '',
      parts: []
    });
  };

  const resetTireForm = () => {
    setTireForm({
      vehicleId: '',
      position: 'front_left',
      brand: '',
      model: '',
      purchaseDate: '',
      purchaseCost: '',
      currentMileage: '',
      expectedLifeMileage: '',
      condition: 'good',
      notes: ''
    });
  };

  const resetOilForm = () => {
    setOilForm({
      vehicleId: '',
      oilType: 'synthetic',
      oilBrand: '',
      quantity: '',
      cost: '',
      mileage: '',
      nextOilChangeMileage: '',
      nextOilChangeDate: '',
      notes: ''
    });
  };

  const resetFuelForm = () => {
    setFuelForm({
      vehicleId: '',
      fuelType: 'diesel',
      quantity: '',
      costPerLiter: '',
      totalCost: '',
      mileage: '',
      fuelingDate: '',
      station: '',
      notes: ''
    });
  };

  const getMaintenanceTypeColor = (type) => {
    const colors = {
      routine: 'text-blue-600 bg-blue-100',
      emergency: 'text-red-600 bg-red-100',
      preventive: 'text-green-600 bg-green-100',
      repair: 'text-yellow-600 bg-yellow-100'
    };
    return colors[type] || 'text-gray-600 bg-gray-100';
  };

  const getTireConditionColor = (condition) => {
    const colors = {
      excellent: 'text-green-600 bg-green-100',
      good: 'text-blue-600 bg-blue-100',
      fair: 'text-yellow-600 bg-yellow-100',
      poor: 'text-red-600 bg-red-100'
    };
    return colors[condition] || 'text-gray-600 bg-gray-100';
  };

  const renderMaintenanceTab = () => (
    <div className="space-y-6">
      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">صيانة هذا الشهر</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.maintenance?.thisMonth || 0}
              </p>
            </div>
            <Wrench className="w-8 h-8 text-blue-600" />
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
              <p className="text-sm text-gray-600">تكاليف الصيانة</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.maintenance?.totalCost?.toLocaleString() || 0} ريال
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-red-600" />
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
              <p className="text-sm text-gray-600">صيانة قادمة</p>
              <p className="text-2xl font-bold text-yellow-600">
                {statistics?.maintenance?.upcoming || 0}
              </p>
            </div>
            <Calendar className="w-8 h-8 text-yellow-600" />
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
              <p className="text-sm text-gray-600">معدل الاستهلاك</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.maintenance?.avgConsumption || 0}%
              </p>
            </div>
            <Activity className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>
      </div>

      {/* Maintenance Records Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">سجلات الصيانة</h3>
            <button
              onClick={() => { setMaintenanceForm({ ...maintenanceForm, vehicleId: vehicles[0]?._id || '' }); setShowModal(true); }}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إضافة صيانة</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المركبة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  النوع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الوصف
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التكلفة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسافة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التاريخ
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {maintenanceRecords.map((record, index) => (
                <motion.tr
                  key={record._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {record.vehicleId?.plateNumber || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getMaintenanceTypeColor(record.type)}`}>
                      {record.type === 'routine' ? 'روتينية' :
                       record.type === 'emergency' ? 'طوارئ' :
                       record.type === 'preventive' ? 'وقائية' : 'إصلاح'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {record.description}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {record.cost?.toLocaleString() || 0} ريال
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {record.mileage?.toLocaleString() || 0} كم
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(record.createdAt).toLocaleDateString('ar-SA')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedRecord(record)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Edit logic */}}
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

        {maintenanceRecords.length === 0 && (
          <div className="text-center py-12">
            <Wrench className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">لا توجد سجلات صيانة</p>
          </div>
        )}
      </div>
    </div>
  );

  const renderTiresTab = () => (
    <div className="space-y-6">
      {/* Tire Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">إطارات ممتازة</p>
              <p className="text-2xl font-bold text-green-600">
                {tireRecords.filter(t => t.condition === 'excellent').length}
              </p>
            </div>
            <CheckCircle className="w-8 h-8 text-green-600" />
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
              <p className="text-sm text-gray-600">تحتاج استبدال</p>
              <p className="text-2xl font-bold text-red-600">
                {tireRecords.filter(t => t.condition === 'poor').length}
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
              <p className="text-sm text-gray-600">متوسط العمر</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.tires?.avgAge || 0} كم
              </p>
            </div>
            <Clock className="w-8 h-8 text-blue-600" />
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
              <p className="text-sm text-gray-600">تكاليف الإطارات</p>
              <p className="text-2xl font-bold text-purple-600">
                {statistics?.tires?.totalCost?.toLocaleString() || 0} ريال
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-purple-600" />
          </div>
        </motion.div>
      </div>

      {/* Tires Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">سجلات الإطارات</h3>
            <button
              onClick={() => { setTireForm({ ...tireForm, vehicleId: vehicles[0]?._id || '' }); setShowModal(true); }}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إضافة إطار</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المركبة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الموقع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الماركة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسافة الحالية
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  تاريخ الشراء
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {tireRecords.map((tire, index) => (
                <motion.tr
                  key={tire._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {tire.vehicleId?.plateNumber || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {tire.position === 'front_left' ? 'أمامي يسار' :
                     tire.position === 'front_right' ? 'أمامي يمين' :
                     tire.position === 'rear_left' ? 'خلفي يسار' :
                     tire.position === 'rear_right' ? 'خلفي يمين' : tire.position}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {tire.brand} {tire.model}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getTireConditionColor(tire.condition)}`}>
                      {tire.condition === 'excellent' ? 'ممتاز' :
                       tire.condition === 'good' ? 'جيد' :
                       tire.condition === 'fair' ? 'متوسط' : 'سيء'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {tire.currentMileage?.toLocaleString() || 0} كم
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(tire.purchaseDate).toLocaleDateString('ar-SA')}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedRecord(tire)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Edit logic */}}
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

        {tireRecords.length === 0 && (
          <div className="text-center py-12">
            <Settings className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">لا توجد سجلات إطارات</p>
          </div>
        )}
      </div>
    </div>
  );

  const renderOilTab = () => (
    <div className="space-y-6">
      {/* Oil Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">تغييرات هذا الشهر</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.oil?.thisMonth || 0}
              </p>
            </div>
            <Droplet className="w-8 h-8 text-blue-600" />
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
              <p className="text-sm text-gray-600">تكاليف الزيت</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.oil?.totalCost?.toLocaleString() || 0} ريال
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-red-600" />
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
              <p className="text-sm text-gray-600">تغييرات قادمة</p>
              <p className="text-2xl font-bold text-yellow-600">
                {statistics?.oil?.upcoming || 0}
              </p>
            </div>
            <Calendar className="w-8 h-8 text-yellow-600" />
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
              <p className="text-sm text-gray-600">متوسط الفترة</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.oil?.avgInterval || 0} كم
              </p>
            </div>
            <Activity className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>
      </div>

      {/* Oil Records Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">سجلات تغيير الزيت</h3>
            <button
              onClick={() => { setOilForm({ ...oilForm, vehicleId: vehicles[0]?._id || '' }); setShowModal(true); }}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إضافة تغيير زيت</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المركبة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  نوع الزيت
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الماركة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الكمية
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التكلفة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسافة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  التالي
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {oilRecords.map((oil, index) => (
                <motion.tr
                  key={oil._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {oil.vehicleId?.plateNumber || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {oil.oilType === 'synthetic' ? 'اصطناعي' :
                     oil.oilType === 'conventional' ? 'تقليدي' :
                     oil.oilType === 'blend' ? 'مختلط' : oil.oilType}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {oil.oilBrand}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {oil.quantity} لتر
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {oil.cost?.toLocaleString() || 0} ريال
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {oil.mileage?.toLocaleString() || 0} كم
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {oil.nextOilChangeMileage?.toLocaleString() || 0} كم
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedRecord(oil)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Edit logic */}}
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

        {oilRecords.length === 0 && (
          <div className="text-center py-12">
            <Droplet className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">لا توجد سجلات تغيير زيت</p>
          </div>
        )}
      </div>
    </div>
  );

  const renderFuelTab = () => (
    <div className="space-y-6">
      {/* Fuel Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">استهلاك هذا الشهر</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.fuel?.thisMonth || 0} لتر
              </p>
            </div>
            <Battery className="w-8 h-8 text-blue-600" />
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
              <p className="text-sm text-gray-600">تكاليف الوقود</p>
              <p className="text-2xl font-bold text-red-600">
                {statistics?.fuel?.totalCost?.toLocaleString() || 0} ريال
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-red-600" />
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
              <p className="text-sm text-gray-600">متوسط السعر</p>
              <p className="text-2xl font-bold text-yellow-600">
                {statistics?.fuel?.avgPrice || 0} ريال/لتر
              </p>
            </div>
            <BarChart3 className="w-8 h-8 text-yellow-600" />
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
              <p className="text-sm text-gray-600">استهلاك/100كم</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.fuel?.consumptionRate || 0} لتر
              </p>
            </div>
            <Activity className="w-8 h-8 text-green-600" />
          </div>
        </motion.div>
      </div>

      {/* Fuel Records Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">سجلات التزود بالوقود</h3>
            <button
              onClick={() => { setFuelForm({ ...fuelForm, vehicleId: vehicles[0]?._id || '' }); setShowModal(true); }}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إضافة تزود بالوقود</span>
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المركبة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  نوع الوقود
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الكمية
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  السعر/لتر
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجمالي
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسافة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المحطة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {fuelRecords.map((fuel, index) => (
                <motion.tr
                  key={fuel._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {fuel.vehicleId?.plateNumber || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {fuel.fuelType === 'diesel' ? 'ديزل' :
                     fuel.fuelType === 'gasoline' ? 'بنزين' :
                     fuel.fuelType === 'natural_gas' ? 'غاز طبيعي' : fuel.fuelType}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {fuel.quantity} لتر
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {fuel.costPerLiter} ريال
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {fuel.totalCost?.toLocaleString() || 0} ريال
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {fuel.mileage?.toLocaleString() || 0} كم
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {fuel.station || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedRecord(fuel)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Edit logic */}}
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

        {fuelRecords.length === 0 && (
          <div className="text-center py-12">
            <Battery className="w-12 h-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">لا توجد سجلات تزود بالوقود</p>
          </div>
        )}
      </div>
    </div>
  );

  const renderModal = () => {
    if (!showModal) return null;

    let form, title, onSubmit;
    
    switch (activeTab) {
      case 'maintenance':
        form = maintenanceForm;
        title = 'إضافة صيانة';
        onSubmit = handleSubmitMaintenance;
        break;
      case 'tires':
        form = tireForm;
        title = 'إضافة إطار';
        onSubmit = handleSubmitTire;
        break;
      case 'oil':
        form = oilForm;
        title = 'إضافة تغيير زيت';
        onSubmit = handleSubmitOil;
        break;
      case 'fuel':
        form = fuelForm;
        title = 'إضافة تزود بالوقود';
        onSubmit = handleSubmitFuel;
        break;
      default:
        return null;
    }

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        >
          <div className="p-6 border-b">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold">{title}</h2>
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
                  المركبة
                </label>
                <select
                  value={form.vehicleId}
                  onChange={(e) => {
                    if (activeTab === 'maintenance') setMaintenanceForm({ ...maintenanceForm, vehicleId: e.target.value });
                    else if (activeTab === 'tires') setTireForm({ ...tireForm, vehicleId: e.target.value });
                    else if (activeTab === 'oil') setOilForm({ ...oilForm, vehicleId: e.target.value });
                    else if (activeTab === 'fuel') setFuelForm({ ...fuelForm, vehicleId: e.target.value });
                  }}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">اختر المركبة</option>
                  {vehicles.map((vehicle) => (
                    <option key={vehicle._id} value={vehicle._id}>
                      {vehicle.plateNumber} - {vehicle.make} {vehicle.model}
                    </option>
                  ))}
                </select>
              </div>

              {activeTab === 'maintenance' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      نوع الصيانة
                    </label>
                    <select
                      value={form.type}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, type: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="routine">روتينية</option>
                      <option value="emergency">طوارئ</option>
                      <option value="preventive">وقائية</option>
                      <option value="repair">إصلاح</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      التكلفة (ريال)
                    </label>
                    <input
                      type="number"
                      value={form.cost}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, cost: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      المسافة (كم)
                    </label>
                    <input
                      type="number"
                      value={form.mileage}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, mileage: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      تاريخ الصيانة القادمة
                    </label>
                    <input
                      type="date"
                      value={form.nextMaintenanceDate}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, nextMaintenanceDate: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الوصف
                    </label>
                    <textarea
                      value={form.description}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, description: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      rows="3"
                      required
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      ملاحظات
                    </label>
                    <textarea
                      value={form.notes}
                      onChange={(e) => setMaintenanceForm({ ...maintenanceForm, notes: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      rows="2"
                      placeholder="اختياري"
                    />
                  </div>
                </>
              )}

              {activeTab === 'tires' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      موقع الإطار
                    </label>
                    <select
                      value={form.position}
                      onChange={(e) => setTireForm({ ...tireForm, position: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="front_left">أمامي يسار</option>
                      <option value="front_right">أمامي يمين</option>
                      <option value="rear_left">خلفي يسار</option>
                      <option value="rear_right">خلفي يمين</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الماركة
                    </label>
                    <input
                      type="text"
                      value={form.brand}
                      onChange={(e) => setTireForm({ ...tireForm, brand: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الموديل
                    </label>
                    <input
                      type="text"
                      value={form.model}
                      onChange={(e) => setTireForm({ ...tireForm, model: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      تاريخ الشراء
                    </label>
                    <input
                      type="date"
                      value={form.purchaseDate}
                      onChange={(e) => setTireForm({ ...tireForm, purchaseDate: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      تكلفة الشراء (ريال)
                    </label>
                    <input
                      type="number"
                      value={form.purchaseCost}
                      onChange={(e) => setTireForm({ ...tireForm, purchaseCost: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الحالة
                    </label>
                    <select
                      value={form.condition}
                      onChange={(e) => setTireForm({ ...tireForm, condition: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="excellent">ممتاز</option>
                      <option value="good">جيد</option>
                      <option value="fair">متوسط</option>
                      <option value="poor">سيء</option>
                    </select>
                  </div>
                </>
              )}

              {activeTab === 'oil' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      نوع الزيت
                    </label>
                    <select
                      value={form.oilType}
                      onChange={(e) => setOilForm({ ...oilForm, oilType: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="synthetic">اصطناعي</option>
                      <option value="conventional">تقليدي</option>
                      <option value="blend">مختلط</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      ماركة الزيت
                    </label>
                    <input
                      type="text"
                      value={form.oilBrand}
                      onChange={(e) => setOilForm({ ...oilForm, oilBrand: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الكمية (لتر)
                    </label>
                    <input
                      type="number"
                      value={form.quantity}
                      onChange={(e) => setOilForm({ ...oilForm, quantity: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                      step="0.1"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      التكلفة (ريال)
                    </label>
                    <input
                      type="number"
                      value={form.cost}
                      onChange={(e) => setOilForm({ ...oilForm, cost: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      المسافة الحالية (كم)
                    </label>
                    <input
                      type="number"
                      value={form.mileage}
                      onChange={(e) => setOilForm({ ...oilForm, mileage: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      المسافة التالية (كم)
                    </label>
                    <input
                      type="number"
                      value={form.nextOilChangeMileage}
                      onChange={(e) => setOilForm({ ...oilForm, nextOilChangeMileage: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>
                </>
              )}

              {activeTab === 'fuel' && (
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      نوع الوقود
                    </label>
                    <select
                      value={form.fuelType}
                      onChange={(e) => setFuelForm({ ...fuelForm, fuelType: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="diesel">ديزل</option>
                      <option value="gasoline">بنزين</option>
                      <option value="natural_gas">غاز طبيعي</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الكمية (لتر)
                    </label>
                    <input
                      type="number"
                      value={form.quantity}
                      onChange={(e) => setFuelForm({ ...fuelForm, quantity: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                      step="0.1"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      السعر للتر (ريال)
                    </label>
                    <input
                      type="number"
                      value={form.costPerLiter}
                      onChange={(e) => setFuelForm({ ...fuelForm, costPerLiter: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                      step="0.01"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      الإجمالي (ريال)
                    </label>
                    <input
                      type="number"
                      value={form.totalCost}
                      onChange={(e) => setFuelForm({ ...fuelForm, totalCost: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                      step="0.01"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      المسافة (كم)
                    </label>
                    <input
                      type="number"
                      value={form.mileage}
                      onChange={(e) => setFuelForm({ ...fuelForm, mileage: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                      min="0"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      تاريخ التزود
                    </label>
                    <input
                      type="date"
                      value={form.fuelingDate}
                      onChange={(e) => setFuelForm({ ...fuelForm, fuelingDate: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      محطة الوقود
                    </label>
                    <input
                      type="text"
                      value={form.station}
                      onChange={(e) => setFuelForm({ ...fuelForm, station: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      placeholder="اختياري"
                    />
                  </div>
                </>
              )}
            </div>

            <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
              <button
                onClick={() => setShowModal(false)}
                className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              >
                إلغاء
              </button>
              <button
                onClick={onSubmit}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                حفظ
              </button>
            </div>
          </div>
        </motion.div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل بيانات الورشة...</p>
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
            <Wrench className="w-8 h-8 text-blue-600" />
            <div>
              <h1 className="text-2xl font-bold text-gray-900">ورشة الصيانة</h1>
              <p className="text-sm text-gray-600">إدارة صيانة الأسطول والإطارات والوقود</p>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8 space-x-reverse">
            {[
              { id: 'maintenance', label: 'الصيانة', icon: Wrench },
              { id: 'tires', label: 'الإطارات', icon: Settings },
              { id: 'oil', label: 'تغيير الزيت', icon: Droplet },
              { id: 'fuel', label: 'الوقود', icon: Battery }
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
        {activeTab === 'maintenance' && renderMaintenanceTab()}
        {activeTab === 'tires' && renderTiresTab()}
        {activeTab === 'oil' && renderOilTab()}
        {activeTab === 'fuel' && renderFuelTab()}
      </div>

      {/* Modal */}
      {renderModal()}

      {/* Details Modal */}
      {selectedRecord && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">تفاصيل السجل</h2>
                <button
                  onClick={() => setSelectedRecord(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="space-y-4">
                {Object.entries(selectedRecord).map(([key, value]) => {
                  if (key === '_id' || key === '__v' || key === 'createdAt' || key === 'updatedAt') return null;
                  
                  let displayValue = value;
                  if (typeof value === 'object' && value !== null) {
                    displayValue = value.plateNumber || value.name || JSON.stringify(value);
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
                  onClick={() => setSelectedRecord(null)}
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

export default WorkshopPage;
