/**
 * ============================================
 * 📅 Calendar Page - نظام إدهام
 * Edham Logistics - Calendar & Scheduling
 * ============================================
 */

import React, { useState } from 'react';
import { Calendar, ChevronLeft, ChevronRight, Plus, Clock, MapPin, Truck, User, Filter } from 'lucide-react';
import Button from '../components/UI/Button';

export default function CalendarPage() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [view, setView] = useState('month'); // month, week, day
  const [filterType, setFilterType] = useState('all');

  const events = [
    {
      id: 1,
      title: 'صيانة الشاحنة TRK001',
      date: new Date(),
      time: '09:00',
      type: 'maintenance',
      location: 'مركز الصيانة',
      assignedTo: 'أحمد محمد',
      status: 'pending'
    },
    {
      id: 2,
      title: 'تسليم الشحنة #12345',
      date: new Date(Date.now() + 86400000),
      time: '14:00',
      type: 'delivery',
      location: 'الرياض',
      assignedTo: 'خالد عبدالله',
      status: 'confirmed'
    },
    {
      id: 3,
      title: 'استلام الشحنة #67890',
      date: new Date(Date.now() + 172800000),
      time: '10:00',
      type: 'pickup',
      location: 'جدة',
      assignedTo: 'سعود علي',
      status: 'pending'
    },
    {
      id: 4,
      title: 'تغيير زيت الشاحنة TRK002',
      date: new Date(Date.now() + 259200000),
      time: '08:00',
      type: 'oil_change',
      location: 'مركز الصيانة',
      assignedTo: 'محمد أحمد',
      status: 'scheduled'
    }
  ];

  const filteredEvents = filterType === 'all' 
    ? events 
    : events.filter(event => event.type === filterType);

  const getDaysInMonth = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startDay = firstDay.getDay();
    
    return { daysInMonth, startDay };
  };

  const { daysInMonth, startDay } = getDaysInMonth(currentDate);

  const prevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1));
  };

  const nextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1));
  };

  const getEventsForDay = (day) => {
    return filteredEvents.filter(event => {
      const eventDate = new Date(event.date);
      return eventDate.getDate() === day && 
             eventDate.getMonth() === currentDate.getMonth() &&
             eventDate.getFullYear() === currentDate.getFullYear();
    });
  };

  const monthNames = [
    'يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو',
    'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'
  ];

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">التقويم</h1>
          <p className="text-gray-400 mt-1">إدارة المواعيد والجدول</p>
        </div>
        <Button
          icon={<Plus className="w-4 h-4" />}
        >
          إضافة موعد
        </Button>
      </div>

      {/* Filters */}
      <div className="flex gap-4 mb-6">
        <select
          value={filterType}
          onChange={(e) => setFilterType(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الأنواع</option>
          <option value="maintenance">صيانة</option>
          <option value="delivery">تسليم</option>
          <option value="pickup">استلام</option>
          <option value="oil_change">تغيير زيت</option>
        </select>

        <select
          value={view}
          onChange={(e) => setView(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="month">شهر</option>
          <option value="week">أسبوع</option>
          <option value="day">يوم</option>
        </select>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Calendar */}
        <div className="lg:col-span-2 bg-gray-800 rounded-lg p-6">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <button
              onClick={prevMonth}
              className="p-2 hover:bg-gray-700 rounded text-white"
            >
              <ChevronRight className="w-5 h-5" />
            </button>
            <h2 className="text-xl font-bold text-white">
              {monthNames[currentDate.getMonth()]} {currentDate.getFullYear()}
            </h2>
            <button
              onClick={nextMonth}
              className="p-2 hover:bg-gray-700 rounded text-white"
            >
              <ChevronLeft className="w-5 h-5" />
            </button>
          </div>

          {/* Days Header */}
          <div className="grid grid-cols-7 gap-2 mb-2">
            {['أحد', 'إثنين', 'ثلاثاء', 'أربعاء', 'خميس', 'جمعة', 'سبت'].map((day) => (
              <div key={day} className="text-center text-gray-400 font-semibold py-2">
                {day}
              </div>
            ))}
          </div>

          {/* Calendar Grid */}
          <div className="grid grid-cols-7 gap-2">
            {/* Empty cells for days before the first day of month */}
            {Array.from({ length: startDay }).map((_, index) => (
              <div key={`empty-${index}`} className="h-24" />
            ))}

            {/* Days of month */}
            {Array.from({ length: daysInMonth }).map((_, index) => {
              const day = index + 1;
              const dayEvents = getEventsForDay(day);
              const isToday = day === new Date().getDate() && 
                            currentDate.getMonth() === new Date().getMonth() &&
                            currentDate.getFullYear() === new Date().getFullYear();

              return (
                <div
                  key={day}
                  onClick={() => setSelectedDate(day)}
                  className={`h-24 p-2 rounded cursor-pointer hover:bg-gray-700 ${
                    isToday ? 'bg-blue-600' : 'bg-gray-700'
                  }`}
                >
                  <p className={`text-white font-semibold ${isToday ? '' : 'text-gray-300'}`}>
                    {day}
                  </p>
                  <div className="space-y-1 mt-1">
                    {dayEvents.slice(0, 2).map((event) => (
                      <div
                        key={event.id}
                        className="text-xs px-1 py-0.5 rounded truncate"
                        style={{ backgroundColor: getEventColor(event.type) }}
                      >
                        {event.title}
                      </div>
                    ))}
                    {dayEvents.length > 2 && (
                      <p className="text-xs text-gray-400">
                        +{dayEvents.length - 2} أخرى
                      </p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Events List */}
        <div className="bg-gray-800 rounded-lg p-6">
          <h2 className="text-xl font-bold text-white mb-4">الأحداث</h2>

          <div className="space-y-3">
            {filteredEvents.map((event) => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>

          {/* Legend */}
          <div className="mt-6 pt-6 border-t border-gray-700">
            <h3 className="text-white font-semibold mb-3">المفتاح</h3>
            <div className="space-y-2">
              <LegendItem color="#3B82F6" label="صيانة" type="maintenance" />
              <LegendItem color="#10B981" label="تسليم" type="delivery" />
              <LegendItem color="#F59E0B" label="استلام" type="pickup" />
              <LegendItem color="#8B5CF6" label="تغيير زيت" type="oil_change" />
            </div>
          </div>
        </div>
      </div>

      {/* Selected Date Events */}
      {selectedDate && (
        <div className="mt-6 bg-gray-800 rounded-lg p-6">
          <h2 className="text-xl font-bold text-white mb-4">
            أحداث {selectedDate} {monthNames[currentDate.getMonth()]}
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {getEventsForDay(selectedDate).map((event) => (
              <EventCard key={event.id} event={event} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function EventCard({ event }) {
  return (
    <div className="bg-gray-700 p-4 rounded">
      <div className="flex items-start justify-between mb-2">
        <h3 className="text-white font-semibold">{event.title}</h3>
        <EventStatusBadge status={event.status} />
      </div>
      <div className="space-y-1 text-sm">
        <div className="flex items-center gap-2 text-gray-300">
          <Clock className="w-4 h-4" />
          <span>{event.time}</span>
        </div>
        <div className="flex items-center gap-2 text-gray-300">
          <MapPin className="w-4 h-4" />
          <span>{event.location}</span>
        </div>
        <div className="flex items-center gap-2 text-gray-300">
          <User className="w-4 h-4" />
          <span>{event.assignedTo}</span>
        </div>
      </div>
    </div>
  );
}

function EventStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    confirmed: 'bg-green-500',
    scheduled: 'bg-blue-500',
    completed: 'bg-gray-500',
    cancelled: 'bg-red-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    confirmed: 'مؤكد',
    scheduled: 'مجدول',
    completed: 'مكتمل',
    cancelled: 'ملغي'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-2 py-1 rounded text-xs text-white`}>
      {statusLabels[status] || status}
    </span>
  );
}

function LegendItem({ color, label, type }) {
  return (
    <div className="flex items-center gap-2">
      <div className="w-3 h-3 rounded" style={{ backgroundColor: color }} />
      <span className="text-gray-300 text-sm">{label}</span>
    </div>
  );
}

function getEventColor(type) {
  const colors = {
    maintenance: '#3B82F6',
    delivery: '#10B981',
    pickup: '#F59E0B',
    oil_change: '#8B5CF6'
  };
  return colors[type] || '#6B7280';
}
