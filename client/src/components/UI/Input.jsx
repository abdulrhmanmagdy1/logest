/**
 * ============================================
 * 📝 Input Component - نظام إدهام
 * Edham Logistics - Input Component
 * ============================================
 */

import React from 'react';

export default function Input({
  label,
  type = 'text',
  placeholder = '',
  value,
  onChange,
  name,
  error,
  disabled = false,
  required = false,
  icon: Icon,
  className = ''
}) {
  return (
    <div className={`mb-4 ${className}`}>
      {label && (
        <label className="block text-gray-300 mb-2">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <div className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400">
            <Icon className="w-5 h-5" />
          </div>
        )}
        <input
          type={type}
          name={name}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          disabled={disabled}
          required={required}
          className={`
            w-full bg-gray-700 text-white px-4 py-2 rounded
            border ${error ? 'border-red-500' : 'border-gray-600'}
            focus:border-blue-500 outline-none transition
            ${Icon ? 'pr-10' : ''}
            disabled:opacity-50 disabled:cursor-not-allowed
          `}
        />
      </div>
      {error && (
        <p className="text-red-500 text-sm mt-1">{error}</p>
      )}
    </div>
  );
}

export function TextArea({
  label,
  placeholder = '',
  value,
  onChange,
  name,
  error,
  disabled = false,
  required = false,
  rows = 3,
  className = ''
}) {
  return (
    <div className={`mb-4 ${className}`}>
      {label && (
        <label className="block text-gray-300 mb-2">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <textarea
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        disabled={disabled}
        required={required}
        rows={rows}
        className={`
          w-full bg-gray-700 text-white px-4 py-2 rounded
          border ${error ? 'border-red-500' : 'border-gray-600'}
          focus:border-blue-500 outline-none transition resize-none
          disabled:opacity-50 disabled:cursor-not-allowed
        `}
      />
      {error && (
        <p className="text-red-500 text-sm mt-1">{error}</p>
      )}
    </div>
  );
}

export function Select({
  label,
  options = [],
  value,
  onChange,
  name,
  error,
  disabled = false,
  required = false,
  placeholder = 'اختر...',
  className = ''
}) {
  return (
    <div className={`mb-4 ${className}`}>
      {label && (
        <label className="block text-gray-300 mb-2">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <select
        name={name}
        value={value}
        onChange={onChange}
        disabled={disabled}
        required={required}
        className={`
          w-full bg-gray-700 text-white px-4 py-2 rounded
          border ${error ? 'border-red-500' : 'border-gray-600'}
          focus:border-blue-500 outline-none transition
          disabled:opacity-50 disabled:cursor-not-allowed
        `}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {error && (
        <p className="text-red-500 text-sm mt-1">{error}</p>
      )}
    </div>
  );
}
