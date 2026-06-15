'use client';

import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid } from 'recharts';
import { revenueData } from '@/data/dummy/charts';

export function RevenueChart() {
  return (
    <div className="rounded-[28px] border border-white/10 bg-slate-950/80 p-5 shadow-edham">
      <div className="mb-5 flex items-center justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">تحليلات الإيرادات</p>
          <h2 className="text-xl font-semibold text-white">أداء الإيرادات الشهري</h2>
        </div>
      </div>
      <div className="h-[260px]">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={revenueData} margin={{ top: 10, right: 0, left: -20, bottom: 0 }}>
            <defs>
              <linearGradient id="revenueGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#38bdf8" stopOpacity={0.8} />
                <stop offset="95%" stopColor="#38bdf8" stopOpacity={0.1} />
              </linearGradient>
            </defs>
            <CartesianGrid stroke="rgba(148,163,184,0.08)" vertical={false} />
            <XAxis dataKey="month" tickLine={false} axisLine={false} tick={{ fill: '#94a3b8' }} />
            <YAxis tickLine={false} axisLine={false} tick={{ fill: '#94a3b8' }} />
            <Tooltip contentStyle={{ background: '#0f172a', borderColor: 'rgba(148,163,184,0.16)' }} />
            <Area type="monotone" dataKey="revenue" stroke="#38bdf8" strokeWidth={3} fill="url(#revenueGradient)" />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
