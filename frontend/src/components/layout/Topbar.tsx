'use client';

import { Bell, Search, SunMoon, LogOut } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/useAuthStore';

export function Topbar() {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  const handleLogout = () => {
    logout();
    localStorage.removeItem('edham_token');
    localStorage.removeItem('edham_user');
    router.push('/login');
  };

  return (
    <header className="flex flex-col gap-4 rounded-[32px] border border-white/10 bg-slate-950/85 p-5 shadow-edham md:flex-row md:items-center md:justify-between">
      <div className="flex flex-1 items-center gap-3 rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3">
        <Search size={18} className="text-slate-400" />
        <input
          type="search"
          placeholder="ابحث في الشحنات، السائقين، العملاء..."
          className="w-full bg-transparent text-sm text-slate-200 outline-none placeholder:text-slate-500"
        />
      </div>
      <div className="flex items-center gap-3 text-slate-200">
        <button className="inline-flex h-12 w-12 items-center justify-center rounded-3xl border border-white/10 bg-slate-900/80 text-slate-200 transition hover:border-cyan-300/20">
          <SunMoon size={18} />
        </button>
        <button className="inline-flex h-12 w-12 items-center justify-center rounded-3xl border border-white/10 bg-slate-900/80 text-slate-200 transition hover:border-cyan-300/20">
          <Bell size={18} />
        </button>
        <button
          type="button"
          onClick={handleLogout}
          className="inline-flex h-12 w-12 items-center justify-center rounded-3xl border border-white/10 bg-slate-900/80 text-slate-200 transition hover:border-rose-300/20"
        >
          <LogOut size={18} />
        </button>
        <div className="hidden items-center gap-3 rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 md:flex">
          <span className="h-10 w-10 rounded-full bg-cyan-400/15 text-cyan-300 grid place-items-center">{user?.name?.charAt(0).toUpperCase() ?? 'A'}</span>
          <div>
            <p className="text-sm text-slate-200">{user?.name ?? 'مدير النظام'}</p>
            <p className="text-xs text-slate-500">{user?.role ?? 'Admin'}</p>
          </div>
        </div>
      </div>
    </header>
  );
}
