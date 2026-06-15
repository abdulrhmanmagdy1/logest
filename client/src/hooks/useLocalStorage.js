/**
 * ============================================
 * 💾 useLocalStorage Hook - نظام إدهام
 * Local storage hook with state synchronization
 * ============================================
 */

import { useState, useEffect, useCallback } from 'react';

export const useLocalStorage = (key, initialValue = null) => {
  // Get initial value from localStorage or use initialValue
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  });

  // Set value in localStorage when state changes
  const setValue = useCallback((value) => {
    try {
      // Allow value to be a function so we have the same API as useState
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      
      setStoredValue(valueToStore);
      
      if (valueToStore === null || valueToStore === undefined) {
        window.localStorage.removeItem(key);
      } else {
        window.localStorage.setItem(key, JSON.stringify(valueToStore));
      }
    } catch (error) {
      console.error(`Error setting localStorage key "${key}":`, error);
    }
  }, [key, storedValue]);

  // Remove value from localStorage
  const removeValue = useCallback(() => {
    try {
      window.localStorage.removeItem(key);
      setStoredValue(null);
    } catch (error) {
      console.error(`Error removing localStorage key "${key}":`, error);
    }
  }, [key]);

  // Listen for changes in other tabs/windows
  useEffect(() => {
    const handleStorageChange = (event) => {
      if (event.key === key) {
        try {
          const newValue = event.newValue ? JSON.parse(event.newValue) : null;
          setStoredValue(newValue);
        } catch (error) {
          console.error(`Error parsing localStorage change for key "${key}":`, error);
        }
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, [key]);

  return [storedValue, setValue, removeValue];
};

export default useLocalStorage;
