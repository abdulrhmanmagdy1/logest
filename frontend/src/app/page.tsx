import { DashboardShell } from '@/components/layout/DashboardShell';
import { DashboardSummary } from '@/components/dashboard/DashboardSummary';
import { LiveMapWidget } from '@/components/dashboard/LiveMapWidget';
import { RevenueChart } from '@/components/dashboard/RevenueChart';
import { ShipmentStatusChart } from '@/components/dashboard/ShipmentStatusChart';
import { kpis } from '@/data/dummy/dashboard';

export default function HomePage() {
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
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">تنبيه</p>
            <h3 className="mt-3 text-xl font-semibold text-white">درجة حرارة غير مستقرة</h3>
            <p className="mt-3 text-slate-400">تم اكتشاف ارتفاع طفيف في الشحنة رقم SH-1134، يرجى إرسال إشعار للسائق.</p>
          </div>
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">الخريطة الحية</p>
            <h3 className="mt-3 text-xl font-semibold text-white">متابعة 12 مركبة</h3>
            <p className="mt-3 text-slate-400">جميع المركبات ضمن الشبكة الآن، التحديث كل 10 ثواني.</p>
          </div>
          <div className="rounded-[32px] border border-white/10 bg-slate-950/75 p-6 shadow-edham">
            <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">تحذير الصيانة</p>
            <h3 className="mt-3 text-xl font-semibold text-white">3 مركبات تحتاج صيانة خلال 7 أيام</h3>
            <p className="mt-3 text-slate-400">قم بجدولة الفحص قبل أن تؤثر الأعطال على الجدول.</p>
          </div>
        </section>
      </div>
    </DashboardShell>
  );
}
