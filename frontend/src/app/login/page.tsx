'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/Button';
import { authApi } from '@/lib/api';
import { useAuthStore } from '@/store/useAuthStore';
import type { AuthResponse } from '@/lib/auth';

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(true);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const setToken = useAuthStore((state) => state.setToken);
  const setUser = useAuthStore((state) => state.setUser);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.login({ email, password });
      const authData = response.data as AuthResponse;

      setToken(authData.token);
      setUser(authData.user);

      if (rememberMe) {
        localStorage.setItem('edham_token', authData.token);
        localStorage.setItem('edham_user', JSON.stringify(authData.user));
      }

      router.push('/');
    } catch (error) {
      setError('فشل تسجيل الدخول، تحقق من البريد الإلكتروني وكلمة المرور.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.12),transparent_30%),radial-gradient(circle_at_20%_30%,rgba(14,165,233,0.08),transparent_18%),#050816] text-white flex items-center justify-center px-4 py-10">
      <motion.div
        initial={{ opacity: 0, y: 32 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-md rounded-[32px] border border-white/10 bg-slate-950/80 p-8 shadow-edham"
      >
        <div className="space-y-4 text-center">
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">مرحبا بك في ادهام</p>
          <h1 className="text-3xl font-semibold">تسجيل الدخول</h1>
          <p className="text-slate-400">ادخل بريدك الإلكتروني وكلمة المرور للانتقال إلى لوحة التحكم.</p>
        </div>

        <form onSubmit={handleSubmit} className="mt-8 space-y-5">
          <div className="space-y-2">
            <label className="block text-sm text-slate-300">البريد الإلكتروني</label>
            <input
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              placeholder="example@edham.com"
              className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              required
            />
          </div>
          <div className="space-y-2">
            <label className="block text-sm text-slate-300">كلمة المرور</label>
            <input
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              type="password"
              placeholder="********"
              className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              required
            />
          </div>
          <div className="flex items-center justify-between text-sm text-slate-400">
            <label className="inline-flex items-center gap-2">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(event) => setRememberMe(event.target.checked)}
                className="h-4 w-4 rounded border-white/20 bg-slate-900 text-cyan-400"
              />
              تذكرني
            </label>
            <Link href="/reset-password" className="text-cyan-300 hover:text-cyan-200">
              نسيت كلمة المرور؟
            </Link>
          </div>

          {error && <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{error}</p>}

          <Button type="submit" className="w-full py-3" disabled={loading}>
            {loading ? 'جاري تسجيل الدخول...' : 'تسجيل الدخول'}
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-400">
          لا تملك حسابًا؟{' '}
          <Link href="/register" className="text-cyan-300 hover:text-cyan-200">
            سجل الآن
          </Link>
        </p>
      </motion.div>
    </main>
  );
}
