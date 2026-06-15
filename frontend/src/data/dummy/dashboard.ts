import type { KpiCard } from '@/types';

export const kpis: KpiCard[] = [
  {
    id: 'shipments',
    label: 'إجمالي الشحنات',
    value: '1,284',
    subtitle: 'الشحنات المسجلة خلال الشهر الحالي',
  },
  {
    id: 'active',
    label: 'الشحنات النشطة',
    value: '348',
    subtitle: 'الشحنات قيد التنفيذ الآن',
  },
  {
    id: 'delay',
    label: 'الشحنات المتأخرة',
    value: '12',
    subtitle: 'الشحنات التي تحتاج متابعة عاجلة',
  },
  {
    id: 'revenue',
    label: 'الإيرادات',
    value: '2.4M SAR',
    subtitle: 'الإيرادات المقدرة للشهر الحالي',
  },
];
