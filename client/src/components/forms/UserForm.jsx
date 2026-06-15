/**
 * ============================================
 * 👤 User Form Component - نظام إدهام
 * Edham Logistics - User Form
 * ============================================
 */

import React, { useState } from 'react';
import api from '../../services/api';
import { User, Save, X } from 'lucide-react';
import Input from '../UI/Input';
import Select from '../UI/Input';
import Button from '../UI/Button';

export default function UserForm({ user, onSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    name: user?.name || '',
    email: user?.email || '',
    phone: user?.phone || '',
    role: user?.role || 'employee',
    city: user?.city || '',
    address: user?.address || '',
    status: user?.status || 'active'
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
      if (user?._id) {
        await api.put(`/users/${user._id}`, formData);
      } else {
        await api.post('/users', formData);
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
          <User className="w-5 h-5" />
          {user?._id ? 'تعديل المستخدم' : 'مستخدم جديد'}
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
            label="الاسم"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
          <Input
            label="البريد الإلكتروني"
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            required
            disabled={!!user?._id}
          />
          <Input
            label="رقم الهاتف"
            name="phone"
            type="tel"
            value={formData.phone}
            onChange={handleChange}
          />
          <Select
            label="الدور"
            name="role"
            value={formData.role}
            onChange={handleChange}
            options={[
              { value: 'admin', label: 'مشرف' },
              { value: 'supervisor', label: 'مشرف ميداني' },
              { value: 'accountant', label: 'محاسب' },
              { value: 'driver', label: 'سائق' },
              { value: 'client', label: 'عميل' },
              { value: 'employee', label: 'موظف' },
              { value: 'maintenance', label: 'صيانة' }
            ]}
            required
          />
          <Input
            label="المدينة"
            name="city"
            value={formData.city}
            onChange={handleChange}
          />
          <Select
            label="الحالة"
            name="status"
            value={formData.status}
            onChange={handleChange}
            options={[
              { value: 'active', label: 'نشط' },
              { value: 'inactive', label: 'غير نشط' },
              { value: 'suspended', label: 'معلق' }
            ]}
          />
        </div>

        <div className="md:col-span-2">
          <Input
            label="العنوان"
            name="address"
            value={formData.address}
            onChange={handleChange}
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
