/**
 * ============================================
 * 🔔 Notifications Page - نظام إدهام
 * Edham Logistics - Notifications Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { Bell, Check, X, Trash2, Loader, AlertCircle } from 'lucide-react';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all'); // all, unread, read

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      // Mock data - replace with actual API call
      const mockNotifications = [
        {
          id: 1,
          title: 'شحنة جديدة مُسندة',
          message: 'تم إسناد الشحنة #12345 إليك',
          type: 'shipment',
          read: false,
          createdAt: new Date(Date.now() - 1000 * 60 * 5)
        },
        {
          id: 2,
          title: 'تذكير بصيانة',
          message: 'الشاحنة #TRK001 تحتاج صيانة روتينية',
          type: 'maintenance',
          read: false,
          createdAt: new Date(Date.now() - 1000 * 60 * 60)
        },
        {
          id: 3,
          title: 'فاتورة جديدة',
          message: 'فاتورة #INV67890 جاهزة للدفع',
          type: 'invoice',
          read: true,
          createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24)
        },
        {
          id: 4,
          title: 'رحلة مكتملة',
          message: 'تم إكمال الرحلة #TRIP456 بنجاح',
          type: 'trip',
          read: true,
          createdAt: new Date(Date.now() - 1000 * 60 * 60 * 48)
        }
      ];
      setNotifications(mockNotifications);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = (id) => {
    setNotifications(prev =>
      prev.map(notif =>
        notif.id === id ? { ...notif, read: true } : notif
      )
    );
  };

  const markAllAsRead = () => {
    setNotifications(prev =>
      prev.map(notif => ({ ...notif, read: true }))
    );
  };

  const deleteNotification = (id) => {
    setNotifications(prev => prev.filter(notif => notif.id !== id));
  };

  const clearAll = () => {
    setNotifications([]);
  };

  const filteredNotifications = notifications.filter(notif => {
    if (filter === 'unread') return !notif.read;
    if (filter === 'read') return notif.read;
    return true;
  });

  const unreadCount = notifications.filter(n => !n.read).length;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="animate-spin w-8 h-8 text-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-500 text-white p-4 rounded">
        <AlertCircle className="inline mr-2" />
        {error}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">الإشعارات</h1>
          <p className="text-gray-400 mt-1">
            {unreadCount > 0 ? `${unreadCount} إشعار غير مقروء` : 'لا توجد إشعارات جديدة'}
          </p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={markAllAsRead}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
          >
            <Check className="w-4 h-4" />
            تعريف الكل
          </button>
          <button
            onClick={clearAll}
            className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
          >
            <Trash2 className="w-4 h-4" />
            مسح الكل
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div className="flex gap-2 mb-6">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded ${
            filter === 'all' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'
          }`}
        >
          الكل ({notifications.length})
        </button>
        <button
          onClick={() => setFilter('unread')}
          className={`px-4 py-2 rounded ${
            filter === 'unread' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'
          }`}
        >
          غير مقروء ({unreadCount})
        </button>
        <button
          onClick={() => setFilter('read')}
          className={`px-4 py-2 rounded ${
            filter === 'read' ? 'bg-blue-600 text-white' : 'bg-gray-700 text-gray-300'
          }`}
        >
          مقروء ({notifications.length - unreadCount})
        </button>
      </div>

      {/* Notifications List */}
      <div className="space-y-4">
        {filteredNotifications.length === 0 ? (
          <div className="bg-gray-800 p-8 rounded-lg text-center">
            <Bell className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <p className="text-gray-400">لا توجد إشعارات</p>
          </div>
        ) : (
          filteredNotifications.map((notification) => (
            <div
              key={notification.id}
              className={`bg-gray-800 p-4 rounded-lg border-l-4 ${
                notification.read ? 'border-gray-600' : 'border-blue-500'
              }`}
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-2">
                    {!notification.read && (
                      <span className="w-2 h-2 bg-blue-500 rounded-full" />
                    )}
                    <h3 className="text-white font-semibold">{notification.title}</h3>
                    <NotificationTypeBadge type={notification.type} />
                  </div>
                  <p className="text-gray-400 mb-2">{notification.message}</p>
                  <p className="text-gray-500 text-sm">
                    {new Date(notification.createdAt).toLocaleString('ar-SA')}
                  </p>
                </div>
                <div className="flex gap-2">
                  {!notification.read && (
                    <button
                      onClick={() => markAsRead(notification.id)}
                      className="text-blue-500 hover:text-blue-400"
                      title="تعريف كمقروء"
                    >
                      <Check className="w-5 h-5" />
                    </button>
                  )}
                  <button
                    onClick={() => deleteNotification(notification.id)}
                    className="text-red-500 hover:text-red-400"
                    title="حذف"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

function NotificationTypeBadge({ type }) {
  const typeColors = {
    shipment: 'bg-blue-500',
    maintenance: 'bg-yellow-500',
    invoice: 'bg-green-500',
    trip: 'bg-purple-500',
    system: 'bg-gray-500'
  };

  const typeLabels = {
    shipment: 'شحنة',
    maintenance: 'صيانة',
    invoice: 'فاتورة',
    trip: 'رحلة',
    system: 'نظام'
  };

  return (
    <span className={`${typeColors[type] || 'bg-gray-500'} px-2 py-1 rounded text-xs text-white`}>
      {typeLabels[type] || type}
    </span>
  );
}
