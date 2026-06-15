'use client';

import { motion } from 'framer-motion';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function ResetPasswordPage() {
  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.12),transparent_30%),radial-gradient(circle_at_80%_10%,rgba(59,130,246,0.08),transparent_24%),#050816] text-white flex items-center justify-center px-4 py-10">
      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-md rounded-[32px] border border-white/10 bg-slate-950/80 p-8 shadow-edham"
      >
        <div className="space-y-4 text-center">
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">إعادة تعيين كلمة المرور</p>
          <h1 className="text-3xl font-semibold">أنشئ كلمة مرور جديدة</h1>
          <p className="text-slate-400">أدخل كلمة المرور الجديدة لتأمين حسابك واستعادة وصولك.</p>
        </div>
        <form className="mt-8 space-y-5">
          <Input label="كلمة المرور الجديدة" type="password" placeholder="********" />
          <Input label="تأكيد كلمة المرور" type="password" placeholder="********" />
          <Button type="submit" className="w-full py-3">
            حفظ التغييرات
          </Button>
        </form>
      </motion.div>
    </main>
  );
}
