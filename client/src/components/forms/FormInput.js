import React from 'react';

const FormInput = ({ label, name, value, onChange, type = 'text', placeholder = '', required = false, icon: Icon, disabled = false }) => {
  return (
    <div className="input-group">
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="text-red-400 mr-1">*</span>}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <Icon className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-500" />
        )}
        <input
          type={type}
          name={name}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          disabled={disabled}
          className={`input-field ${Icon ? 'pr-12' : ''}`}
        />
      </div>
    </div>
  );
};

export default FormInput;
