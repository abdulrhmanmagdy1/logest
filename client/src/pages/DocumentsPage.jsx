/**
 * ============================================
 * 📄 Documents Page - نظام إدهام
 * Edham Logistics - Document Management
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, FileText, Upload, Download, Trash2, Search, Filter } from 'lucide-react';
import Button from '../components/UI/Button';

export default function DocumentsPage() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterType, setFilterType] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchDocuments();
  }, [filterType]);

  const fetchDocuments = async () => {
    try {
      setLoading(true);
      const params = filterType !== 'all' ? { type: filterType } : {};
      const response = await api.get('/documents', { params });
      setDocuments(response.data.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = () => {
    // Implement upload functionality
    console.log('Upload document');
  };

  const handleDownload = (doc) => {
    // Implement download functionality
    console.log('Download document:', doc);
  };

  const handleDelete = async (docId) => {
    if (window.confirm('هل أنت متأكد من حذف هذا المستند؟')) {
      try {
        await api.delete(`/documents/${docId}`);
        setDocuments(prev => prev.filter(doc => doc._id !== docId));
      } catch (err) {
        console.error('Delete error:', err);
      }
    }
  };

  const filteredDocuments = documents.filter(doc => {
    if (searchTerm) {
      const searchLower = searchTerm.toLowerCase();
      return (
        doc.name?.toLowerCase().includes(searchLower) ||
        doc.description?.toLowerCase().includes(searchLower)
      );
    }
    return true;
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="animate-spin w-8 h-8 text-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-500 text-white p-4 rounded">
        <AlertCircle className="inline mr-2" />
        {error}
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">المستندات</h1>
          <p className="text-gray-400 mt-1">إدارة المستندات والمرفقات</p>
        </div>
        <Button
          onClick={handleUpload}
          icon={<Upload className="w-4 h-4" />}
        >
          رفع مستند
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="إجمالي المستندات" value={documents.length} color="blue" />
        <StatCard title="فواتير" value="12" color="green" />
        <StatCard title="عقود" value="8" color="yellow" />
        <StatCard title="تقارير" value="15" color="gold" />
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-4 mb-6">
        <select
          value={filterType}
          onChange={(e) => setFilterType(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الأنواع</option>
          <option value="invoice">فواتير</option>
          <option value="contract">عقود</option>
          <option value="report">تقارير</option>
          <option value="certificate">شهادات</option>
          <option value="other">أخرى</option>
        </select>

        <div className="flex-1 relative">
          <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="بحث في المستندات..."
            className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
          />
        </div>
      </div>

      {/* Documents Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredDocuments.length === 0 ? (
          <div className="col-span-full bg-gray-800 p-8 rounded-lg text-center">
            <FileText className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <p className="text-gray-400">لا توجد مستندات</p>
          </div>
        ) : (
          filteredDocuments.map((doc) => (
            <DocumentCard
              key={doc._id}
              document={doc}
              onDownload={() => handleDownload(doc)}
              onDelete={() => handleDelete(doc._id)}
            />
          ))
        )}
      </div>
    </div>
  );
}

function StatCard({ title, value, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600',
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function DocumentCard({ document, onDownload, onDelete }) {
  const typeIcons = {
    invoice: <FileText className="w-8 h-8" />,
    contract: <FileText className="w-8 h-8" />,
    report: <FileText className="w-8 h-8" />,
    certificate: <FileText className="w-8 h-8" />,
    other: <FileText className="w-8 h-8" />
  };

  const typeColors = {
    invoice: 'bg-green-600',
    contract: 'bg-blue-600',
    report: 'bg-purple-600',
    certificate: 'bg-yellow-600',
    other: 'bg-gray-600'
  };

  const typeLabels = {
    invoice: 'فاتورة',
    contract: 'عقد',
    report: 'تقرير',
    certificate: 'شهادة',
    other: 'أخرى'
  };

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div className="flex items-start justify-between mb-4">
        <div className={`${typeColors[document.type] || typeColors.other} p-3 rounded`}>
          {typeIcons[document.type] || typeIcons.other}
        </div>
        <div className="flex gap-2">
          <button
            onClick={onDownload}
            className="text-blue-500 hover:text-blue-400"
            title="تحميل"
          >
            <Download className="w-4 h-4" />
          </button>
          <button
            onClick={onDelete}
            className="text-red-500 hover:text-red-400"
            title="حذف"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      <h3 className="text-white font-semibold mb-2">{document.name}</h3>
      <p className="text-gray-400 text-sm mb-3">{document.description || 'بدون وصف'}</p>

      <div className="flex items-center justify-between text-sm">
        <span className="text-gray-500">
          {new Date(document.uploadedAt).toLocaleDateString('ar-SA')}
        </span>
        <span className="text-gray-400">
          {(document.size / 1024).toFixed(1)} KB
        </span>
      </div>

      <div className="mt-3">
        <span className="bg-gray-700 text-gray-300 px-2 py-1 rounded text-xs">
          {typeLabels[document.type] || document.type}
        </span>
      </div>
    </div>
  );
}
