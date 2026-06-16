'use client';

export function ShipmentStatusChart() {
  return (
    <div className="rounded-[28px] border border-white/10 bg-slate-950/80 p-5 shadow-edham">
      <div className="mb-5 flex items-center justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">معدل الشحنات</p>
          <h2 className="text-xl font-semibold text-white">حالة الشحنات الأسبوعية</h2>
        </div>
      </div>
      <div className="flex h-[320px] items-center justify-center rounded-[20px] border border-dashed border-white/10 bg-slate-900/50">
        <div className="text-center">
          <p className="text-slate-400">لا توجد شحنات بعد</p>
          <p className="mt-2 text-sm text-slate-600">ستظهر الرسم البياني هنا عند إضافة شحنات للنظام</p>
        </div>
      </div>
    </div>
  );
}
