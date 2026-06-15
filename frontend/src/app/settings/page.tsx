import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function SettingsPage() {
  return (
    <DashboardShell title="الإعدادات" description="ضبط الشركة والصلاحيات وسياسات الأمان.">
      <div className="grid gap-5 lg:grid-cols-[1.2fr_0.8fr]">
        <Card className="glass p-6">
          <h2 className="text-xl font-semibold text-white">إعدادات الشركة</h2>
          <div className="mt-6 grid gap-4">
            <Input label="اسم الشركة" placeholder="شركة ادهام للنقل" />
            <Input label="البريد الإلكتروني للشركة" placeholder="contact@edhamlogistics.com" type="email" />
            <Input label="رقم الهاتف" placeholder="00966 5X XXX XXXX" type="tel" />
          </div>
          <div className="mt-6 flex items-center gap-3">
            <Button>حفظ التغييرات</Button>
            <Button variant="secondary">إلغاء</Button>
          </div>
        </Card>
        <Card className="glass p-6">
          <h2 className="text-xl font-semibold text-white">صلاحيات المستخدمين</h2>
          <p className="mt-3 text-sm text-slate-400">قم بإدارة الأدوار والمستويات وصولًا إلى المشرفين والسائقين والعملاء.</p>
          <div className="mt-5 space-y-4">
            <div className="rounded-3xl bg-slate-900/70 p-4 text-slate-200">
              <h3 className="font-semibold">Admin</h3>
              <p className="mt-2 text-sm text-slate-400">صلاحيات كاملة على النظام.</p>
            </div>
            <div className="rounded-3xl bg-slate-900/70 p-4 text-slate-200">
              <h3 className="font-semibold">Operations Manager</h3>
              <p className="mt-2 text-sm text-slate-400">إدارة الشحنات والأسطول.</p>
            </div>
          </div>
        </Card>
      </div>
    </DashboardShell>
  );
}
