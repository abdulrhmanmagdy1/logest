/**
 * ============================================
 * 🚛 Supervisor Dashboard - Premium
 * Real-time operations monitoring center
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
  BarChart3, TrendingUp, Users, Truck, AlertCircle, Clock, CheckCircle,
  MapPin, DollarSign, Zap, Bell, Activity, Gauge
} from 'lucide-react';
import {
  DashboardHeader,
  PremiumStatCard,
  ActivityFeed,
  RealtimeAlerts,
  KPIGrid,
  PremiumTable
} from './PremiumDashboardComponents';

const SupervisorDashboard = () => {
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState({
    totalShipments: 1234,
    activeDrivers: 42,
    revenue: '$125,400',
    efficiency: '94%',
    pendingOrders: 18,
    inTransit: 67,
    delivered: 1145,
    alerts: 5
  });

  const user = {
    firstName: 'Ahmed',
    lastName: 'Mohamed',
    role: 'supervisor'
  };

  const kpis = [
    {
      icon: Truck,
      label: 'Active Shipments',
      value: '67',
      trend: 12,
      unit: 'in transit',
      color: 'blue'
    },
    {
      icon: Users,
      label: 'Available Drivers',
      value: '42',
      trend: 8,
      unit: 'ready',
      color: 'green'
    },
    {
      icon: DollarSign,
      label: 'Today Revenue',
      value: '$125.4K',
      trend: 15,
      unit: 'earned',
      color: 'purple'
    },
    {
      icon: Zap,
      label: 'System Efficiency',
      value: '94%',
      trend: 3,
      unit: 'optimal',
      color: 'amber'
    }
  ];

  const recentActivities = [
    {
      icon: CheckCircle,
      title: 'Order #1234 Delivered',
      description: 'Jeddah → Riyadh (Saudi Arabia)',
      time: '5 minutes ago',
      status: 'success'
    },
    {
      icon: Truck,
      title: 'New Order from Premium Client',
      description: 'Customer: ABC Logistics - Qty: 5 pallets',
      time: '12 minutes ago',
      status: 'pending'
    },
    {
      icon: Users,
      title: 'New Driver Registration',
      description: 'Driver: Mohammed Al-Otaibi',
      time: '1 hour ago',
      status: 'success'
    },
    {
      icon: AlertCircle,
      title: 'Vehicle Maintenance Due',
      description: 'Truck: TRK-001 (Oil change overdue)',
      time: '2 hours ago',
      status: 'alert'
    }
  ];

  const alerts = [
    {
      title: 'Weather Alert',
      message: 'Heavy traffic on Riyadh-Jeddah route. ETA +45 mins',
      time: 'Now'
    },
    {
      title: 'Driver Offline',
      message: 'Driver #42 offline for 15 minutes',
      time: '5 mins'
    },
    {
      title: 'Low Fuel Alert',
      message: 'Vehicle TRK-005 fuel below 15%',
      time: '12 mins'
    }
  ];

  const shipmentData = [
    {
      id: '#SHP-001',
      customer: 'ABC Logistics',
      destination: 'Riyadh',
      driver: 'Mohammed Ali',
      status: 'In Transit',
      eta: '2:30 PM',
      progress: '65%'
    },
    {
      id: '#SHP-002',
      customer: 'XYZ Import',
      destination: 'Jeddah',
      driver: 'Ahmed Hassan',
      status: 'In Transit',
      eta: '4:15 PM',
      progress: '45%'
    },
    {
      id: '#SHP-003',
      customer: 'Global Trade',
      destination: 'Dammam',
      driver: 'Khalid Ibrahim',
      status: 'Pending',
      eta: '6:00 PM',
      progress: '0%'
    }
  ];

  const containerVariants = {
    initial: { opacity: 0 },
    animate: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2
      }
    }
  };

  const itemVariants = {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 }
  };

  return (
    <motion.div
      variants={containerVariants}
      initial="initial"
      animate="animate"
      className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-950 dark:to-gray-900"
    >
      {/* Header */}
      <DashboardHeader
        title="Operations Center"
        subtitle="Real-time logistics monitoring"
        user={user}
      />

      {/* Main content */}
      <div className="max-w-7xl mx-auto px-6 py-8 space-y-8">
        {/* KPI Grid */}
        <motion.div variants={itemVariants}>
          <KPIGrid kpis={kpis} />
        </motion.div>

        {/* Main grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left column - Activity feed */}
          <motion.div variants={itemVariants} className="lg:col-span-2">
            <ActivityFeed activities={recentActivities} />
          </motion.div>

          {/* Right column - Alerts */}
          <motion.div variants={itemVariants}>
            <RealtimeAlerts alerts={alerts} />
          </motion.div>
        </div>

        {/* Live shipments table */}
        <motion.div variants={itemVariants}>
          <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 shadow-sm border border-gray-100 dark:border-gray-800">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-6">
              Live Shipments
            </h3>

            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 dark:bg-gray-800/50">
                  <tr>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Shipment ID
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Customer
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Destination
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Driver
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Status
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      Progress
                    </th>
                    <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 dark:text-gray-300 uppercase">
                      ETA
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-100 dark:divide-gray-800">
                  {shipmentData.map((shipment, idx) => (
                    <motion.tr
                      key={idx}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: idx * 0.1 }}
                      className="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
                    >
                      <td className="px-6 py-4 text-sm font-semibold text-blue-600 dark:text-blue-400">
                        {shipment.id}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-700 dark:text-gray-300">
                        {shipment.customer}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-700 dark:text-gray-300 flex items-center gap-2">
                        <MapPin className="w-4 h-4 text-gray-500" />
                        {shipment.destination}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-700 dark:text-gray-300">
                        {shipment.driver}
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          shipment.status === 'In Transit'
                            ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400'
                            : 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400'
                        }`}>
                          {shipment.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <div className="w-24 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                          <motion.div
                            className="h-full bg-gradient-to-r from-blue-500 to-cyan-500"
                            style={{ width: shipment.progress }}
                            animate={{ width: shipment.progress }}
                          />
                        </div>
                        <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                          {shipment.progress}
                        </p>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-700 dark:text-gray-300">
                        {shipment.eta}
                      </td>
                    </motion.tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </motion.div>

        {/* Maps placeholder */}
        <motion.div variants={itemVariants}>
          <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 shadow-sm border border-gray-100 dark:border-gray-800 h-96 flex items-center justify-center">
            <div className="text-center">
              <MapPin className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                Live Fleet Map
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                Real-time GPS tracking of all active shipments
              </p>
            </div>
          </div>
        </motion.div>
      </div>
    </motion.div>
  );
};

export default SupervisorDashboard;
