/**
 * ============================================
 * 🎨 Edham Logistics - Bottom Navigation Component
 * نظام إدهام - شريط التنقل السفلي
 * ============================================
 */

import React from 'react';
import { motion } from 'framer-motion';
import { 
  Home, Truck, MessageSquare, Settings, Plus
} from 'lucide-react';

const BottomNavigation = ({ activeTab = 'home', onTabChange }) => {
  const navItems = [
    {
      id: 'home',
      label: 'الرئيسية',
      icon: Home,
      position: 'left'
    },
    {
      id: 'orders',
      label: 'الطلبات',
      icon: Truck,
      position: 'left'
    },
    {
      id: 'new-order',
      label: 'طلب جديد',
      icon: Plus,
      position: 'center',
      isSpecial: true
    },
    {
      id: 'chat',
      label: 'المحادثات',
      icon: MessageSquare,
      position: 'right'
    },
    {
      id: 'settings',
      label: 'الإعدادات',
      icon: Settings,
      position: 'right'
    }
  ];

  return (
    <div className="bottom-navigation">
      <div className="nav-container">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          
          if (item.isSpecial) {
            return (
              <motion.button
                key={item.id}
                onClick={() => onTabChange?.(item.id)}
                className="nav-item special-item"
                whileHover={{ scale: 1.1 }}
                whileTap={{ scale: 0.95 }}
              >
                <div className="special-button">
                  <Icon className="w-6 h-6" />
                </div>
                <span className="nav-label">{item.label}</span>
              </motion.button>
            );
          }

          return (
            <motion.button
              key={item.id}
              onClick={() => onTabChange?.(item.id)}
              className={`nav-item ${isActive ? 'active' : ''}`}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <Icon className={`w-5 h-5 ${isActive ? 'active-icon' : ''}`} />
              <span className="nav-label">{item.label}</span>
            </motion.button>
          );
        })}
      </div>
    </div>
  );
};

export default BottomNavigation;
