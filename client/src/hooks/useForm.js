/**
 * ============================================
 * 📝 useForm Hook - نظام إدهام
 * Form handling hook with validation
 * ============================================
 */

import { useState, useCallback } from 'react';

export const useForm = (initialValues = {}, validators = {}) => {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validate = useCallback(() => {
    const newErrors = {};
    let isValid = true;

    for (const [field, validatorsList] of Object.entries(validators)) {
      const value = values[field];
      
      for (const validator of validatorsList) {
        const result = validator(value, values);
        if (result !== true) {
          newErrors[field] = result;
          isValid = false;
          break;
        }
      }
    }

    setErrors(newErrors);
    return isValid;
  }, [values, validators]);

  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;
    const fieldValue = type === 'checkbox' ? checked : value;
    
    setValues(prev => ({
      ...prev,
      [name]: fieldValue,
    }));

    // Clear error when field is changed
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined,
      }));
    }
  }, [errors]);

  const handleBlur = useCallback((e) => {
    const { name } = e.target;
    
    setTouched(prev => ({
      ...prev,
      [name]: true,
    }));

    // Validate single field
    if (validators[name]) {
      const value = values[name];
      for (const validator of validators[name]) {
        const result = validator(value, values);
        if (result !== true) {
          setErrors(prev => ({
            ...prev,
            [name]: result,
          }));
          break;
        } else {
          setErrors(prev => ({
            ...prev,
            [name]: undefined,
          }));
        }
      }
    }
  }, [values, validators]);

  const setValue = useCallback((name, value) => {
    setValues(prev => ({
      ...prev,
      [name]: value,
    }));
  }, []);

  const reset = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
    setIsSubmitting(false);
  }, [initialValues]);

  const setSubmitting = useCallback((value) => {
    setIsSubmitting(value);
  }, []);

  return {
    values,
    errors,
    touched,
    isSubmitting,
    handleChange,
    handleBlur,
    setValue,
    setValues,
    reset,
    validate,
    setSubmitting,
  };
};

export default useForm;
