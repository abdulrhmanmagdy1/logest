//
/**
 * ============================================
 * 📅 Calendar & Scheduling - التقويم والجدولة
 * ============================================
 */

const mongoose = require('mongoose');

// Calendar Event Schema
const CalendarEventSchema = new mongoose.Schema({
  eventId: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['meeting', 'delivery', 'pickup', 'maintenance', 'appointment', 'reminder', 'task', 'shipment', 'training', 'vacation', 'holiday'],
    default: 'meeting'
  },
  start: {
    type: Date,
    required: true
  },
  end: {
    type: Date,
    required: true
  },
  allDay: {
    type: Boolean,
    default: false
  },
  location: {
    address: String,
    coordinates: {
      lat: Number,
      lng: Number
    },
    link: String // For virtual meetings
  },
  organizer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  attendees: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    status: { type: String, enum: ['pending', 'accepted', 'declined', 'tentative'], default: 'pending' },
    responseTime: Date,
    notes: String,
    required: { type: Boolean, default: true }
  }],
  recurrence: {
    enabled: { type: Boolean, default: false },
    pattern: {
      type: { type: String, enum: ['daily', 'weekly', 'monthly', 'yearly'] },
      interval: { type: Number, default: 1 },
      daysOfWeek: [Number], // 0-6 for weekly
      dayOfMonth: Number,
      monthOfYear: Number
    },
    end: {
      type: { type: String, enum: ['never', 'after', 'on_date'] },
      occurrences: Number,
      endDate: Date
    },
    exceptions: [Date] // Dates to skip
  },
  relatedEntity: {
    type: { type: String, enum: ['shipment', 'customer', 'driver', 'vehicle', 'order'] },
    id: mongoose.Schema.Types.ObjectId,
    reference: String
  },
  reminders: [{
    type: { type: String, enum: ['email', 'push', 'sms', 'popup'] },
    before: Number, // minutes before event
    sent: { type: Boolean, default: false },
    sentAt: Date
  }],
  visibility: {
    type: String,
    enum: ['public', 'private', 'team'],
    default: 'public'
  },
  status: {
    type: String,
    enum: ['confirmed', 'tentative', 'cancelled', 'completed'],
    default: 'confirmed'
  },
  color: String,
  attachments: [{
    name: String,
    url: String,
    type: String
  }],
  notes: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

CalendarEventSchema.index({ company: 1, start: 1, end: 1 });
CalendarEventSchema.index({ company: 1, 'attendees.user': 1, start: 1 });

// Availability Schedule Schema
const AvailabilitySchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  date: {
    type: Date,
    required: true
  },
  slots: [{
    start: String, // HH:MM format
    end: String,
    available: { type: Boolean, default: true },
    type: { type: String, enum: ['work', 'break', 'meeting', 'unavailable'] },
    event: { type: mongoose.Schema.Types.ObjectId, ref: 'CalendarEvent' }
  }],
  timezone: String,
  workingHours: {
    start: String,
    end: String,
    breaks: [{
      start: String,
      end: String
    }]
  },
  isWorkingDay: { type: Boolean, default: true },
  notes: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

AvailabilitySchema.index({ company: 1, user: 1, date: 1 });

// Schedule Template Schema
const ScheduleTemplateSchema = new mongoose.Schema({
  templateId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  type: {
    type: String,
    enum: ['driver', 'warehouse', 'office', 'maintenance', 'custom']
  },
  timezone: String,
  shifts: [{
    name: String,
    days: [Number], // 0-6
    start: String, // HH:MM
    end: String,
    breaks: [{
      start: String,
      end: String,
      paid: Boolean
    }],
    color: String
  }],
  assignments: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    shift: String,
    startDate: Date,
    endDate: Date
  }],
  rotation: {
    enabled: { type: Boolean, default: false },
    type: { type: String, enum: ['weekly', 'biweekly', 'monthly'] },
    pattern: [String] // shift names in order
  },
  isActive: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  CalendarEvent: mongoose.model('CalendarEvent', CalendarEventSchema),
  Availability: mongoose.model('Availability', AvailabilitySchema),
  ScheduleTemplate: mongoose.model('ScheduleTemplate', ScheduleTemplateSchema)
};
