/**
 * ============================================
 * 🌐 useApi Hook - نظام إدهام
 * Edham Logistics - API Hook
 * ============================================
 */

import { useState, useEffect } from 'react';
import api from '../services/api';

export const useApi = (url, options = {}) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.get(url, options);
      setData(response.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [url]);

  return { data, loading, error, refetch: fetchData };
};

export const usePostApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const postData = async (url, data) => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.post(url, data);
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { postData, loading, error };
};

export const usePutApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const putData = async (url, data) => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.put(url, data);
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { putData, loading, error };
};

export const useDeleteApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const deleteData = async (url) => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.delete(url);
      return response.data;
    } catch (err) {
      setError(err.response?.data?.message || err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return { deleteData, loading, error };
};

export default useApi;
