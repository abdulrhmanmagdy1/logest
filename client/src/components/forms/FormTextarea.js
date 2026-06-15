import React from 'react';

const FormTextarea = ({ label, name, value, onChange, placeholder = '', required = false, rows = 4 }) => {
  return (
    <div className="input-group">
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="text-red-400 mr-1">*</span>}
        </label>
      )}
      <textarea
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        rows={rows}
        className="input-field resize-none"
      />
    </div>
  );
};

export default FormTextarea;
