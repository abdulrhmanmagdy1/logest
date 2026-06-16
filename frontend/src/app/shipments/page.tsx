'use client';

import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { shipmentsApi } from '@/lib/api';

interface Shipment {
  _id: string;
  trackingNumber: string;
  status: string;
  client?: { firstName?: string; lastName?: string; companyName?: string };
  driver?: { firstName?: string; lastName?: string };
}

const STATUS_LABELS: Record<string, string> = {
  pending: 'في الانتظار',
  assigned: 'مُسند',
  at_pickup: 'في نقطة الاستلام',
  picked_up: 'تم الاستلام',
  in_transit: 'قيد النقل',
  on_the_way: 'في الطريق',
  at_delivery: 'في نقطة التسليم',
  delivered: 'تم التسليم',
  completed: 'مكتمل',
  cancelled: 'ملغي',
  delayed: 'متأخر',
};

const STATUS_COLORS: Record<string, string> = {
  pending: 'bg-amber-500/15 text-amber-300',
  assigned: 'bg-amber-500/15 text-amber-300',
  at_pickup: 'bg-cyan-500/15 text-cyan-300',
  picked_up: 'bg-cyan-500/15 text-cyan-300',
  in_transit: 'bg-emerald-500/15 text-emerald-300',
  on_the_way: 'bg-emerald-500/15 text-emerald-300',
  at_delivery: 'bg-emerald-500/15 text-emerald-300',
  delivered: 'bg-slate-500/15 text-slate-300',
  completed: 'bg-slate-500/15 text-slate-300',
  cancelled: 'bg-rose-500/15 text-rose-300',
  delayed: 'bg-rose-500/15 text-rose-300',
};

function clientName(c?: Shipment['client']): string {
  if (!c) return '—';
  return c.companyName || [c.firstName, c.lastName].filter(Boolean).join(' ') || '—';
}

function driverName(d?: Shipment['driver']): string {
  if (!d) return '—';
  return [d.firstName, d.lastName].filter(Boolean).join(' ') || '—';
}

export default function ShipmentsPage() {
  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [loading, setLoading]     = useState(true);
  const [search, setSearch]       = useState('');

  useEffect(() => {
    shipmentsApi.list()
      .then((res) => {
        const body = res.data as { success: boolean; data: Shipment[] };
        if (body.success) setShipments(body.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const filtered = search
    ? shipments.filter((s) =>
        s.trackingNumber.toLowerCase().includes(search.toLowerCase()) ||
        clientName(s.client).includes(search)
      )
    : shipments;

  return (
    <DashboardShell title="الشحنات" description="لوحة إدارة الشحنات وتقارير حالة الطلبات">
      <div className="grid gap-5">
        <Card className="glass p-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-semibold">جميع الشحنات</h2>
              <p className="text-sm text-slate-400">إدارة حالة كل شحنة في الوقت الحقيقي.</p>
            </div>
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
              <Input
                placeholder="بحث بالرقم أو العميل"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <Button variant="secondary">تصفية متقدمة</Button>
            </div>
          </div>
        </Card>

        <Card className="glass p-6 overflow-x-auto">
          {loading ? (
            <p className="py-10 text-center text-slate-400">جاري التحميل...</p>
          ) : filtered.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-slate-400">لا توجد شحنات بعد</p>
              <p className="mt-2 text-sm text-slate-600">ستظهر الشحنات هنا بعد إضافتها للنظام</p>
            </div>
          ) : (
            <table className="w-full min-w-[760px] border-collapse text-left text-sm text-slate-300">
              <thead>
                <tr className="border-b border-white/10 text-slate-400">
                  <th className="py-4 pr-6">رقم الشحنة</th>
                  <th className="py-4 pr-6">العميل</th>
                  <th className="py-4 pr-6">السائق</th>
                  <th className="py-4 pr-6">الحالة</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((item) => (
                  <tr key={item._id} className="border-b border-white/10 transition hover:bg-slate-900/70">
                    <td className="py-4 pr-6 font-semibold text-white">{item.trackingNumber}</td>
                    <td className="py-4 pr-6">{clientName(item.client)}</td>
                    <td className="py-4 pr-6">{driverName(item.driver)}</td>
                    <td className="py-4 pr-6">
                      <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${STATUS_COLORS[item.status] ?? 'bg-slate-500/15 text-slate-300'}`}>
                        {STATUS_LABELS[item.status] ?? item.status}
                      </span>
                    </td>
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
