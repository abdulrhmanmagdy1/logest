'use client';

import { useEffect } from 'react';
import { useAuthStore } from '@/store/useAuthStore';

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const setToken = useAuthStore((state) => state.setToken);
  const setUser = useAuthStore((state) => state.setUser);
  const setLoading = useAuthStore((state) => state.setLoading);

  useEffect(() => {
    const savedToken = typeof window !== 'undefined' ? localStorage.getItem('edham_token') : null;
    const savedUser = typeof window !== 'undefined' ? localStorage.getItem('edham_user') : null;

    if (savedToken) {
      setToken(savedToken);
    }

    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch {
        localStorage.removeItem('edham_user');
      }
    }

    setLoading(false);
  }, [setToken, setUser, setLoading]);

  return <>{children}</>;
}
