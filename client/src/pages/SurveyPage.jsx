/**
 * ============================================
 * 📝 Survey Page - نظام إدهام
 * Edham Logistics - Survey & Feedback Page
 * ============================================
 */

import React, { useState } from 'react';
import api from '../services/api';
import { Star, MessageSquare, Send, CheckCircle } from 'lucide-react';
import Button from '../components/UI/Button';
import Select from '../components/UI/Input';

export default function SurveyPage() {
  const [surveyType, setSurveyType] = useState('driver');
  const [driverId, setDriverId] = useState('');
  const [shipmentId, setShipmentId] = useState('');
  const [ratings, setRatings] = useState({
    vehicleCondition: 0,
    routeEfficiency: 0,
    customerService: 0,
    timeManagement: 0,
    overall: 0
  });
  const [feedback, setFeedback] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleRatingChange = (category, value) => {
    setRatings(prev => ({ ...prev, [category]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await api.post('/surveys', {
        surveyType,
        driver: driverId,
        shipment: shipmentId,
        ratings,
        feedback
      });
      setSubmitted(true);
    } catch (err) {
      console.error('Survey submission error:', err);
    } finally {
      setLoading(false);
    }
  };

  if (submitted) {
    return (
      <div className="max-w-2xl mx-auto p-6">
        <div className="bg-gray-800 p-8 rounded-lg text-center">
          <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
          <h2 className="text-2xl font-bold text-white mb-2">شكراً لتقييمك</h2>
          <p className="text-gray-400 mb-6">تم إرسال تقييمك بنجاح</p>
          <Button onClick={() => setSubmitted(false)}>
            تقييم آخر
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-white">التقييم والملاحظات</h1>
        <p className="text-gray-400 mt-1">ساعدنا في تحسين خدماتنا بتقييمك</p>
      </div>

      <div className="bg-gray-800 p-6 rounded-lg">
        <form onSubmit={handleSubmit}>
          <div className="mb-6">
            <label className="block text-gray-300 mb-2">نوع التقييم</label>
            <Select
              value={surveyType}
              onChange={(e) => setSurveyType(e.target.value)}
              options={[
                { value: 'driver', label: 'تقييم السائق' },
                { value: 'service', label: 'تقييم الخدمة' },
                { value: 'shipment', label: 'تقييم الشحنة' }
              ]}
            />
          </div>

          {surveyType === 'driver' && (
            <>
              <div className="mb-6">
                <label className="block text-gray-300 mb-2">السائق</label>
                <Select
                  value={driverId}
                  onChange={(e) => setDriverId(e.target.value)}
                  options={[
                    { value: '', label: 'اختر السائق' },
                    { value: '1', label: 'أحمد محمد' },
                    { value: '2', label: 'خالد عبدالله' }
                  ]}
                />
              </div>

              <div className="mb-6">
                <label className="block text-gray-300 mb-2">الشحنة</label>
                <Select
                  value={shipmentId}
                  onChange={(e) => setShipmentId(e.target.value)}
                  options={[
                    { value: '', label: 'اختر الشحنة' },
                    { value: '1', label: 'SHP-12345' },
                    { value: '2', label: 'SHP-67890' }
                  ]}
                />
              </div>
            </>
          )}

          {/* Rating Categories */}
          <div className="space-y-6 mb-6">
            <RatingCategory
              label="حالة المركبة"
              value={ratings.vehicleCondition}
              onChange={(value) => handleRatingChange('vehicleCondition', value)}
            />
            <RatingCategory
              label="كفاءة المسار"
              value={ratings.routeEfficiency}
              onChange={(value) => handleRatingChange('routeEfficiency', value)}
            />
            <RatingCategory
              label="خدمة العملاء"
              value={ratings.customerService}
              onChange={(value) => handleRatingChange('customerService', value)}
            />
            <RatingCategory
              label="إدارة الوقت"
              value={ratings.timeManagement}
              onChange={(value) => handleRatingChange('timeManagement', value)}
            />
            <RatingCategory
              label="التقييم العام"
              value={ratings.overall}
              onChange={(value) => handleRatingChange('overall', value)}
            />
          </div>

          {/* Feedback */}
          <div className="mb-6">
            <label className="block text-gray-300 mb-2 flex items-center gap-2">
              <MessageSquare className="w-4 h-4" />
              ملاحظات إضافية
            </label>
            <textarea
              value={feedback}
              onChange={(e) => setFeedback(e.target.value)}
              rows="4"
              className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none resize-none"
              placeholder="اكتب ملاحظاتك هنا..."
            />
          </div>

          <Button
            type="submit"
            loading={loading}
            fullWidth
            icon={<Send className="w-4 h-4" />}
          >
            إرسال التقييم
          </Button>
        </form>
      </div>

      {/* Recent Surveys */}
      <div className="mt-6 bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">التقييمات الأخيرة</h2>
        <div className="space-y-3">
          <SurveyItem
            driver="أحمد محمد"
            rating={4.5}
            date="منذ يومين"
            feedback="خدمة ممتازة"
          />
          <SurveyItem
            driver="خالد عبدالله"
            rating={4.8}
            date="منذ أسبوع"
            feedback="وصول في الوقت المحدد"
          />
          <SurveyItem
            driver="سعود علي"
            rating={4.2}
            date="منذ أسبوعين"
            feedback="تحتاج تحسين"
          />
        </div>
      </div>
    </div>
  );
}

function RatingCategory({ label, value, onChange }) {
  return (
    <div>
      <label className="block text-gray-300 mb-2">{label}</label>
      <div className="flex gap-2">
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type="button"
            onClick={() => onChange(star)}
            className="focus:outline-none"
          >
            <Star
              className={`w-8 h-8 ${
                star <= value ? 'text-yellow-500 fill-current' : 'text-gray-600'
              }`}
            />
          </button>
        ))}
      </div>
    </div>
  );
}

function SurveyItem({ driver, rating, date, feedback }) {
  return (
    <div className="flex items-center justify-between p-4 bg-gray-700 rounded">
      <div>
        <p className="text-white font-semibold">{driver}</p>
        <p className="text-gray-400 text-sm">{feedback}</p>
      </div>
      <div className="text-left">
        <div className="flex items-center gap-1">
          <Star className="w-4 h-4 text-yellow-500 fill-current" />
          <span className="text-white font-semibold">{rating}</span>
        </div>
        <p className="text-gray-500 text-xs">{date}</p>
      </div>
    </div>
  );
}
