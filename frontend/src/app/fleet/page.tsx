'use client';

import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import { trucksApi } from '@/lib/api';

interface Truck {
  _id: string;
  plateNumber: string;
  make?: string;
  model?: string;
  status: string;
  mileage?: number;
}

const STATUS_LABELS: Record<string, string> = {
  active: 'نشط',
  inactive: 'غير نشط',
  maintenance: 'في الصيانة',
  available: 'متاح',
};

const STATUS_COLORS: Record<string, string> = {
  active: 'bg-emerald-500/15 text-emerald-300',
  available: 'bg-cyan-500/15 text-cyan-300',
  maintenance: 'bg-amber-500/15 text-amber-300',
  inactive: 'bg-slate-500/15 text-slate-400',
};

const BLANK_FORM = {
  plateNumber: '', make: '', model: '',
  year: new Date().getFullYear().toString(),
  type: 'medium', capacityWeight: '10000',
  minTemp: '-20', maxTemp: '25',
};

export default function FleetPage() {
  const [trucks, setTrucks]     = useState<Truck[]>([]);
  const [loading, setLoading]   = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm]         = useState(BLANK_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError]   = useState('');

  const active      = trucks.filter((t) => t.status === 'active' || t.status === 'available').length;
  const maintenance = trucks.filter((t) => t.status === 'maintenance').length;

  function fetchTrucks() {
    trucksApi.list()
      .then((res) => {
        const body = res.data as { success: boolean; data: Truck[] };
        if (body.success) setTrucks(body.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }

  useEffect(() => { fetchTrucks(); }, []);

  function set(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleAdd(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      await trucksApi.create({
        plateNumber: form.plateNumber.trim(),
        make: form.make.trim(),
        model: form.model.trim(),
        year: parseInt(form.year),
        type: form.type,
        capacity: { weight: { value: parseFloat(form.capacityWeight), unit: 'kg' } },
        refrigeration: {
          minTemperature: parseFloat(form.minTemp),
          maxTemperature: parseFloat(form.maxTemp),
        },
      });
      setShowModal(false);
      setForm(BLANK_FORM);
      setLoading(true);
      fetchTrucks();
    } catch (err: unknown) {
      const apiErr = err as { response?: { data?: { message?: string; errors?: { msg: string }[] } } };
      setFormError(
        apiErr?.response?.data?.errors?.[0]?.msg ??
        apiErr?.response?.data?.message ??
        'فشل إضافة الشاحنة'
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <DashboardShell title="الأسطول" description="إدارة المركبات والجداول الدورية للصيانة.">

      <Modal title="إضافة شاحنة جديدة" open={showModal} onClose={() => { setShowModal(false); setFormError(''); setForm(BLANK_FORM); }}>
        <form onSubmit={handleAdd} className="grid gap-4">
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="رقم اللوحة" placeholder="ABC-1234" required value={form.plateNumber} onChange={(e) => set('plateNumber', e.target.value)} />
            <Input label="الشركة المصنعة" placeholder="Volvo" required value={form.make} onChange={(e) => set('make', e.target.value)} />
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="الموديل" placeholder="FH16" required value={form.model} onChange={(e) => set('model', e.target.value)} />
            <Input label="سنة الصنع" type="number" placeholder="2022" required value={form.year} onChange={(e) => set('year', e.target.value)} />
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <div>
              <label className="mb-2 block text-sm text-slate-300">نوع الشاحنة</label>
              <select value={form.type} onChange={(e) => set('type', e.target.value)}
                className="w-full rounded-3xl border border-white/10 bg-slate-900/85 px-4 py-3 text-white outline-none transition focus:border-cyan-400">
                <option value="small">صغيرة</option>
                <option value="medium">متوسطة</option>
                <option value="large">كبيرة</option>
                <option value="xl">ضخمة</option>
                <option value="trailer">مقطورة</option>
              </select>
            </div>
            <Input label="الحمولة القصوى (كجم)" type="number" placeholder="10000" required value={form.capacityWeight} onChange={(e) => set('capacityWeight', e.target.value)} />
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="درجة الحرارة الدنيا (°م)" type="number" placeholder="-20" required value={form.minTemp} onChange={(e) => set('minTemp', e.target.value)} />
            <Input label="درجة الحرارة القصوى (°م)" type="number" placeholder="25" required value={form.maxTemp} onChange={(e) => set('maxTemp', e.target.value)} />
          </div>
          {formError && <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{formError}</p>}
          <div className="flex gap-3 pt-2">
            <Button type="submit" disabled={submitting} className="flex-1">
              {submitting ? 'جاري الإضافة...' : 'إضافة الشاحنة'}
            </Button>
            <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>إلغاء</Button>
          </div>
        </form>
      </Modal>

      <div className="grid gap-5">
        <div className="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <Card className="glass p-6">
            <div className="flex items-center justify-between gap-4">
              <div>
                <h2 className="text-xl font-semibold text-white">نظرة عامة على الأسطول</h2>
                <p className="mt-1 text-sm text-slate-400">تحكم في حالة المركبات وجدول الصيانة الذكي.</p>
              </div>
              <button onClick={() => setShowModal(true)}
                className="rounded-full bg-cyan-400 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300 whitespace-nowrap">
                + إضافة شاحنة
              </button>
            </div>
            <div className="mt-6 grid gap-4 sm:grid-cols-3">
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">إجمالي المركبات</p>
                <p className="mt-3 text-3xl font-semibold">{loading ? '—' : trucks.length}</p>
              </div>
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">المركبات النشطة</p>
                <p className="mt-3 text-3xl font-semibold">{loading ? '—' : active}</p>
              </div>
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">في الصيانة</p>
                <p className="mt-3 text-3xl font-semibold">{loading ? '—' : maintenance}</p>
              </div>
            </div>
          </Card>
          <Card className="glass p-6">
            <h3 className="text-lg font-semibold text-white">تنبيهات الصيانة</h3>
            <div className="mt-5">
              {loading ? (
                <p className="text-sm text-slate-500">جاري التحميل...</p>
              ) : maintenance === 0 ? (
                <p className="text-sm text-slate-500">لا توجد مركبات في الصيانة حالياً</p>
              ) : (
                <div className="space-y-4 text-slate-300">
                  {trucks.filter((t) => t.status === 'maintenance').map((t) => (
                    <div key={t._id} className="rounded-3xl bg-slate-900/70 p-4">
                      <p className="text-sm text-slate-400">{t.plateNumber}</p>
                      <p className="mt-2 text-sm">{t.make} {t.model} — في الصيانة</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </Card>
        </div>

        <Card className="glass p-6 overflow-x-auto">
          {loading ? (
            <p className="py-10 text-center text-slate-400">جاري التحميل...</p>
          ) : trucks.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-slate-400">لا توجد مركبات بعد</p>
              <p className="mt-2 text-sm text-slate-600">ستظهر المركبات هنا بعد إضافتها للنظام</p>
            </div>
          ) : (
            <table className="w-full min-w-[680px] border-collapse text-left text-sm text-slate-300">
              <thead>
                <tr className="border-b border-white/10 text-slate-400">
                  <th className="py-4 pr-6">رقم اللوحة</th>
                  <th className="py-4 pr-6">الموديل</th>
                  <th className="py-4 pr-6">الحالة</th>
                  <th className="py-4 pr-6">المسافة المقطوعة</th>
                </tr>
              </thead>
              <tbody>
                {trucks.map((truck) => (
                  <tr key={truck._id} className="border-b border-white/10 transition hover:bg-slate-900/70">
                    <td className="py-4 pr-6 font-semibold text-white">{truck.plateNumber}</td>
                    <td className="py-4 pr-6">{[truck.make, truck.model].filter(Boolean).join(' ') || '—'}</td>
                    <td className="py-4 pr-6">
                      <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${STATUS_COLORS[truck.status] ?? 'bg-slate-500/15 text-slate-300'}`}>
                        {STATUS_LABELS[truck.status] ?? truck.status}
                      </span>
                    </td>
                    <td className="py-4 pr-6">{truck.mileage != null ? truck.mileage.toLocaleString('ar-SA') + ' كم' : '—'}</td>
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
