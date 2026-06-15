/**
 * ============================================
 * 🚛 Truck Form Component - نظام إدهام
 * Edham Logistics - Truck Form
 * ============================================
 */

import React, { useState } from 'react';
import api from '../../services/api';
import { Truck, Save, X } from 'lucide-react';
import Input from '../UI/Input';
import Select from '../UI/Input';
import Button from '../UI/Button';

export default function TruckForm({ truck, onSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    truckNumber: truck?.truckNumber || '',
    plateNumber: truck?.plateNumber || '',
    type: truck?.type || 'standard',
    capacity: truck?.capacity || '',
    manufacturer: truck?.manufacturer || '',
    year: truck?.year || '',
    status: truck?.status || 'active'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (truck?._id) {
        await api.put(`/trucks/${truck._id}`, formData);
      } else {
        await api.post('/trucks', formData);
      }
      onSuccess?.();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <Truck className="w-5 h-5" />
          {truck?._id ? 'تعديل الشاحنة' : 'شاحنة جديدة'}
        </h2>
        <button onClick={onCancel} className="text-gray-400 hover:text-white">
          <X className="w-5 h-5" />
        </button>
      </div>

      {error && (
        <div className="bg-red-500 text-white p-3 rounded mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="رقم الشاحنة"
            name="truckNumber"
            value={formData.truckNumber}
            onChange={handleChange}
            required
          />
          <Input
            label="رقم اللوحة"
            name="plateNumber"
            value={formData.plateNumber}
            onChange={handleChange}
            required
          />
          <Select
            label="النوع"
            name="type"
            value={formData.type}
            onChange={handleChange}
            options={[
              { value: 'standard', label: 'عادية' },
              { value: 'refrigerated', label: 'مبردة' },
              { value: 'tanker', label: 'صهريج' }
            ]}
            required
          />
          <Input
            label="السعة (طن)"
            name="capacity"
            type="number"
            value={formData.capacity}
            onChange={handleChange}
            required
          />
          <Input
            label="الشركة المصنعة"
            name="manufacturer"
            value={formData.manufacturer}
            onChange={handleChange}
          />
          <Input
            label="سنة الصنع"
            name="year"
            type="number"
            value={formData.year}
            onChange={handleChange}
          />
          <Select
            label="الحالة"
            name="status"
            value={formData.status}
            onChange={handleChange}
            options={[
              { value: 'active', label: 'نشطة' },
              { value: 'inactive', label: 'غير نشطة' },
              { value: 'maintenance', label: 'صيانة' }
            ]}
          />
        </div>

        <div className="flex justify-end gap-2 mt-6">
          <Button
            type="button"
            variant="secondary"
            onClick={onCancel}
          >
            إلغاء
          </Button>
          <Button
            type="submit"
            loading={loading}
            icon={<Save className="w-4 h-4" />}
          >
            حفظ
          </Button>
        </div>
      </form>
    </div>
  );
}
