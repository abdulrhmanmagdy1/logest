/**
 * ============================================
 * 📄 Invoice Form Component - نظام إدهام
 * Edham Logistics - Invoice Form
 * ============================================
 */

import React, { useState } from 'react';
import api from '../../services/api';
import { FileText, Save, X, Plus, Trash2 } from 'lucide-react';
import Input from '../UI/Input';
import Select from '../UI/Input';
import Button from '../UI/Button';

export default function InvoiceForm({ invoice, onSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    clientId: invoice?.client?._id || '',
    issueDate: invoice?.issueDate || new Date().toISOString().split('T')[0],
    dueDate: invoice?.dueDate || '',
    paymentMethod: invoice?.paymentMethod || 'bank_transfer',
    items: invoice?.items || [{ description: '', quantity: 1, price: 0 }]
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleItemChange = (index, field, value) => {
    const newItems = [...formData.items];
    newItems[index][field] = value;
    setFormData(prev => ({ ...prev, items: newItems }));
  };

  const addItem = () => {
    setFormData(prev => ({
      ...prev,
      items: [...prev.items, { description: '', quantity: 1, price: 0 }]
    }));
  };

  const removeItem = (index) => {
    const newItems = formData.items.filter((_, i) => i !== index);
    setFormData(prev => ({ ...prev, items: newItems }));
  };

  const calculateTotal = () => {
    return formData.items.reduce((sum, item) => sum + (item.quantity * item.price), 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const payload = {
        ...formData,
        total: calculateTotal()
      };

      if (invoice?._id) {
        await api.put(`/invoices/${invoice._id}`, payload);
      } else {
        await api.post('/invoices', payload);
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
          <FileText className="w-5 h-5" />
          {invoice?._id ? 'تعديل الفاتورة' : 'فاتورة جديدة'}
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
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
          <Select
            label="العميل"
            name="clientId"
            value={formData.clientId}
            onChange={handleChange}
            options={[
              { value: '', label: 'اختر العميل' },
              { value: '1', label: 'شركة ABC' },
              { value: '2', label: 'شركة XYZ' }
            ]}
            required
          />
          <Input
            label="تاريخ الإصدار"
            name="issueDate"
            type="date"
            value={formData.issueDate}
            onChange={handleChange}
            required
          />
          <Input
            label="تاريخ الاستحقاق"
            name="dueDate"
            type="date"
            value={formData.dueDate}
            onChange={handleChange}
            required
          />
          <Select
            label="طريقة الدفع"
            name="paymentMethod"
            value={formData.paymentMethod}
            onChange={handleChange}
            options={[
              { value: 'bank_transfer', label: 'تحويل بنكي' },
              { value: 'cash', label: 'نقداً' },
              { value: 'card', label: 'بطاقة' },
              { value: 'check', label: 'شيك' }
            ]}
          />
        </div>

        {/* Items */}
        <div className="mb-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-bold text-white">البنود</h3>
            <Button
              type="button"
              variant="secondary"
              size="sm"
              onClick={addItem}
              icon={<Plus className="w-4 h-4" />}
            >
              إضافة بند
            </Button>
          </div>

          <div className="space-y-3">
            {formData.items.map((item, index) => (
              <div key={index} className="flex gap-2 items-start">
                <div className="flex-1">
                  <Input
                    label="الوصف"
                    value={item.description}
                    onChange={(e) => handleItemChange(index, 'description', e.target.value)}
                    required
                  />
                </div>
                <div className="w-24">
                  <Input
                    label="الكمية"
                    type="number"
                    value={item.quantity}
                    onChange={(e) => handleItemChange(index, 'quantity', parseInt(e.target.value))}
                    required
                  />
                </div>
                <div className="w-32">
                  <Input
                    label="السعر"
                    type="number"
                    value={item.price}
                    onChange={(e) => handleItemChange(index, 'price', parseFloat(e.target.value))}
                    required
                  />
                </div>
                <div className="w-24 pt-6">
                  <p className="text-white font-semibold">
                    {item.quantity * item.price} ريال
                  </p>
                </div>
                {formData.items.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeItem(index)}
                    className="text-red-500 hover:text-red-400 mt-6"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Total */}
        <div className="bg-gray-700 p-4 rounded mb-6">
          <div className="flex justify-between items-center">
            <span className="text-white font-semibold">الإجمالي</span>
            <span className="text-2xl font-bold text-blue-500">{calculateTotal()} ريال</span>
          </div>
        </div>

        <div className="flex justify-end gap-2">
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
