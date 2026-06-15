/**
 * ============================================
 * 📊 Premium Dashboard Components & Layout
 * Enterprise-grade logistics dashboard
 * ============================================
 */

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import {
  BarChart3, TrendingUp, Users, Truck, AlertCircle, Clock, CheckCircle,
  MapPin, DollarSign, Zap, Bell, Menu, X
} from 'lucide-react';

// ========================
// DASHBOARD HEADER
// ========================

export const DashboardHeader = ({ title, subtitle, user, onMenuClick }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      className="border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900/50 backdrop-blur-sm sticky top-0 z-40"
    >
      <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={onMenuClick}
            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors"
          >
            <Menu className="w-6 h-6 text-gray-700 dark:text-gray-300" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{title}</h1>
            {subtitle && <p className="text-sm text-gray-500 dark:text-gray-400">{subtitle}</p>}
          </div>
        </div>

        <div className="flex items-center gap-4">
          {/* Notifications */}
          <motion.button
            whileHover={{ scale: 1.05 }}
            className="relative p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors"
          >
            <Bell className="w-6 h-6 text-gray-700 dark:text-gray-300" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
          </motion.button>

          {/* User profile */}
          <motion.div
            whileHover={{ scale: 1.05 }}
            className="flex items-center gap-3 pl-4 border-l border-gray-200 dark:border-gray-800"
          >
            <div className="w-10 h-10 bg-gradient-to-br from-blue-400 to-cyan-500 rounded-full flex items-center justify-center text-white font-semibold">
              {user?.firstName?.charAt(0)}{user?.lastName?.charAt(0)}
            </div>
            <div className="text-sm">
              <p className="font-semibold text-gray-900 dark:text-white">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-xs text-gray-500 dark:text-gray-400 capitalize">
                {user?.role}
              </p>
            </div>
          </motion.div>
        </div>
      </div>
    </motion.div>
  );
};

// ========================
// STAT CARD - PREMIUM
// ========================

