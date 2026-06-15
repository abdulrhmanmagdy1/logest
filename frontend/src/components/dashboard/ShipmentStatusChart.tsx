'use client';

import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid } from 'recharts';
import { shipmentStatusData } from '@/data/dummy/charts';

export function ShipmentStatusChart() {
  return (
    <div className="rounded-[28px] border border-white/10 bg-slate-950/80 p-5 shadow-edham">
      <div className="mb-5 flex items-center justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">معدل الشحنات</p>
          <h2 className="text-xl font-semibold text-white">حالة الشحنات الأسبوعية</h2>
        </div>
      </div>
      <div className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={shipmentStatusData} margin={{ top: 10, right: 0, left: -20, bottom: 0 }}>
            <CartesianGrid stroke="rgba(148,163,184,0.08)" vertical={false} />
            <XAxis dataKey="day" tickLine={false} axisLine={false} tick={{ fill: '#94a3b8' }} />
            <YAxis tickLine={false} axisLine={false} tick={{ fill: '#94a3b8' }} />
            <Tooltip contentStyle={{ background: '#0f172a', borderColor: 'rgba(148,163,184,0.16)' }} />
            <Line type="monotone" dataKey="delivered" stroke="#22c55e" strokeWidth={3} dot={false} />
            <Line type="monotone" dataKey="inTransit" stroke="#38bdf8" strokeWidth={3} dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
