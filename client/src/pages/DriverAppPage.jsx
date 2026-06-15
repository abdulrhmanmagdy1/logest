/**
 * ============================================
 * 🚛 Edham Logistics - Driver App Page
 * نظام إدهام - تطبيق السائق
 * ============================================
 */

import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Truck, MapPin, Navigation, Phone, Camera, Upload, 
  CheckCircle, Clock, AlertCircle, Package, DollarSign,
  Power, User, Settings, Star, MessageCircle, ChevronRight,
  ChevronLeft, FileText, Clipboard, Check, X
} from 'lucide-react';
import io from 'socket.io-client';
import './DriverAppPage.css';

const DriverAppPage = () => {
  const [currentView, setCurrentView] = useState('dashboard');
  const [driverStatus, setDriverStatus] = useState('OFF_DUTY');
  const [isAvailable, setIsAvailable] = useState(false);
  const [currentTask, setCurrentTask] = useState(null);
  const [location, setLocation] = useState(null);
  const [socket, setSocket] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [uploadingFiles, setUploadingFiles] = useState(false);
  const [uploadedFiles, setUploadedFiles] = useState([]);
  const [surveyData, setSurveyData] = useState({});
  const [offlineData, setOfflineData] = useState({
    locationUpdates: [],
    statusUpdates: []
  });
  
  const fileInputRef = useRef(null);
  const locationWatchRef = useRef(null);

  // Initialize socket connection
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) return;

    const newSocket = io(process.env.REACT_APP_SOCKET_URL || 'http://localhost:5000', {
      auth: { token },
      transports: ['websocket', 'polling']
    });

    newSocket.on('connect', () => {
      setIsConnected(true);
      console.log('Driver connected to WebSocket');
    });

    newSocket.on('disconnect', () => {
      setIsConnected(false);
      console.log('Driver disconnected from WebSocket');
    });

    newSocket.on('task_assigned', (data) => {
      setCurrentTask(data);
      setCurrentView('task_assigned');
    });

    newSocket.on('message_from_customer', (data) => {
      console.log('Message from customer:', data);
      // Handle customer messages
    });

    newSocket.on('system_notification', (data) => {
      console.log('System notification:', data);
      // Handle system notifications
    });

    setSocket(newSocket);

    return () => {
      if (locationWatchRef.current) {
        navigator.geolocation.clearWatch(locationWatchRef.current);
      }
      newSocket.disconnect();
    };
  }, []);

  // Start location tracking
  useEffect(() => {
    if (isAvailable && isConnected) {
      startLocationTracking();
    } else {
      stopLocationTracking();
    }
  }, [isAvailable, isConnected]);

  const startLocationTracking = () => {
    if (!navigator.geolocation) {
      console.error('Geolocation not supported');
      return;
    }

    locationWatchRef.current = navigator.geolocation.watchPosition(
      (position) => {
        const locationData = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: position.coords.accuracy,
          speed: position.coords.speed,
          heading: position.coords.heading,
          timestamp: new Date()
        };

        setLocation(locationData);

        // Send to server
        if (socket && isConnected) {
          socket.emit('update_location', locationData);
        } else {
          // Store for offline sync
          setOfflineData(prev => ({
            ...prev,
            locationUpdates: [...prev.locationUpdates, locationData]
          }));
        }
      },
      (error) => {
        console.error('Location error:', error);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 5000
      }
    );
  };

  const stopLocationTracking = () => {
    if (locationWatchRef.current) {
      navigator.geolocation.clearWatch(locationWatchRef.current);
      locationWatchRef.current = null;
    }
  };

  const toggleAvailability = async () => {
    try {
      const newStatus = isAvailable ? 'OFF_DUTY' : 'ON_DUTY';
      const newAvailability = !isAvailable;

      const response = await fetch('/api/v1/drivers/status', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          is_available: newAvailability,
          status: newStatus
        })
      });

      if (response.ok) {
        setDriverStatus(newStatus);
        setIsAvailable(newAvailability);
      }
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  const respondToTask = async (response, reason = '') => {
    try {
      const res = await fetch('/api/v1/drivers/task-response', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          order_id: currentTask.order_id,
          response,
          reason
        })
      });

      if (res.ok) {
        if (response === 'accept') {
          setCurrentView('task_active');
        } else {
          setCurrentTask(null);
          setCurrentView('dashboard');
        }
      }
    } catch (error) {
      console.error('Error responding to task:', error);
    }
  };

  const updateTaskStatus = async (newStatus, notes = '') => {
    try {
      const res = await fetch(`/api/v1/drivers/orders/${currentTask.order_id}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          new_status: newStatus,
          notes,
          location: location
        })
      });

      if (res.ok) {
        // Update current task state
        setCurrentTask(prev => ({
          ...prev,
          task_state: newStatus
        }));
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const handleFileUpload = async (files) => {
    setUploadingFiles(true);
    const formData = new FormData();
    
    Array.from(files).forEach(file => {
      formData.append('files', file);
    });

    try {
      const response = await fetch(`/api/v1/drivers/orders/${currentTask.order_id}/proof-of-delivery`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });

      if (response.ok) {
        const data = await response.json();
        setUploadedFiles(prev => [...prev, ...data.data.uploaded_files]);
      }
    } catch (error) {
      console.error('Error uploading files:', error);
    } finally {
      setUploadingFiles(false);
    }
  };

  const submitSurvey = async () => {
    try {
      const response = await fetch(`/api/v1/drivers/orders/${currentTask.order_id}/survey`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(surveyData)
      });

      if (response.ok) {
        setCurrentTask(null);
        setCurrentView('dashboard');
        setSurveyData({});
      }
    } catch (error) {
      console.error('Error submitting survey:', error);
    }
  };

  const syncOfflineData = async () => {
    if (offlineData.locationUpdates.length === 0 && offlineData.statusUpdates.length === 0) {
      return;
    }

    try {
      const response = await fetch('/api/v1/drivers/sync-offline', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(offlineData)
      });

      if (response.ok) {
        setOfflineData({ locationUpdates: [], statusUpdates: [] });
      }
    } catch (error) {
      console.error('Error syncing offline data:', error);
    }
  };

  // Dashboard View
  const DashboardView = () => (
    <div className="driver-dashboard">
      <div className="dashboard-header">
        <div className="status-toggle">
          <div className={`toggle-button ${isAvailable ? 'active' : ''}`} onClick={toggleAvailability}>
            <Power className="w-8 h-8" />
            <span>{isAvailable ? 'متاحد' : 'غير متاح'}</span>
          </div>
        </div>
        <div className="connection-status">
          <div className={`status-indicator ${isConnected ? 'connected' : 'disconnected'}`}></div>
          <span>{isConnected ? 'متصل' : 'غير متصل'}</span>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="stats-grid">
          <div className="stat-card">
            <Package className="w-8 h-8" />
            <div className="stat-info">
              <span className="stat-value">0</span>
              <span className="stat-label">طلبات اليوم</span>
            </div>
          </div>
          <div className="stat-card">
            <DollarSign className="w-8 h-8" />
            <div className="stat-info">
              <span className="stat-value">0</span>
              <span className="stat-label">الإيرادات</span>
            </div>
          </div>
          <div className="stat-card">
            <Star className="w-8 h-8" />
            <div className="stat-info">
              <span className="stat-value">4.8</span>
              <span className="stat-label">التقييم</span>
            </div>
          </div>
        </div>

        <div className="quick-actions">
          <button className="action-button">
            <User className="w-6 h-6" />
            <span>الملف الشخصي</span>
          </button>
          <button className="action-button">
            <Settings className="w-6 h-6" />
            <span>الإعدادات</span>
          </button>
          <button className="action-button" onClick={syncOfflineData}>
            <Navigation className="w-6 h-6" />
            <span>مزامنة</span>
          </button>
        </div>
      </div>
    </div>
  );

  // Task Assigned View
  const TaskAssignedView = () => (
    <div className="task-assigned">
      <div className="task-header">
        <div className="task-alert">
          <AlertCircle className="w-12 h-12" />
          <h2>طلب جديد!</h2>
          <p>تم تعيين طلب جديد لك</p>
        </div>
      </div>

      {currentTask && (
        <div className="task-details">
          <div className="task-info">
            <h3>طلب #{currentTask.order_number}</h3>
            <div className="route-info">
              <div className="route-point">
                <MapPin className="w-6 h-6" />
                <span>{currentTask.route?.pickup?.address}</span>
              </div>
              <div className="route-arrow">→</div>
              <div className="route-point">
                <MapPin className="w-6 h-6" />
                <span>{currentTask.route?.dropoff?.address}</span>
              </div>
            </div>
            <div className="task-meta">
              <div className="meta-item">
                <Package className="w-5 h-5" />
                <span>{currentTask.cargo?.description}</span>
              </div>
              <div className="meta-item">
                <DollarSign className="w-5 h-5" />
                <span>{currentTask.invoice?.total_amount} ريال</span>
              </div>
            </div>
          </div>

          <div className="task-actions">
            <button 
              className="accept-button"
              onClick={() => respondToTask('accept')}
            >
              <CheckCircle className="w-6 h-6" />
              <span>قبول</span>
            </button>
            <button 
              className="reject-button"
              onClick={() => respondToTask('reject', 'Reason for rejection')}
            >
              <X className="w-6 h-6" />
              <span>رفض</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );

  // Active Task View
  const ActiveTaskView = () => {
    const taskStateButtons = {
      'TASK_ACCEPTED': [
        { state: 'HEADING_TO_PICKUP', label: 'في الطريق للاستلام', icon: <Navigation className="w-6 h-6" /> }
      ],
      'HEADING_TO_PICKUP': [
        { state: 'ARRIVED_AT_PICKUP', label: 'وصلت نقطة الاستلام', icon: <MapPin className="w-6 h-6" /> }
      ],
      'ARRIVED_AT_PICKUP': [
        { state: 'PICKUP_CONFIRMED', label: 'تأكيد الاستلام', icon: <Package className="w-6 h-6" /> }
      ],
      'PICKUP_CONFIRMED': [
        { state: 'LOADING_COMPLETE', label: 'إكمال التحميل', icon: <Truck className="w-6 h-6" /> }
      ],
      'LOADING_COMPLETE': [
        { state: 'HEADING_TO_DROPOFF', label: 'في الطريق للتسليم', icon: <Navigation className="w-6 h-6" /> }
      ],
      'HEADING_TO_DROPOFF': [
        { state: 'ARRIVED_AT_DROPOFF', label: 'وصلت نقطة التسليم', icon: <MapPin className="w-6 h-6" /> }
      ],
      'ARRIVED_AT_DROPOFF': [
        { state: 'UNLOADING_STARTED', label: 'بدء التفريغ', icon: <Package className="w-6 h-6" /> }
      ],
      'UNLOADING_STARTED': [
        { state: 'DELIVERY_CONFIRMED', label: 'تأكيد التسليم', icon: <CheckCircle className="w-6 h-6" /> }
      ]
    };

    const currentButtons = taskStateButtons[currentTask?.task_state] || [];

    return (
      <div className="active-task">
        <div className="task-header">
          <h2>مهمة نشطة</h2>
          <div className="task-status">
            <span className="status-badge">{currentTask?.task_state}</span>
          </div>
        </div>

        <div className="task-progress">
          <div className="progress-steps">
            {[
              'TASK_ACCEPTED',
              'HEADING_TO_PICKUP',
              'ARRIVED_AT_PICKUP',
              'PICKUP_CONFIRMED',
              'HEADING_TO_DROPOFF',
              'ARRIVED_AT_DROPOFF',
              'DELIVERY_CONFIRMED'
            ].map((step, index) => (
              <div 
                key={step}
                className={`progress-step ${getStepStatus(step, currentTask?.task_state)}`}
              >
                <div className="step-number">{index + 1}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="task-actions">
          {currentButtons.map((button, index) => (
            <button
              key={index}
              className="task-button primary"
              onClick={() => updateTaskStatus(button.state)}
            >
              {button.icon}
              <span>{button.label}</span>
            </button>
          ))}

          {currentTask?.task_state === 'DELIVERY_CONFIRMED' && (
            <button 
              className="task-button secondary"
              onClick={() => setCurrentView('proof_of_delivery')}
            >
              <Camera className="w-6 h-6" />
              <span>إثبات التسليم</span>
            </button>
          )}
        </div>

        <div className="task-info">
          <div className="info-card">
            <h3>معلومات الطلب</h3>
            <p>#{currentTask?.order_number}</p>
            <p>{currentTask?.route?.pickup?.address} → {currentTask?.route?.dropoff?.address}</p>
          </div>
        </div>
      </div>
    );
  };

  // Proof of Delivery View
  const ProofOfDeliveryView = () => (
    <div className="proof-of-delivery">
      <div className="pod-header">
        <h2>إثبات التسليم</h2>
        <p>ارفع المستندات والصور لإثبات التسليم</p>
      </div>

      <div className="upload-area">
        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept="image/*,.pdf,.doc,.docx"
          onChange={(e) => handleFileUpload(e.target.files)}
          style={{ display: 'none' }}
        />
        
        <button 
          className="upload-button"
          onClick={() => fileInputRef.current?.click()}
          disabled={uploadingFiles}
        >
          <Camera className="w-8 h-8" />
          <span>{uploadingFiles ? 'جاري الرفع...' : 'التقاط صورة/رفع مستند'}</span>
        </button>
      </div>

      {uploadedFiles.length > 0 && (
        <div className="uploaded-files">
          <h3>الملفات المرفوعة</h3>
          <div className="files-list">
            {uploadedFiles.map((file, index) => (
              <div key={index} className="file-item">
                <FileText className="w-6 h-6" />
                <span>{file.original_name}</span>
                <CheckCircle className="w-5 h-5 text-green-500" />
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="pod-actions">
        <button 
          className="action-button secondary"
          onClick={() => setCurrentView('task_active')}
        >
          <ChevronRight className="w-6 h-6" />
          <span>العودة للمهمة</span>
        </button>
        
        {uploadedFiles.length > 0 && (
          <button 
            className="action-button primary"
            onClick={() => setCurrentView('survey')}
          >
            <ChevronLeft className="w-6 h-6" />
            <span>التالي</span>
          </button>
        )}
      </div>
    </div>
  );

  // Survey View
  const SurveyView = () => (
    <div className="survey">
      <div className="survey-header">
        <h2>استبيان ما بعد المهمة</h2>
        <p>ساعدنا في تحسين خدمتنا</p>
      </div>

      <div className="survey-form">
        <div className="rating-section">
          <h3>كيف كانت تجربة هذه المهمة؟</h3>
          <div className="rating-stars">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                className={`star-button ${surveyData.overall_rating >= star ? 'active' : ''}`}
                onClick={() => setSurveyData(prev => ({ ...prev, overall_rating: star }))}
              >
                <Star className="w-8 h-8" />
              </button>
            ))}
          </div>
        </div>

        <div className="feedback-section">
          <h3>ملاحظات إضافية</h3>
          <textarea
            placeholder="اكتب ملاحظاتك هنا..."
            value={surveyData.feedback || ''}
            onChange={(e) => setSurveyData(prev => ({ ...prev, feedback: e.target.value }))}
            className="feedback-textarea"
            rows={4}
          />
        </div>

        <div className="survey-actions">
          <button 
            className="action-button secondary"
            onClick={() => setCurrentView('proof_of_delivery')}
          >
            <ChevronRight className="w-6 h-6" />
            <span>السابق</span>
          </button>
          
          <button 
            className="action-button primary"
            onClick={submitSurvey}
            disabled={!surveyData.overall_rating}
          >
            <CheckCircle className="w-6 h-6" />
            <span>إرسال</span>
          </button>
        </div>
      </div>
    </div>
  );

  const getStepStatus = (step, currentTaskState) => {
    const stepsOrder = [
      'TASK_ACCEPTED',
      'HEADING_TO_PICKUP',
      'ARRIVED_AT_PICKUP',
      'PICKUP_CONFIRMED',
      'HEADING_TO_DROPOFF',
      'ARRIVED_AT_DROPOFF',
      'DELIVERY_CONFIRMED'
    ];
    
    const currentIndex = stepsOrder.indexOf(currentTaskState);
    const stepIndex = stepsOrder.indexOf(step);
    
    if (stepIndex < currentIndex) return 'completed';
    if (stepIndex === currentIndex) return 'active';
    return 'pending';
  };

  const renderCurrentView = () => {
    switch (currentView) {
      case 'dashboard':
        return <DashboardView />;
      case 'task_assigned':
        return <TaskAssignedView />;
      case 'task_active':
        return <ActiveTaskView />;
      case 'proof_of_delivery':
        return <ProofOfDeliveryView />;
      case 'survey':
        return <SurveyView />;
      default:
        return <DashboardView />;
    }
  };

  return (
    <div className="driver-app">
      <div className="app-header">
        <div className="header-content">
          <Truck className="w-8 h-8" />
          <h1>إدهام - السائق</h1>
        </div>
        <div className="header-actions">
          <button className="header-button">
            <MessageCircle className="w-6 h-6" />
          </button>
          <button className="header-button">
            <Phone className="w-6 h-6" />
          </button>
        </div>
      </div>

      <div className="app-content">
        <AnimatePresence mode="wait">
          {renderCurrentView()}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default DriverAppPage;
