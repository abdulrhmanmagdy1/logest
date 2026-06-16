'use client';

import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
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

export default function FleetPage() {
  const [trucks, setTrucks]   = useState<Truck[]>([]);
  const [loading, setLoading] = useState(true);

  const active      = trucks.filter((t) => t.status === 'active' || t.status === 'available').length;
  const maintenance = trucks.filter((t) => t.status === 'maintenance').length;

  useEffect(() => {
    trucksApi.list()
      .then((res) => {
        const body = res.data as { success: boolean; data: Truck[] };
        if (body.success) setTrucks(body.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardShell title="الأسطول" description="إدارة المركبات والجداول الدورية للصيانة.">
      <div className="grid gap-5">
        <div className="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <Card className="glass p-6">
            <h2 className="text-xl font-semibold text-white">نظرة عامة على الأسطول</h2>
            <p className="mt-2 text-sm text-slate-400">تحكم في حالة المركبات وجدول الصيانة الذكي.</p>
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
