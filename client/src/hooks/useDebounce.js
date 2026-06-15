/**
 * ============================================
 * ⏱️ useDebounce Hook - نظام إدهام
 * Debounce hook for delaying function execution
 * ============================================
 */

import { useState, useEffect } from 'react';

/**
 * Debounce a value
 * @param {any} value - Value to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {any} Debounced value
 */
export const useDebounce = (value, delay = 500) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(timer);
    };
  }, [value, delay]);

  return debouncedValue;
};

/**
 * Debounce a function
 * @param {Function} func - Function to debounce
 * @param {number} delay - Delay in milliseconds
 * @returns {Function} Debounced function
 */
export const useDebounceFn = (func, delay = 500) => {
  const [timer, setTimer] = useState(null);

  const debouncedFn = (...args) => {
    if (timer) {
      clearTimeout(timer);
    }

    const newTimer = setTimeout(() => {
      func(...args);
    }, delay);

    setTimer(newTimer);
  };

  return debouncedFn;
};

export default useDebounce;
