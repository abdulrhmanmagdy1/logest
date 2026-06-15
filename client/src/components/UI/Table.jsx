/**
 * ============================================
 * 📊 Table Component - نظام إدهام
 * Edham Logistics - Table Component
 * ============================================
 */

import React from 'react';

export default function Table({ columns, data, onRowClick, loading = false }) {
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="bg-gray-800 p-8 rounded-lg text-center">
        <p className="text-gray-400">لا توجد بيانات</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-800 rounded-lg overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-white text-sm">
          <thead className="bg-gray-700">
            <tr>
              {columns.map((column) => (
                <th
                  key={column.key}
                  className={`px-4 py-3 ${column.align === 'center' ? 'text-center' : column.align === 'left' ? 'text-left' : 'text-right'}`}
                >
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((row, rowIndex) => (
              <tr
                key={rowIndex}
                className={`border-t border-gray-700 ${onRowClick ? 'hover:bg-gray-700 cursor-pointer' : ''}`}
                onClick={() => onRowClick?.(row)}
              >
                {columns.map((column) => (
                  <td
                    key={column.key}
                    className={`px-4 py-3 ${column.align === 'center' ? 'text-center' : column.align === 'left' ? 'text-left' : 'text-right'}`}
                  >
                    {column.render ? column.render(row[column.key], row) : row[column.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
