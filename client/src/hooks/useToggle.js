/**
 * ============================================
 * 🔘 useToggle Hook - نظام إدهام
 * Toggle boolean state hook
 * ============================================
 */

import { useState, useCallback } from 'react';

/**
 * Toggle hook for boolean state
 * @param {boolean} initialValue - Initial value
 * @returns {Array} [value, toggle, setTrue, setFalse, setValue]
 */
export const useToggle = (initialValue = false) => {
  const [value, setValue] = useState(initialValue);

  const toggle = useCallback(() => {
    setValue(prev => !prev);
  }, []);

  const setTrue = useCallback(() => {
    setValue(true);
  }, []);

  const setFalse = useCallback(() => {
    setValue(false);
  }, []);

  return [value, toggle, setTrue, setFalse, setValue];
};

export default useToggle;
