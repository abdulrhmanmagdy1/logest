'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/useAuthStore';

export function RequireAuth({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const token = useAuthStore((state) => state.token);
  const loading = useAuthStore((state) => state.loading);

  useEffect(() => {
    if (!token && !loading) {
      router.push('/login');
    }
  }, [token, loading, router]);

  if (!token) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[#050816] text-white">
        <div className="rounded-3xl border border-white/10 bg-slate-950/80 p-8 text-center shadow-edham">
          <p className="text-lg font-semibold">جارٍ إعادة التوجيه إلى صفحة تسجيل الدخول...</p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}
