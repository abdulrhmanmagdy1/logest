'use client';

import { useEffect, useState } from 'react';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { DashboardSummary } from '@/components/dashboard/DashboardSummary';
import { LiveMapWidget } from '@/components/dashboard/LiveMapWidget';
import { RevenueChart } from '@/components/dashboard/RevenueChart';
import { ShipmentStatusChart } from '@/components/dashboard/ShipmentStatusChart';
import { dashboardApi } from '@/lib/api';
import type { KpiCard } from '@/types';

interface DashboardStats {
  totalShipments: number;
  activeShipments: number;
  totalTrucks: number;
  activeTrucks: number;
  totalDrivers: number;
  monthlyShipments: number;
  monthlyRevenue: number;
}

function formatRevenue(amount: number): string {
  if (amount >= 1_000_000) return (amount / 1_000_000).toFixed(1) + 'M ر.س';
  if (amount >= 1_000) return Math.round(amount / 1_000) + 'K ر.س';
  return amount.toLocaleString('ar-SA') + ' ر.س';
}

function statsToKpis(s: DashboardStats): KpiCard[] {
  return [
    { id: 'shipments', label: 'إجمالي الشحنات',   value: s.totalShipments.toLocaleString('ar-SA'), subtitle: `${s.monthlyShipments} شحنة هذا الشهر` },
    { id: 'active',    label: 'الشحنات النشطة',    value: s.activeShipments.toLocaleString('ar-SA'), subtitle: 'الشحنات قيد التنفيذ الآن' },
    { id: 'delay',     label: 'الشاحنات النشطة',   value: s.activeTrucks.toLocaleString('ar-SA'),    subtitle: `من إجمالي ${s.totalTrucks} شاحنة` },
    { id: 'revenue',   label: 'إيرادات الشهر',     value: formatRevenue(s.monthlyRevenue),            subtitle: 'إجمالي الفواتير المسددة هذا الشهر' },
  ];
}

const loadingKpis: KpiCard[] = [
  { id: 'shipments', label: 'إجمالي الشحنات',  value: '—', subtitle: 'جاري التحميل...' },
  { id: 'active',    label: 'الشحنات النشطة',   value: '—', subtitle: 'جاري التحميل...' },
  { id: 'delay',     label: 'الشاحنات النشطة',  value: '—', subtitle: 'جاري التحميل...' },
  { id: 'revenue',   label: 'إيرادات الشهر',    value: '—', subtitle: 'جاري التحميل...' },
];

export default function HomePage() {
  const [kpis, setKpis] = useState<KpiCard[]>(loadingKpis);

  useEffect(() => {
    dashboardApi.stats()
      .then((res) => {
        const body = res.data as { success: boolean; data: DashboardStats };
        if (body.success) setKpis(statsToKpis(body.data));
      })
      .catch(() => {/* silently keep loading state */});
  }, []);

  return (
    <DashboardShell title="لوحة القيادة" description="مركز القيادة الذكي لإدارة الشحنات والأسطول.">
      <div className="grid gap-6">
        <section className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">نظرة عامة</p>
                <h2 className="text-2xl font-semibold text-white">أحدث مؤشرات الأداء</h2>
              </div>
              <button className="rounded-full bg-cyan-400 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300">
                مشاهدة المزيد
              </button>
            </div>
            <div className="mt-6">
              <DashboardSummary cards={kpis} />
            </div>
          </div>
          <LiveMapWidget />
        </section>

        <section className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
          <ShipmentStatusChart />
          <RevenueChart />
        </section>

        <section className="grid gap-6 xl:grid-cols-3">
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">التنبيهات</p>
            <h3 className="mt-3 text-xl font-semibold text-white">لا توجد تنبيهات</h3>
            <p className="mt-3 text-slate-400">ستظهر التنبيهات هنا عند وجود شحنات تحتاج متابعة عاجلة.</p>
          </div>
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">الخريطة الحية</p>
            <h3 className="mt-3 text-xl font-semibold text-white">لا توجد مركبات نشطة</h3>
            <p className="mt-3 text-slate-400">ستظهر مواقع الشاحنات هنا عند بدء تسجيل الشحنات.</p>
          </div>
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">الصيانة</p>
            <h3 className="mt-3 text-xl font-semibold text-white">لا توجد تحذيرات صيانة</h3>
            <p className="mt-3 text-slate-400">ستظهر تحذيرات الصيانة هنا عند اقتراب مواعيد صيانة الشاحنات.</p>
          </div>
        </section>
      </div>
    </DashboardShell>
  );
}
