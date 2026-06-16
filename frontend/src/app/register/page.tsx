'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/Button';
import { authApi } from '@/lib/api';

export default function RegisterPage() {
  const router = useRouter();

  const [companyName, setCompanyName] = useState('');
  const [fullName,    setFullName]    = useState('');
  const [email,       setEmail]       = useState('');
  const [phone,       setPhone]       = useState('');
  const [password,    setPassword]    = useState('');
  const [confirm,     setConfirm]     = useState('');
  const [error,       setError]       = useState('');
  const [success,     setSuccess]     = useState('');
  const [loading,     setLoading]     = useState(false);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (password !== confirm) {
      setError('كلمة المرور وتأكيدها غير متطابقتين.');
      return;
    }
    if (password.length < 8) {
      setError('كلمة المرور يجب أن تكون 8 أحرف على الأقل.');
      return;
    }

    // split "الاسم الكامل" → firstName + lastName
    const parts     = fullName.trim().split(/\s+/);
    const firstName = parts[0] ?? fullName.trim();
    const lastName  = parts.slice(1).join(' ') || firstName;

    setLoading(true);
    try {
      await authApi.register({ firstName, lastName, email, phone, password, companyName });
      setSuccess('تم إنشاء الحساب بنجاح! سيتم تحويلك لتسجيل الدخول...');
      setTimeout(() => router.push('/login'), 2000);
    } catch (err: unknown) {
      const apiErr = err as { response?: { data?: { message?: string; errors?: { msg: string }[] } } };
      const msg =
        apiErr?.response?.data?.errors?.[0]?.msg ??
        apiErr?.response?.data?.message ??
        'فشل إنشاء الحساب، تحقق من البيانات وحاول مرة أخرى.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(59,130,246,0.12),transparent_30%),radial-gradient(circle_at_80%_10%,rgba(8,145,178,0.08),transparent_24%),#050816] text-white flex items-center justify-center px-4 py-10">
      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-3xl rounded-[32px] border border-white/10 bg-slate-950/80 p-8 shadow-edham"
      >
        <div className="grid gap-4 md:grid-cols-[1.2fr_0.8fr] md:items-end">
          <div>
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">انضم الآن</p>
            <h1 className="text-3xl font-semibold">إنشاء حساب جديد</h1>
            <p className="mt-2 text-slate-400">ابدأ في إدارة الطلبات، الأسطول، والتقارير من لوحة تحكم واحدة.</p>
          </div>
          <div className="rounded-3xl bg-slate-900/70 p-4 text-slate-300">
            <p className="text-sm text-cyan-300">لماذا Edham؟</p>
            <ul className="mt-3 space-y-2 text-sm text-slate-400">
              <li>• لوحة تحكم احترافية</li>
              <li>• تتبع مباشر للشحنات</li>
              <li>• نظام صلاحيات متقدم</li>
            </ul>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="mt-8 grid gap-5">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">اسم الشركة</label>
              <input
                value={companyName}
                onChange={(e) => setCompanyName(e.target.value)}
                placeholder="شركة النقل الذكية"
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">الاسم الكامل</label>
              <input
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="أماني أحمد"
                required
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">البريد الإلكتروني</label>
              <input
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                type="email"
                placeholder="example@edham.com"
                required
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">
                رقم الهاتف <span className="text-slate-500 text-xs">(05XXXXXXXX)</span>
              </label>
              <input
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="0512345678"
                required
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">كلمة المرور</label>
              <input
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                type="password"
                placeholder="8 أحرف على الأقل"
                required
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
            <div className="space-y-2">
              <label className="block text-sm text-slate-300">تأكيد كلمة المرور</label>
              <input
                value={confirm}
                onChange={(e) => setConfirm(e.target.value)}
                type="password"
                placeholder="********"
                required
                className="w-full rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white outline-none transition focus:border-cyan-400"
              />
            </div>
          </div>

          {error && (
            <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{error}</p>
          )}
          {success && (
            <p className="rounded-3xl bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">{success}</p>
          )}

          <Button type="submit" className="w-full py-3" disabled={loading}>
            {loading ? 'جاري إنشاء الحساب...' : 'إنشاء الحساب'}
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-400">
          لديك حساب بالفعل؟{' '}
          <Link href="/login" className="text-cyan-300 hover:text-cyan-200">
            تسجيل الدخول
          </Link>
        </p>
      </motion.div>
    </main>
  );
}
