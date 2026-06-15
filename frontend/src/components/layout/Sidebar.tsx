import Link from 'next/link';
import { Activity, Home, PackageOpen, Settings, Truck, Users } from 'lucide-react';

const navItems = [
  { label: 'لوحة القيادة', href: '/', icon: Home },
  { label: 'الشحنات', href: '/shipments', icon: PackageOpen },
  { label: 'الأسطول', href: '/fleet', icon: Truck },
  { label: 'السائقين', href: '/drivers', icon: Users },
  { label: 'التقارير', href: '/reports', icon: Activity },
  { label: 'الإعدادات', href: '/settings', icon: Settings },
];

export function Sidebar() {
  return (
    <aside className="hidden h-full w-[280px] flex-none flex-col gap-8 border-r border-white/10 bg-slate-950/90 p-6 xl:flex">
      <div className="space-y-3">
        <div className="rounded-3xl bg-white/5 p-4 shadow-edham">
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">Edham</p>
          <h2 className="text-xl font-semibold">Logistics HQ</h2>
        </div>
      </div>
      <nav className="space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <Link
              key={item.href}
              href={item.href}
              className="flex items-center gap-3 rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-sm text-slate-200 transition hover:border-cyan-400/20 hover:bg-slate-900"
            >
              <Icon size={18} className="text-cyan-300" />
              {item.label}
            </Link>
          );
        })}
      </nav>
      <div className="mt-auto rounded-3xl border border-white/10 bg-slate-900/70 p-5 text-sm text-slate-400">
        <p className="text-slate-200">نظام إدارة لوجستي متكامل</p>
        <p className="mt-3 text-slate-400">تابع موقع الأسطول، حالة الشحنات، والأداء المالي من مكان واحد.</p>
      </div>
    </aside>
  );
}
