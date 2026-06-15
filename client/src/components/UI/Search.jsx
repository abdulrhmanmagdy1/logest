/**
 * ============================================
 * 🔍 Search Component - نظام إدهام
 * Edham Logistics - Search Component
 * ============================================
 */

import React from 'react';
import { Search as SearchIcon, X } from 'lucide-react';

export default function Search({ value, onChange, placeholder = 'بحث...', onClear, className = '' }) {
  return (
    <div className={`relative ${className}`}>
      <SearchIcon className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
      <input
        type="text"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none transition"
      />
      {value && onClear && (
        <button
          onClick={onClear}
          className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-white transition"
        >
          <X className="w-4 h-4" />
        </button>
      )}
    </div>
  );
}
