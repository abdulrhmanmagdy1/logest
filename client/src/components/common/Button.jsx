/**
 * ============================================
 * 🔘 Button Component - نظام إدهام
 * Professional button component
 * ============================================
 */

import React from 'react';
import { Loader2 } from 'lucide-react';

const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  isLoading = false,
  isDisabled = false,
  leftIcon,
  rightIcon,
  onClick,
  type = 'button',
  className = '',
  fullWidth = false,
  ...props
}) => {
  const baseStyles = 'inline-flex items-center justify-center font-medium transition-all duration-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2';
  
  const variants = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500 disabled:bg-blue-400',
    secondary: 'bg-gray-200 text-gray-800 hover:bg-gray-300 focus:ring-gray-500 disabled:bg-gray-100',
    success: 'bg-green-600 text-white hover:bg-green-700 focus:ring-green-500 disabled:bg-green-400',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500 disabled:bg-red-400',
    warning: 'bg-yellow-500 text-white hover:bg-yellow-600 focus:ring-yellow-500 disabled:bg-yellow-400',
    outline: 'border-2 border-blue-600 text-blue-600 hover:bg-blue-50 focus:ring-blue-500 disabled:opacity-50',
    ghost: 'text-gray-600 hover:bg-gray-100 focus:ring-gray-500 disabled:opacity-50',
  };
  
  const sizes = {
    xs: 'px-3 py-1.5 text-xs',
    sm: 'px-4 py-2 text-sm',
    md: 'px-6 py-2.5 text-base',
    lg: 'px-8 py-3 text-lg',
    xl: 'px-10 py-4 text-xl',
  };

  const classes = [
    baseStyles,
    variants[variant],
    sizes[size],
    fullWidth ? 'w-full' : '',
    isLoading || isDisabled ? 'cursor-not-allowed opacity-70' : '',
    className,
  ].join(' ');

  return (
    <button
      type={type}
      className={classes}
      onClick={onClick}
      disabled={isLoading || isDisabled}
      {...props}
    >
      {isLoading && (
        <Loader2 className="w-5 h-5 mr-2 animate-spin" />
      )}
      {!isLoading && leftIcon && (
        <span className="mr-2">{leftIcon}</span>
      )}
      {children}
      {!isLoading && rightIcon && (
        <span className="ml-2">{rightIcon}</span>
      )}
    </button>
  );
};

export default Button;
