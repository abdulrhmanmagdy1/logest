import { ArrowUpRight, Clock3, ShieldCheck, Truck } from 'lucide-react';
import { type KpiCard } from '@/types';

interface DashboardSummaryProps {
  cards: KpiCard[];
}

const icons = {
  shipments: Truck,
  revenue: ArrowUpRight,
  active: ShieldCheck,
  delay: Clock3,
};

export function DashboardSummary({ cards }: DashboardSummaryProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-2">
      {cards.map((card) => {
        const Icon = icons[card.id] ?? Truck;
        return (
          <div key={card.id} className="rounded-[28px] border border-white/10 bg-white/5 p-5 shadow-sm transition hover:border-cyan-300/20">
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="text-sm text-slate-400">{card.label}</p>
                <p className="mt-3 text-3xl font-semibold text-white">{card.value}</p>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-3xl bg-cyan-400/15 text-cyan-300">
                <Icon size={22} />
              </div>
            </div>
            <p className="mt-3 text-sm text-slate-500">{card.subtitle}</p>
          </div>
        );
      })}
    </div>
  );
}
