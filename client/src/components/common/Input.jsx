/**
 * ============================================
 * 📝 Input Component - نظام إدهام
 * Professional input component
 * ============================================
 */

import React, { forwardRef } from 'react';
import { Eye, EyeOff } from 'lucide-react';

const Input = forwardRef(({
  label,
  error,
  helperText,
  leftIcon,
  rightIcon,
  isPassword = false,
  showPasswordToggle = false,
  showPassword = false,
  onTogglePassword,
  isRequired = false,
  isDisabled = false,
  isReadOnly = false,
  className = '',
  containerClassName = '',
  ...props
}, ref) => {
  const [internalShowPassword, setInternalShowPassword] = React.useState(false);
  
  const actualShowPassword = showPasswordToggle 
    ? (showPassword !== undefined ? showPassword : internalShowPassword)
    : false;
    
  const handleTogglePassword = () => {
    if (onTogglePassword) {
      onTogglePassword();
    } else {
      setInternalShowPassword(!internalShowPassword);
    }
  };

  const baseStyles = 'block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 transition-colors duration-200';
  
  const stateStyles = error
    ? 'border-red-300 focus:border-red-500 focus:ring-red-500'
    : 'border-gray-300 focus:border-blue-500 focus:ring-blue-500';
    
  const disabledStyles = (isDisabled || isReadOnly) ? 'bg-gray-100 cursor-not-allowed' : '';
  
  const iconPadding = leftIcon ? 'pl-10' : '';
  const rightPadding = (rightIcon || (isPassword && showPasswordToggle)) ? 'pr-10' : '';

  const classes = [
    baseStyles,
    stateStyles,
    disabledStyles,
    iconPadding,
    rightPadding,
    className,
  ].join(' ');

  const inputType = isPassword && !actualShowPassword ? 'password' : props.type || 'text';

  return (
    <div className={containerClassName}>
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1.5">
          {label}
          {isRequired && <span className="text-red-500 mr-1">*</span>}
        </label>
      )}
      
      <div className="relative">
        {leftIcon && (
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
            {leftIcon}
          </div>
        )}
        
        <input
          ref={ref}
          className={classes}
          disabled={isDisabled}
          readOnly={isReadOnly}
          type={inputType}
          {...props}
        />
        
        {rightIcon && !isPassword && (
          <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none text-gray-400">
            {rightIcon}
          </div>
        )}
        
        {isPassword && showPasswordToggle && (
          <button
            type="button"
            onClick={handleTogglePassword}
            className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
          >
            {actualShowPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
          </button>
        )}
      </div>
      
      {error && (
        <p className="mt-1.5 text-sm text-red-600">{error}</p>
      )}
      
      {helperText && !error && (
        <p className="mt-1.5 text-sm text-gray-500">{helperText}</p>
      )}
    </div>
  );
});

Input.displayName = 'Input';

export default Input;
