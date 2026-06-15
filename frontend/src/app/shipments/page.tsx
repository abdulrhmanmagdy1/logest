import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

const shipments = [
  { id: 'SH-1024', client: 'شركة الخليج', status: 'In Transit', driver: 'أحمد سالم', eta: '14:45', temp: '7°C' },
  { id: 'SH-1098', client: 'العربية للتبريد', status: 'Pending', driver: 'محمد الراشد', eta: '18:00', temp: '5°C' },
  { id: 'SH-1154', client: 'دار الشحن', status: 'Delayed', driver: 'سلمان هاشم', eta: '20:30', temp: '12°C' },
];

export default function ShipmentsPage() {
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
              <Input placeholder="بحث بالرقم أو العميل" />
              <Button variant="secondary">تصفية متقدمة</Button>
            </div>
          </div>
        </Card>

        <Card className="glass p-6 overflow-x-auto">
          <table className="w-full min-w-[760px] border-collapse text-left text-sm text-slate-300">
            <thead>
              <tr className="border-b border-white/10 text-slate-400">
                <th className="py-4 pr-6">رقم الشحنة</th>
                <th className="py-4 pr-6">العميل</th>
                <th className="py-4 pr-6">السائق</th>
                <th className="py-4 pr-6">الحالة</th>
                <th className="py-4 pr-6">ETA</th>
                <th className="py-4 pr-6">درجة الحرارة</th>
              </tr>
            </thead>
            <tbody>
              {shipments.map((item) => (
                <tr key={item.id} className="border-b border-white/10 transition hover:bg-slate-900/70">
                  <td className="py-4 pr-6 font-semibold text-white">{item.id}</td>
                  <td className="py-4 pr-6">{item.client}</td>
                  <td className="py-4 pr-6">{item.driver}</td>
                  <td className="py-4 pr-6">
                    <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${item.status === 'Delayed' ? 'bg-rose-500/15 text-rose-300' : item.status === 'Pending' ? 'bg-amber-500/15 text-amber-300' : 'bg-emerald-500/15 text-emerald-300'}`}>
                      {item.status}
                    </span>
                  </td>
                  <td className="py-4 pr-6">{item.eta}</td>
                  <td className="py-4 pr-6">{item.temp}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      </div>
    </DashboardShell>
  );
}
