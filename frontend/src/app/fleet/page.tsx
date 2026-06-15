import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';

const vehicles = [
  { plate: 'HJL-5221', model: 'Volvo FH', status: 'Active', nextService: '20 مايو', km: '178,000' },
  { plate: 'LKP-3300', model: 'Scania R450', status: 'Maintenance', nextService: '09 مايو', km: '244,500' },
  { plate: 'ZQR-8710', model: 'Mercedes Actros', status: 'Available', nextService: '16 يونيو', km: '98,300' },
];

export default function FleetPage() {
  return (
    <DashboardShell title="الأسطول" description="إدارة المركبات والجداول الدورية للصيانة.">
      <div className="grid gap-5">
        <div className="grid gap-4 lg:grid-cols-[1.2fr_0.8fr]">
          <Card className="glass p-6">
            <h2 className="text-xl font-semibold text-white">نظرة عامة على الأسطول</h2>
            <p className="mt-2 text-sm text-slate-400">تحكم في حالة المركبات وجدول الصيانة الذكي.</p>
            <div className="mt-6 grid gap-4 sm:grid-cols-3">
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">المركبات المتاحة</p>
                <p className="mt-3 text-3xl font-semibold">24</p>
              </div>
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">أعمال الصيانة القادمة</p>
                <p className="mt-3 text-3xl font-semibold">8</p>
              </div>
              <div className="rounded-[24px] bg-white/5 p-4 text-slate-200">
                <p className="text-sm text-slate-400">متوسط استهلاك الوقود</p>
                <p className="mt-3 text-3xl font-semibold">4.8 km/L</p>
              </div>
            </div>
          </Card>
          <Card className="glass p-6">
            <h3 className="text-lg font-semibold text-white">تنبيهات الصيانة</h3>
            <div className="mt-5 space-y-4 text-slate-300">
              <div className="rounded-3xl bg-slate-900/70 p-4">
                <p className="text-sm text-slate-400">HJL-5221</p>
                <p className="mt-2 text-sm">تغيير زيت بعد 450 كم</p>
              </div>
              <div className="rounded-3xl bg-slate-900/70 p-4">
                <p className="text-sm text-slate-400">LKP-3300</p>
                <p className="mt-2 text-sm">فحص نظام التبريد</p>
              </div>
            </div>
          </Card>
        </div>

        <Card className="glass p-6 overflow-x-auto">
          <table className="w-full min-w-[680px] border-collapse text-left text-sm text-slate-300">
            <thead>
              <tr className="border-b border-white/10 text-slate-400">
                <th className="py-4 pr-6">رقم اللوحة</th>
                <th className="py-4 pr-6">الموديل</th>
                <th className="py-4 pr-6">الحالة</th>
                <th className="py-4 pr-6">الخدمة القادمة</th>
                <th className="py-4 pr-6">المسافة المقطوعة</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.map((vehicle) => (
                <tr key={vehicle.plate} className="border-b border-white/10 transition hover:bg-slate-900/70">
                  <td className="py-4 pr-6 font-semibold text-white">{vehicle.plate}</td>
                  <td className="py-4 pr-6">{vehicle.model}</td>
                  <td className="py-4 pr-6">{vehicle.status}</td>
                  <td className="py-4 pr-6">{vehicle.nextService}</td>
                  <td className="py-4 pr-6">{vehicle.km}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      </div>
    </DashboardShell>
  );
}
