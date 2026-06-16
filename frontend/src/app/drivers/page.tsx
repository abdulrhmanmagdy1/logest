'use client';

import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import { usersApi } from '@/lib/api';

function normalizePhone(raw: string): string {
  const d = raw.replace(/\s+/g, '');
  if (d.startsWith('00966')) return '0' + d.slice(5);
  if (d.startsWith('+966'))  return '0' + d.slice(4);
  if (d.startsWith('966'))   return '0' + d.slice(3);
  return d;
}

interface Driver {
  _id: string;
  firstName: string;
  lastName: string;
  status: string;
  driverInfo?: { rating?: number; totalTrips?: number; isAvailable?: boolean };
}

const STATUS_LABELS: Record<string, string> = {
  active: 'نشط',
  inactive: 'غير نشط',
  pending: 'في الانتظار',
  suspended: 'موقوف',
};

const STATUS_COLORS: Record<string, string> = {
  active: 'bg-emerald-500/15 text-emerald-300',
  pending: 'bg-amber-500/15 text-amber-300',
  inactive: 'bg-slate-500/15 text-slate-400',
  suspended: 'bg-rose-500/15 text-rose-300',
};

const BLANK = { firstName: '', lastName: '', email: '', phone: '', password: '' };

export default function DriversPage() {
  const [drivers, setDrivers]   = useState<Driver[]>([]);
  const [loading, setLoading]   = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm]         = useState(BLANK);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError]   = useState('');

  function fetchDrivers() {
    usersApi.list({ role: 'driver' })
      .then((res) => {
        const body = res.data as { success: boolean; data: Driver[] };
        if (body.success) setDrivers(body.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }

  useEffect(() => { fetchDrivers(); }, []);

  function set(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleAdd(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      await usersApi.create({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        phone: normalizePhone(form.phone),
        password: form.password,
        role: 'driver',
      });
      setShowModal(false);
      setForm(BLANK);
      setLoading(true);
      fetchDrivers();
    } catch (err: unknown) {
      const apiErr = err as { response?: { data?: { message?: string; errors?: { msg: string }[] } } };
      setFormError(
        apiErr?.response?.data?.errors?.[0]?.msg ??
        apiErr?.response?.data?.message ??
        'فشل إضافة السائق'
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <DashboardShell title="السائقين" description="مراقبة الأداء والمهام الحالية.">

      <Modal title="إضافة سائق جديد" open={showModal} onClose={() => { setShowModal(false); setFormError(''); setForm(BLANK); }}>
        <form onSubmit={handleAdd} className="grid gap-4">
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="الاسم الأول" placeholder="محمد" required value={form.firstName} onChange={(e) => set('firstName', e.target.value)} />
            <Input label="اسم العائلة" placeholder="العمري" required value={form.lastName} onChange={(e) => set('lastName', e.target.value)} />
          </div>
          <Input label="البريد الإلكتروني" type="email" placeholder="driver@example.com" required value={form.email} onChange={(e) => set('email', e.target.value)} />
          <Input label="رقم الهاتف" placeholder="00966 5X XXX XXXX" type="tel" required value={form.phone} onChange={(e) => set('phone', e.target.value)} />
          <Input label="كلمة المرور" type="password" placeholder="8 أحرف على الأقل" required value={form.password} onChange={(e) => set('password', e.target.value)} />
          {formError && <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{formError}</p>}
          <div className="flex gap-3 pt-2">
            <Button type="submit" disabled={submitting} className="flex-1">
              {submitting ? 'جاري الإضافة...' : 'إضافة السائق'}
            </Button>
            <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>إلغاء</Button>
          </div>
        </form>
      </Modal>

      <div className="grid gap-5">
        <Card className="glass p-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">فريق السائقين</h2>
              <p className="text-sm text-slate-400">عرض سريع لحالة السائقين ومهامهم الحالية.</p>
            </div>
            <button onClick={() => setShowModal(true)}
              className="rounded-full bg-cyan-400 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300">
              + إضافة سائق جديد
            </button>
          </div>
        </Card>

        <Card className="glass p-6 overflow-x-auto">
          {loading ? (
            <p className="py-10 text-center text-slate-400">جاري التحميل...</p>
          ) : drivers.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-slate-400">لا يوجد سائقون بعد</p>
              <p className="mt-2 text-sm text-slate-600">ستظهر بيانات السائقين هنا بعد إضافتهم للنظام</p>
            </div>
          ) : (
            <table className="w-full min-w-[680px] border-collapse text-left text-sm text-slate-300">
              <thead>
                <tr className="border-b border-white/10 text-slate-400">
                  <th className="py-4 pr-6">اسم السائق</th>
                  <th className="py-4 pr-6">الحالة</th>
                  <th className="py-4 pr-6">التقييم</th>
                  <th className="py-4 pr-6">عدد الرحلات</th>
                </tr>
              </thead>
              <tbody>
                {drivers.map((driver) => (
                  <tr key={driver._id} className="border-b border-white/10 transition hover:bg-slate-900/70">
                    <td className="py-4 pr-6 font-semibold text-white">{driver.firstName} {driver.lastName}</td>
                    <td className="py-4 pr-6">
                      <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${STATUS_COLORS[driver.status] ?? 'bg-slate-500/15 text-slate-300'}`}>
                        {STATUS_LABELS[driver.status] ?? driver.status}
                      </span>
                    </td>
                    <td className="py-4 pr-6">{driver.driverInfo?.rating?.toFixed(1) ?? '—'}</td>
                    <td className="py-4 pr-6">{driver.driverInfo?.totalTrips ?? 0}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Card>
      </div>
    </DashboardShell>
  );
}
