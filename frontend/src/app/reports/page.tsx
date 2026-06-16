'use client';

import { useRouter } from 'next/navigation';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';

const reports = [
  {
    title: 'تقرير الإيرادات',
    subtitle: 'ملف PDF جاهز للتصدير',
    status: 'جاهز',
    href: '/shipments',
  },
  {
    title: 'كفاءة الأسطول',
    subtitle: 'تقرير حالة المركبات والاستهلاك',
    status: 'قيد المعالجة',
    href: '/fleet',
  },
  {
    title: 'أداء السائقين',
    subtitle: 'تحليل تقييم الرحلات والمخالفات',
    status: 'جاهز',
    href: '/drivers',
  },
];

export default function ReportsPage() {
  const router = useRouter();

  return (
    <DashboardShell title="التقارير" description="مركز التقارير والتحليلات الذكية.">
      <div className="grid gap-5">
        {reports.map((report) => (
          <Card key={report.title} className="glass p-6">
            <div className="flex items-center justify-between gap-4">
              <div>
                <h3 className="text-xl font-semibold text-white">{report.title}</h3>
                <p className="mt-2 text-slate-400">{report.subtitle}</p>
              </div>
              <span className={`rounded-full px-3 py-2 text-sm ${report.status === 'جاهز' ? 'bg-emerald-500/15 text-emerald-200' : 'bg-amber-500/15 text-amber-200'}`}>
                {report.status}
              </span>
            </div>
            <div className="mt-5 flex flex-wrap gap-3">
              <button
                onClick={() => window.print()}
                className="rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300"
              >
                تنزيل PDF
              </button>
              <button
                onClick={() => router.push(report.href)}
                className="rounded-full border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-200 transition hover:bg-white/10"
              >
                عرض
              </button>
            </div>
          </Card>
        ))}
      </div>
    </DashboardShell>
  );
}
