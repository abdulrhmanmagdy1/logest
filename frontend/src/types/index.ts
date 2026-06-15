export interface KpiCard {
  id: 'shipments' | 'active' | 'delay' | 'revenue';
  label: string;
  value: string;
  subtitle: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}
