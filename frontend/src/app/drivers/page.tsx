import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';

const drivers = [
  { name: 'أحمد سالم', status: 'On Route', rating: 4.9, trips: 52 },
  { name: 'محمد الراشد', status: 'Available', rating: 4.6, trips: 40 },
  { name: 'سلمان هاشم', status: 'Delayed', rating: 4.3, trips: 37 },
];

export default function DriversPage() {
  return (
    <DashboardShell title="السائقين" description="مراقبة الأداء والمهام الحالية.">
      <div className="grid gap-5">
        <Card className="glass p-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">فريق السائقين</h2>
              <p className="text-sm text-slate-400">عرض سريع لحالة السائقين ومهامهم الحالية.</p>
            </div>
            <button className="rounded-full bg-cyan-400 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300">
              إضافة سائق جديد
            </button>
          </div>
        </Card>

        <Card className="glass p-6 overflow-x-auto">
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
                <tr key={driver.name} className="border-b border-white/10 transition hover:bg-slate-900/70">
                  <td className="py-4 pr-6 font-semibold text-white">{driver.name}</td>
                  <td className="py-4 pr-6">{driver.status}</td>
                  <td className="py-4 pr-6">{driver.rating}</td>
                  <td className="py-4 pr-6">{driver.trips}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      </div>
    </DashboardShell>
  );
}
