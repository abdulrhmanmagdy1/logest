export function LiveMapWidget() {
  return (
    <div className="min-h-[320px] rounded-[28px] border border-white/10 bg-slate-950/80 p-5">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-cyan-300/80">خريطة مباشرة</p>
          <h2 className="mt-2 text-xl font-semibold text-white">مواقع الأسطول</h2>
        </div>
        <span className="rounded-full bg-white/5 px-3 py-1 text-sm text-slate-200">Real-time</span>
      </div>
      <div className="mt-6 h-[260px] rounded-[28px] border border-white/10 bg-slate-900/70 p-4 text-slate-400">
        <div className="h-full rounded-[24px] bg-gradient-to-br from-slate-900 via-slate-950 to-slate-900 p-5">
          <p className="text-sm text-slate-500">خريطة افتراضية مؤقتة</p>
          <div className="mt-8 flex h-full items-center justify-center rounded-[24px] border border-dashed border-white/10 bg-slate-950/80 text-center text-slate-500">
            سيتم استبدال هذه المنطقة بخريطة Google Maps أو Mapbox عند التكامل.
          </div>
        </div>
      </div>
    </div>
  );
}
