/**
 * ============================================
 * 🔽 Filter Component - نظام إدهام
 * Edham Logistics - Filter Component
 * ============================================
 */

import React from 'react';
import { Filter as FilterIcon, X } from 'lucide-react';

export default function Filter({ filters, onFilterChange, onClear, className = '' }) {
  return (
    <div className={`flex flex-wrap gap-2 ${className}`}>
      {filters.map((filter) => (
        <div key={filter.key} className="flex items-center gap-2">
          <select
            value={filter.value}
            onChange={(e) => onFilterChange(filter.key, e.target.value)}
            className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none transition"
          >
            <option value="">{filter.placeholder}</option>
            {filter.options.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      ))}
      {onClear && (
        <button
          onClick={onClear}
          className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded flex items-center gap-2 transition"
        >
          <X className="w-4 h-4" />
          مسح
        </button>
      )}
    </div>
  );
}

export function FilterButton({ onClick, active = false, count = 0 }) {
  return (
    <button
      onClick={onClick}
      className={`relative flex items-center gap-2 px-4 py-2 rounded transition ${
        active
          ? 'bg-blue-600 text-white'
          : 'bg-gray-700 hover:bg-gray-600 text-white'
      }`}
    >
      <FilterIcon className="w-4 h-4" />
      <span>تصفية</span>
      {count > 0 && (
        <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full text-xs flex items-center justify-center">
          {count}
        </span>
      )}
    </button>
  );
}