export const PremiumStatCard = ({ icon: Icon, label, value, trend, unit, color = 'blue' }) => {
  const colorMap = {
    blue: 'from-blue-600 to-blue-400',
    green: 'from-green-600 to-green-400',
    red: 'from-red-600 to-red-400',
    purple: 'from-purple-600 to-purple-400',
    amber: 'from-amber-600 to-amber-400',
  };

  const isTrendUp = trend >= 0;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      whileHover={{ y: -4, boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)' }}
      className="bg-white dark:bg-gray-900 rounded-2xl p-6 shadow-sm hover:shadow-lg transition-all border border-gray-100 dark:border-gray-800 relative overflow-hidden group"
    >
      {/* Background gradient */}
      <div className={`absolute inset-0 opacity-0 group-hover:opacity-5 bg-gradient-to-br ${colorMap[color]} transition-opacity`} />

      <div className="relative z-10">
        {/* Icon and trend */}
        <div className="flex items-start justify-between mb-4">
          <div className={`p-3 bg-gradient-to-br ${colorMap[color]} rounded-xl shadow-lg`}>
            <Icon className="w-6 h-6 text-white" />
          </div>

          {trend !== undefined && (
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              className={`flex items-center gap-1 px-2 py-1 rounded-lg text-xs font-semibold ${
                isTrendUp
                  ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400'
                  : 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400'
              }`}
            >
              <TrendingUp className={`w-3 h-3 ${isTrendUp ? '' : 'rotate-180'}`} />
              {Math.abs(trend)}%
            </motion.div>
          )}
        </div>

        {/* Label */}
        <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">{label}</p>

        {/* Value */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="text-3xl font-bold text-gray-900 dark:text-white"
        >
          {value}
          {unit && <span className="text-base text-gray-500 dark:text-gray-400 ml-1">{unit}</span>}
        </motion.p>
      </div>
    </motion.div>
  );
};

// ========================
// ACTIVITY FEED
// ========================

export const ActivityFeed = ({ activities }) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="bg-white dark:bg-gray-900 rounded-2xl p-6 shadow-sm border border-gray-100 dark:border-gray-800"
    >
      <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-4">Recent Activity</h3>

      <div className="space-y-4">
        {activities.map((activity, idx) => (
          <motion.div
            key={idx}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: idx * 0.1 }}
            className="flex gap-4 pb-4 border-b border-gray-100 dark:border-gray-800 last:border-0 last:pb-0"
          >
            {/* Icon */}
            <div className="flex-shrink-0">
              <div className="w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center">
                {activity.icon ? (
                  <activity.icon className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                ) : (
                  <div className="w-2 h-2 bg-blue-600 rounded-full" />
                )}
              </div>
            </div>

            {/* Content */}
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-gray-900 dark:text-white">
                {activity.title}
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                {activity.description}
              </p>
              <p className="text-xs text-gray-500 dark:text-gray-500 mt-2">
                {activity.time}
              </p>
            </div>

            {/* Status badge */}
            {activity.status && (
              <div className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap ${
                activity.status === 'success'
                  ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400'
                  : activity.status === 'pending'
                  ? 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400'
                  : 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400'
              }`}>
                {activity.status}
              </div>
            )}
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
};

// ========================
// REAL-TIME ALERTS
// ========================

export const RealtimeAlerts = ({ alerts }) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="bg-gradient-to-br from-red-50 to-orange-50 dark:from-red-900/20 dark:to-orange-900/20 rounded-2xl p-6 border border-red-200 dark:border-red-800"
    >
      <div className="flex items-center gap-3 mb-4">
        <AlertCircle className="w-6 h-6 text-red-600 dark:text-red-400" />
        <h3 className="text-lg font-bold text-red-900 dark:text-red-300">
          Active Alerts
        </h3>
        <span className="ml-auto px-3 py-1 bg-red-600 text-white rounded-full text-sm font-semibold">
          {alerts.length}
        </span>
      </div>

      <div className="space-y-3">
        {alerts.map((alert, idx) => (
          <motion.div
            key={idx}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: idx * 0.1 }}
            className="flex items-start gap-3 p-3 bg-white/50 dark:bg-gray-800/50 rounded-lg"
          >
            <div className="w-2 h-2 rounded-full bg-red-600 dark:bg-red-400 mt-2 flex-shrink-0" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-semibold text-gray-900 dark:text-white">
                {alert.title}
              </p>
              <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">
                {alert.message}
              </p>
            </div>
            <span className="text-xs text-gray-500 whitespace-nowrap">
              {alert.time}
            </span>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
};

// ========================
// KPI GRID
// ========================

export const KPIGrid = ({ kpis }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {kpis.map((kpi, idx) => (
        <PremiumStatCard
          key={idx}
          icon={kpi.icon}
          label={kpi.label}
          value={kpi.value}
          trend={kpi.trend}
          unit={kpi.unit}
          color={kpi.color}
        />
      ))}
    </div>
  );
};

// ========================
// PREMIUM TABLE
// ========================

export const PremiumTable = ({ columns, data, loading = false }) => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="bg-white dark:bg-gray-900 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-800 overflow-hidden"
    >
      <div className="overflow-x-auto">
        <table className="w-full">
          {/* Header */}
          <thead className="bg-gray-50 dark:bg-gray-800/50 border-b border-gray-100 dark:border-gray-800">
            <tr>
              {columns.map((col, idx) => (
                <th
                  key={idx}
                  className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase tracking-wider"
                >
                  {col}
                </th>
              ))}
            </tr>
          </thead>

          {/* Body */}
          <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-8 text-center">
                  <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                    className="w-6 h-6 border-2 border-blue-500 border-t-transparent rounded-full mx-auto"
                  />
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-8 text-center text-gray-500 dark:text-gray-400">
                  No data available
                </td>
              </tr>
            ) : (
              data.map((row, idx) => (
                <motion.tr
                  key={idx}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: idx * 0.1 }}
                  className="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
                >
                  {Object.values(row).map((cell, cellIdx) => (
                    <td
                      key={cellIdx}
                      className="px-6 py-4 text-sm text-gray-700 dark:text-gray-300"
                    >
                      {cell}
                    </td>
                  ))}
                </motion.tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </motion.div>
  );
};

export default {
  DashboardHeader,
  PremiumStatCard,
  ActivityFeed,
  RealtimeAlerts,
  KPIGrid,
  PremiumTable
};
