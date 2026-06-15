'use client';

import { type ReactNode } from 'react';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';
import { RequireAuth } from '@/components/auth/RequireAuth';

interface DashboardShellProps {
  children: ReactNode;
  title?: string;
  description?: string;
}

export function DashboardShell({ children, title, description }: DashboardShellProps) {
  return (
    <RequireAuth>
      <div className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(14,165,233,0.1),transparent_25%),radial-gradient(circle_at_80%_10%,rgba(59,130,246,0.08),transparent_24%),#050816] text-white">
        <div className="mx-auto flex min-h-screen max-w-[1800px]">
          <Sidebar />
          <div className="flex-1 p-6 lg:p-8">
            <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
              <div>
                {title && <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">{title}</p>}
                {description && <h1 className="text-3xl font-semibold text-white">{description}</h1>}
              </div>
            </div>
            <Topbar />
            <div className="mt-6">{children}</div>
          </div>
        </div>
      </div>
    </RequireAuth>
  );
}
