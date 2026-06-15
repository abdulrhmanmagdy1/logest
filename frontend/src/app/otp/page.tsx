'use client';

import { motion } from 'framer-motion';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function OtpPage() {
  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.12),transparent_30%),radial-gradient(circle_at_80%_10%,rgba(16,185,129,0.08),transparent_24%),#050816] text-white flex items-center justify-center px-4 py-10">
      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-md rounded-[32px] border border-white/10 bg-slate-950/80 p-8 shadow-edham"
      >
        <div className="space-y-4 text-center">
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">التحقق</p>
          <h1 className="text-3xl font-semibold">رمز التحقق OTP</h1>
          <p className="text-slate-400">أدخل رمز التحقق المرسل إلى بريدك الإلكتروني أو رقم الهاتف لإكمال التسجيل.</p>
        </div>
        <form className="mt-8 space-y-5">
          <div>
            <label className="block text-sm text-slate-200">رمز التحقق</label>
            <div className="grid grid-cols-4 gap-3">
              {Array.from({ length: 4 }).map((_, index) => (
                <Input key={index} className="text-center text-xl font-semibold" maxLength={1} />
              ))}
            </div>
          </div>
          <Button type="submit" className="w-full py-3">
            تأكيد الرمز
          </Button>
          <p className="text-center text-sm text-slate-500">
            لم تتلق رمزًا؟ <a href="#" className="text-cyan-300">أعد الإرسال</a>
          </p>
        </form>
      </motion.div>
    </main>
  );
}
