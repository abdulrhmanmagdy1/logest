import React, { useState } from 'react';
import { useNotifications } from '../context/NotificationContext';
import { Bell, X, Check } from 'lucide-react';

const NotificationBell = () => {
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 rounded-full hover:bg-edham-white/10 transition-colors"
      >
        <Bell className="w-6 h-6 text-edham-white" />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-edham-gold text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-edham-dark border border-edham-gray rounded-2xl shadow-xl z-50">
          <div className="p-4 border-b border-edham-gray flex justify-between items-center">
            <h3 className="text-lg font-bold text-edham-white">الإشعارات</h3>
            {unreadCount > 0 && (
              <button
                onClick={markAllAsRead}
                className="text-edham-primary text-sm hover:text-edham-primaryLight flex items-center gap-1"
              >
                <Check className="w-4 h-4" />
                تحديد الكل كمقروء
              </button>
            )}
          </div>
          
          <div className="max-h-96 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="p-8 text-center text-edham-white/50">
                لا توجد إشعارات
              </div>
            ) : (
              notifications.map((notification) => (
                <div
                  key={notification._id}
                  className={`p-4 border-b border-edham-gray hover:bg-edham-white/5 transition-colors ${
                    !notification.read ? 'bg-edham-white/10' : ''
                  }`}
                >
                  <div className="flex justify-between items-start gap-3">
                    <div className="flex-1">
                      <h4 className="text-edham-white font-semibold mb-1">{notification.title}</h4>
                      <p className="text-edham-white/70 text-sm">{notification.message}</p>
                      <p className="text-edham-white/50 text-xs mt-2">
                        {new Date(notification.createdAt).toLocaleString('ar-EG')}
                      </p>
                    </div>
                    {!notification.read && (
                      <button
                        onClick={() => markAsRead(notification._id)}
                        className="text-edham-green hover:text-edham-greenLight"
                      >
                        <Check className="w-4 h-4" />
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
