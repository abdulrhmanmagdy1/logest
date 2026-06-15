import { create } from 'zustand';

interface AuthState {
  token: string | null;
  user: { id: string; name: string; email: string; role: string } | null;
  loading: boolean;
  error: string | null;
  setUser: (user: AuthState['user']) => void;
  setToken: (token: string | null) => void;
  setLoading: (value: boolean) => void;
  setError: (message: string | null) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: null,
  user: null,
  loading: true,
  error: null,
  setUser: (user) => set({ user }),
  setToken: (token) => set({ token }),
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
  logout: () => set({ token: null, user: null, error: null }),
}));
