'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function RegisterPage() {
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

        <form className="mt-8 grid gap-5">
          <div className="grid gap-4 md:grid-cols-2">
            <Input label="اسم الشركة" placeholder="شركة النقل الذكية" />
            <Input label="اسم المستخدم" placeholder="أماني أحمد" />
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            <Input label="البريد الإلكتروني" placeholder="example@edham.com" type="email" />
            <Input label="رقم الهاتف" placeholder="00966 5X XXX XXXX" type="tel" />
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            <Input label="كلمة المرور" placeholder="********" type="password" />
            <Input label="تأكيد كلمة المرور" placeholder="********" type="password" />
          </div>
          <Button type="submit" className="w-full py-3">
            إنشاء الحساب
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
