/**
 * ============================================
 * 📊 Edham Logistics - Admin Dashboard Page
 * نظام إدهام - لوحات التحكم الإدارية
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Users, Truck, DollarSign, AlertCircle, MapPin, Navigation,
  Calendar, TrendingUp, TrendingDown, Wrench, FileText,
  Clock, CheckCircle, XCircle, Settings, LogOut, Bell,
  Filter, Search, Download, Eye, Edit, Trash2, Plus,
  ChevronLeft, ChevronRight, Home, BarChart3, Settings2,
  Activity, Package, CreditCard, AlertTriangle
} from 'lucide-react';
import './AdminDashboardPage.css';

const AdminDashboardPage = () => {
  const [activeDashboard, setActiveDashboard] = useState('supervisor');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [dateRange, setDateRange] = useState({ start: '', end: '' });
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [selectedDriver, setSelectedDriver] = useState(null);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showMaintenanceModal, setShowMaintenanceModal] = useState(false);

  // Dashboard data states
  const [supervisorData, setSupervisorData] = useState(null);
  const [accountantData, setAccountantData] = useState(null);
  const [workshopData, setWorkshopData] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, [activeDashboard, searchTerm, dateRange]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    
    try {
      const endpoint = `/api/v1/admin/${activeDashboard}/dashboard`;
      const params = new URLSearchParams();
      
      if (searchTerm) params.append('search', searchTerm);
      if (dateRange.start) params.append('startDate', dateRange.start);
      if (dateRange.end) params.append('endDate', dateRange.end);
      
      const response = await fetch(`${endpoint}?${params}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        
        switch (activeDashboard) {
          case 'supervisor':
            setSupervisorData(data.data);
            break;
          case 'accountant':
            setAccountantData(data.data);
            break;
          case 'workshop':
            setWorkshopData(data.data);
            break;
        }
      } else {
        setError('Failed to fetch dashboard data');
      }
    } catch (err) {
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleAssignOrder = async (orderId, driverId) => {
    try {
      const response = await fetch('/api/v1/admin/supervisor/assign-order', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({ orderId, driverId })
      });

      if (response.ok) {
        setShowAssignModal(false);
        fetchDashboardData();
      }
    } catch (err) {
      setError('Failed to assign order');
    }
  };

  const handleScheduleMaintenance = async (maintenanceData) => {
    try {
      const response = await fetch('/api/v1/admin/workshop/maintenance', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(maintenanceData)
      });

      if (response.ok) {
        setShowMaintenanceModal(false);
        fetchDashboardData();
      }
    } catch (err) {
      setError('Failed to schedule maintenance');
    }
  };

  // Supervisor Dashboard Component
  const SupervisorDashboard = () => (
    <div className="supervisor-dashboard">
      <div className="dashboard-header">
        <h2>غرفة العمليات</h2>
        <div className="header-actions">
          <div className="search-bar">
            <Search className="w-5 h-5" />
            <input
              type="text"
              placeholder="البحث عن طلبات..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <button className="refresh-btn" onClick={fetchDashboardData}>
            <Activity className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Fleet Statistics */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon blue">
            <Users className="w-8 h-8" />
          </div>
          <div className="stat-info">
            <h3>{supervisorData?.fleet_stats?.total_drivers || 0}</h3>
            <p>إجمالي السائقين</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon green">
            <Navigation className="w-8 h-8" />
          </div>
          <div className="stat-info">
            <h3>{supervisorData?.fleet_stats?.active_drivers || 0}</h3>
            <p>سائقين نشطين</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon orange">
            <Truck className="w-8 h-8" />
          </div>
          <div className="stat-info">
            <h3>{supervisorData?.fleet_stats?.available_drivers || 0}</h3>
            <p>سائقين متاحين</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon purple">
            <Package className="w-8 h-8" />
          </div>
          <div className="stat-info">
            <h3>{supervisorData?.active_orders?.length || 0}</h3>
            <p>طلبات نشطة</p>
          </div>
        </div>
      </div>

      {/* Live Fleet Map */}
      <div className="fleet-map-container">
        <h3>خريطة الأسطول المباشرة</h3>
        <div className="map-placeholder">
          <div className="map-legend">
            <div className="legend-item">
              <div className="legend-dot green"></div>
              <span>متاح</span>
            </div>
            <div className="legend-item">
              <div className="legend-dot blue"></div>
              <span>نشط</span>
            </div>
            <div className="legend-item">
              <div className="legend-dot gray"></div>
              <span>غير متاح</span>
            </div>
          </div>
          {/* Map would be rendered here with actual mapping library */}
          <div className="fleet-markers">
            {supervisorData?.fleet_locations?.map((driver, index) => (
              <div
                key={index}
                className={`fleet-marker ${driver.is_available ? 'available' : 'busy'}`}
                style={{
                  position: 'absolute',
                  left: `${20 + (index * 15)}%`,
                  top: `${20 + (index * 10)}%`
                }}
              >
                <Truck className="w-6 h-6" />
                <span>{driver.name}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Pending Orders */}
      <div className="pending-orders-container">
        <h3>الطلبات المعلقة</h3>
        <div className="orders-grid">
          {supervisorData?.pending_orders?.map((order) => (
            <motion.div
              key={order._id}
              className="order-card"
              whileHover={{ scale: 1.02 }}
              onClick={() => {
                setSelectedOrder(order);
                setShowAssignModal(true);
              }}
            >
              <div className="order-header">
                <span className="order-number">#{order.order_number}</span>
                <span className="order-status pending">معلق</span>
              </div>
              <div className="order-route">
                <div className="route-point">
                  <MapPin className="w-4 h-4" />
                  <span>{order.route?.pickup?.address}</span>
                </div>
                <div className="route-arrow">→</div>
                <div className="route-point">
                  <MapPin className="w-4 h-4" />
                  <span>{order.route?.dropoff?.address}</span>
                </div>
              </div>
              <div className="order-info">
                <span>{order.route?.distance_km} كم</span>
                <span>{order.invoice?.total_amount} ريال</span>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* Assign Order Modal */}
      <AnimatePresence>
        {showAssignModal && selectedOrder && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="modal-overlay"
            onClick={() => setShowAssignModal(false)}
          >
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              className="modal-content"
              onClick={(e) => e.stopPropagation()}
            >
              <h3>تعيين سائق للطلب #{selectedOrder.order_number}</h3>
              <div className="driver-suggestions">
                {supervisorData?.available_drivers?.slice(0, 3).map((driver) => (
                  <div key={driver._id} className="driver-suggestion">
                    <div className="driver-info">
                      <h4>{driver.name}</h4>
                      <p>{driver.phone}</p>
                      <div className="driver-stats">
                        <span>تقييم: {driver.performance?.average_rating || 'N/A'}/5</span>
                        <span>معدل التسليم: {driver.performance?.on_time_delivery_rate || 0}%</span>
                      </div>
                    </div>
                    <div className="driver-vehicle">
                      <p>{driver.assigned_vehicle?.truck_id?.plate_number}</p>
                      <p>{driver.assigned_vehicle?.truck_id?.model}</p>
                    </div>
                    <button
                      className="assign-btn"
                      onClick={() => handleAssignOrder(selectedOrder._id, driver._id)}
                    >
                      تعيين
                    </button>
                  </div>
                ))}
              </div>
              <button className="close-btn" onClick={() => setShowAssignModal(false)}>
                <XCircle className="w-5 h-5" />
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );

  // Accountant Dashboard Component
  const AccountantDashboard = () => (
    <div className="accountant-dashboard">
      <div className="dashboard-header">
        <h2>الإدارة المالية</h2>
        <div className="header-actions">
          <div className="date-filters">
            <input
              type="date"
              value={dateRange.start}
              onChange={(e) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
            />
            <span>إلى</span>
            <input
              type="date"
              value={dateRange.end}
              onChange={(e) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
            />
          </div>
          <button className="export-btn">
            <Download className="w-5 h-5" />
            تصدير
          </button>
        </div>
      </div>

      {/* Financial Metrics */}
      <div className="financial-metrics">
        <div className="metric-card revenue">
          <div className="metric-icon">
            <DollarSign className="w-8 h-8" />
          </div>
          <div className="metric-info">
            <h3>{accountantData?.financial_metrics?.total_revenue?.toFixed(2) || 0}</h3>
            <p>إجمالي الإيرادات (ريال)</p>
            <div className="metric-trend">
              <TrendingUp className="w-4 h-4" />
              <span>+12.5%</span>
            </div>
          </div>
        </div>
        
        <div className="metric-card orders">
          <div className="metric-icon">
            <Package className="w-8 h-8" />
          </div>
          <div className="metric-info">
            <h3>{accountantData?.financial_metrics?.total_orders || 0}</h3>
            <p>إجمالي الطلبات</p>
            <div className="metric-trend">
              <TrendingUp className="w-4 h-4" />
              <span>+8.3%</span>
            </div>
          </div>
        </div>

        <div className="metric-card paid">
          <div className="metric-icon">
            <CheckCircle className="w-8 h-8" />
          </div>
          <div className="metric-info">
            <h3>{accountantData?.financial_metrics?.paid_orders || 0}</h3>
            <p>الطلبات المدفوعة</p>
            <div className="metric-trend">
              <TrendingUp className="w-4 h-4" />
              <span>+15.2%</span>
            </div>
          </div>
        </div>

        <div className="metric-card pending">
          <div className="metric-icon">
            <Clock className="w-8 h-8" />
          </div>
          <div className="metric-info">
            <h3>{accountantData?.financial_metrics?.pending_orders || 0}</h3>
            <p>الطلبات المعلقة</p>
            <div className="metric-trend">
              <TrendingDown className="w-4 h-4" />
              <span>-3.1%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Revenue Chart */}
      <div className="revenue-chart">
        <h3>اتجاه الإيرادات اليومية</h3>
        <div className="chart-placeholder">
          {/* Chart would be rendered here with actual charting library */}
          <div className="chart-bars">
            {accountantData?.daily_revenue?.slice(-7).map((day, index) => (
              <div key={index} className="chart-bar">
                <div
                  className="bar"
                  style={{ height: `${(day.revenue / Math.max(...accountantData.daily_revenue.map(d => d.revenue))) * 100}%` }}
                ></div>
                <span>{new Date(day._id).toLocaleDateString('ar-SA', { day: 'numeric' })}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Invoices Table */}
      <div className="invoices-table">
        <h3>سجل الفواتير</h3>
        <div className="table-filters">
          <select onChange={(e) => setDateRange(prev => ({ ...prev, paymentStatus: e.target.value }))}>
            <option value="">كل الحالات</option>
            <option value="PAID">مدفوع</option>
            <option value="PENDING">معلق</option>
            <option value="FAILED">فشل</option>
          </select>
        </div>
        
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>رقم الفاتورة</th>
                <th>العميل</th>
                <th>التاريخ</th>
                <th>المبلغ</th>
                <th>الحالة</th>
                <th>طريقة الدفع</th>
                <th>الإجراءات</th>
              </tr>
            </thead>
            <tbody>
              {accountantData?.recent_invoices?.map((invoice) => (
                <tr key={invoice._id}>
                  <td>#{invoice.order_number}</td>
                  <td>{invoice.customer_id?.name}</td>
                  <td>{new Date(invoice.created_at).toLocaleDateString('ar-SA')}</td>
                  <td>{invoice.invoice?.total_amount} ريال</td>
                  <td>
                    <span className={`status-badge ${invoice.invoice?.payment_status?.toLowerCase()}`}>
                      {invoice.invoice?.payment_status}
                    </span>
                  </td>
                  <td>{invoice.invoice?.payment_method}</td>
                  <td>
                    <div className="table-actions">
                      <button className="action-btn view">
                        <Eye className="w-4 h-4" />
                      </button>
                      <button className="action-btn download">
                        <Download className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  // Workshop Dashboard Component
  const WorkshopDashboard = () => (
    <div className="workshop-dashboard">
      <div className="dashboard-header">
        <h2>إدارة الأسطول والصيانة</h2>
        <div className="header-actions">
          <button
            className="add-maintenance-btn"
            onClick={() => setShowMaintenanceModal(true)}
          >
            <Plus className="w-5 h-5" />
            جدولة صيانة
          </button>
        </div>
      </div>

      {/* Fleet Health */}
      <div className="fleet-health">
        <h3>حالة الأسطول</h3>
        <div className="health-grid">
          <div className="health-card available">
            <div className="health-icon">
              <CheckCircle className="w-8 h-8" />
            </div>
            <div className="health-info">
              <h3>{workshopData?.fleet_health?.available_trucks || 0}</h3>
              <p>شاحنات متاحة</p>
            </div>
          </div>
          
          <div className="health-card in-use">
            <div className="health-icon">
              <Navigation className="w-8 h-8" />
            </div>
            <div className="health-info">
              <h3>{workshopData?.fleet_health?.in_use_trucks || 0}</h3>
              <p>شاحنات قيد الاستخدام</p>
            </div>
          </div>

          <div className="health-card maintenance">
            <div className="health-icon">
              <Wrench className="w-8 h-8" />
            </div>
            <div className="health-info">
              <h3>{workshopData?.fleet_health?.maintenance_trucks || 0}</h3>
              <p>شاحنات تحت الصيانة</p>
            </div>
          </div>

          <div className="health-card out-of-service">
            <div className="health-icon">
              <XCircle className="w-8 h-8" />
            </div>
            <div className="health-info">
              <h3>{workshopData?.fleet_health?.out_of_service_trucks || 0}</h3>
              <p>شاحنات خارج الخدمة</p>
            </div>
          </div>
        </div>
      </div>

      {/* Maintenance Alerts */}
      <div className="maintenance-alerts">
        <h3>تنبيهات الصيانة</h3>
        <div className="alerts-grid">
          {workshopData?.maintenance_alerts?.map((alert, index) => (
            <div key={index} className="alert-card">
              <div className="alert-header">
                <div className="alert-truck">
                  <Truck className="w-5 h-5" />
                  <span>{alert.truckNumber}</span>
                  <span>{alert.plateNumber}</span>
                </div>
                <div className={`alert-severity ${getAlertSeverity(alert)}`}>
                  <AlertTriangle className="w-5 h-5" />
                  <span>{getAlertSeverityText(alert)}</span>
                </div>
              </div>
              <div className="alert-details">
                {alert.days_until_oil_change !== undefined && (
                  <div className="alert-item">
                    <Wrench className="w-4 h-4" />
                    <span>تغيير الزيت: {alert.days_until_oil_change} يوم</span>
                  </div>
                )}
                {alert.days_until_insurance !== undefined && (
                  <div className="alert-item">
                    <FileText className="w-4 h-4" />
                    <span>التأمين: {alert.days_until_insurance} يوم</span>
                  </div>
                )}
                {alert.days_until_registration !== undefined && (
                  <div className="alert-item">
                    <CreditCard className="w-4 h-4" />
                    <span>التسجيل: {alert.days_until_registration} يوم</span>
                  </div>
                )}
              </div>
              <div className="alert-actions">
                <button className="alert-btn schedule">جدولة صيانة</button>
                <button className="alert-btn details">التفاصيل</button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Parts Inventory */}
      <div className="parts-inventory">
        <h3>مخزون القطع</h3>
        <div className="inventory-table">
          <table>
            <thead>
              <tr>
                <th>القطعة</th>
                <th>الكمية الحالية</th>
                <th>الحد الأدنى</th>
                <th>سعر الوحدة</th>
                <th>الحالة</th>
                <th>الإجراءات</th>
              </tr>
            </thead>
            <tbody>
              {workshopData?.parts_inventory?.map((part, index) => (
                <tr key={index}>
                  <td>{part.name}</td>
                  <td>{part.quantity}</td>
                  <td>{part.min_stock}</td>
                  <td>{part.unit_cost} ريال</td>
                  <td>
                    <span className={`stock-status ${part.quantity <= part.min_stock ? 'low' : 'good'}`}>
                      {part.quantity <= part.min_stock ? 'منخفض' : 'جيد'}
                    </span>
                  </td>
                  <td>
                    <div className="table-actions">
                      <button className="action-btn edit">
                        <Edit className="w-4 h-4" />
                      </button>
                      <button className="action-btn order">
                        <Plus className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Maintenance Modal */}
      <AnimatePresence>
        {showMaintenanceModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="modal-overlay"
            onClick={() => setShowMaintenanceModal(false)}
          >
            <motion.div
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
              className="modal-content maintenance-modal"
              onClick={(e) => e.stopPropagation()}
            >
              <h3>جدولة صيانة جديدة</h3>
              <form className="maintenance-form">
                <div className="form-row">
                  <div className="form-group">
                    <label>الشاحنة</label>
                    <select>
                      {workshopData?.maintenance_alerts?.map((alert, index) => (
                        <option key={index} value={alert._id}>
                          {alert.truckNumber} - {alert.plateNumber}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>نوع الصيانة</label>
                    <select>
                      <option value="oil_change">تغيير الزيت</option>
                      <option value="tire_change">تغيير الإطارات</option>
                      <option value="brake_service">صيانة الفرامل</option>
                      <option value="engine_repair">إصلاح المحرك</option>
                      <option value="inspection">فحص دوري</option>
                    </select>
                  </div>
                </div>
                
                <div className="form-row">
                  <div className="form-group">
                    <label>التاريخ</label>
                    <input type="date" />
                  </div>
                  <div className="form-group">
                    <label>الأولوية</label>
                    <select>
                      <option value="low">منخفضة</option>
                      <option value="normal">عادية</option>
                      <option value="high">عالية</option>
                      <option value="urgent">طارئة</option>
                    </select>
                  </div>
                </div>

                <div className="form-group">
                  <label>الوصف</label>
                  <textarea rows={3} placeholder="وصف الصيانة المطلوبة..."></textarea>
                </div>

                <div className="form-actions">
                  <button type="button" className="cancel-btn" onClick={() => setShowMaintenanceModal(false)}>
                    إلغاء
                  </button>
                  <button type="button" className="submit-btn" onClick={() => setShowMaintenanceModal(false)}>
                    جدولة
                  </button>
                </div>
              </form>
              
              <button className="close-btn" onClick={() => setShowMaintenanceModal(false)}>
                <XCircle className="w-5 h-5" />
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );

  const getAlertSeverity = (alert) => {
    if (alert.status === 'maintenance' || alert.status === 'out_of_service') return 'critical';
    if (alert.days_until_oil_change <= 0 || alert.days_until_insurance <= 0 || alert.days_until_registration <= 0) return 'critical';
    if (alert.days_until_oil_change <= 7 || alert.days_until_insurance <= 7 || alert.days_until_registration <= 7) return 'warning';
    return 'info';
  };

  const getAlertSeverityText = (alert) => {
    const severity = getAlertSeverity(alert);
    switch (severity) {
      case 'critical': return 'حرج';
      case 'warning': return 'تحذير';
      case 'info': return 'معلومات';
      default: return 'عادي';
    }
  };

  const renderDashboard = () => {
    switch (activeDashboard) {
      case 'supervisor':
        return <SupervisorDashboard />;
      case 'accountant':
        return <AccountantDashboard />;
      case 'workshop':
        return <WorkshopDashboard />;
      default:
        return <SupervisorDashboard />;
    }
  };

  if (loading) {
    return (
      <div className="admin-dashboard-loading">
        <div className="loading-spinner"></div>
        <p>جاري تحميل البيانات...</p>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      {/* Sidebar Navigation */}
      <div className="admin-sidebar">
        <div className="sidebar-header">
          <Truck className="w-8 h-8" />
          <h1>إدهام - الإدارة</h1>
        </div>
        
        <nav className="sidebar-nav">
          <button
            className={`nav-item ${activeDashboard === 'supervisor' ? 'active' : ''}`}
            onClick={() => setActiveDashboard('supervisor')}
          >
            <Navigation className="w-5 h-5" />
            <span>غرفة العمليات</span>
          </button>
          
          <button
            className={`nav-item ${activeDashboard === 'accountant' ? 'active' : ''}`}
            onClick={() => setActiveDashboard('accountant')}
          >
            <DollarSign className="w-5 h-5" />
            <span>الإدارة المالية</span>
          </button>
          
          <button
            className={`nav-item ${activeDashboard === 'workshop' ? 'active' : ''}`}
            onClick={() => setActiveDashboard('workshop')}
          >
            <Wrench className="w-5 h-5" />
            <span>الورشة والصيانة</span>
          </button>
        </nav>
        
        <div className="sidebar-footer">
          <button className="nav-item">
            <Settings className="w-5 h-5" />
            <span>الإعدادات</span>
          </button>
          <button className="nav-item logout">
            <LogOut className="w-5 h-5" />
            <span>تسجيل الخروج</span>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="admin-main">
        {/* Error Message */}
        {error && (
          <div className="error-message">
            <AlertCircle className="w-5 h-5" />
            <span>{error}</span>
            <button onClick={() => setError('')}>
              <XCircle className="w-5 h-5" />
            </button>
          </div>
        )}

        {/* Dashboard Content */}
        <AnimatePresence mode="wait">
          {renderDashboard()}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
