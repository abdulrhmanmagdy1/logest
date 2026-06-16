'use client';

import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Modal } from '@/components/ui/Modal';
import { shipmentsApi, usersApi, trucksApi } from '@/lib/api';
import { useAuthStore } from '@/store/useAuthStore';

interface Shipment {
  _id: string;
  trackingNumber: string;
  status: string;
  client?: { firstName?: string; lastName?: string; companyName?: string };
  driver?: { firstName?: string; lastName?: string };
}

const STATUS_LABELS: Record<string, string> = {
  pending: 'في الانتظار',
  assigned: 'مُسند',
  at_pickup: 'في نقطة الاستلام',
  picked_up: 'تم الاستلام',
  in_transit: 'قيد النقل',
  on_the_way: 'في الطريق',
  at_delivery: 'في نقطة التسليم',
  delivered: 'تم التسليم',
  completed: 'مكتمل',
  cancelled: 'ملغي',
  delayed: 'متأخر',
};

const STATUS_COLORS: Record<string, string> = {
  pending: 'bg-amber-500/15 text-amber-300',
  assigned: 'bg-amber-500/15 text-amber-300',
  at_pickup: 'bg-cyan-500/15 text-cyan-300',
  picked_up: 'bg-cyan-500/15 text-cyan-300',
  in_transit: 'bg-emerald-500/15 text-emerald-300',
  on_the_way: 'bg-emerald-500/15 text-emerald-300',
  at_delivery: 'bg-emerald-500/15 text-emerald-300',
  delivered: 'bg-slate-500/15 text-slate-300',
  completed: 'bg-slate-500/15 text-slate-300',
  cancelled: 'bg-rose-500/15 text-rose-300',
  delayed: 'bg-rose-500/15 text-rose-300',
};

interface UserOption { _id: string; firstName: string; lastName: string; companyName?: string; }
interface TruckOption { _id: string; plateNumber: string; make?: string; model?: string; }

function clientName(c?: Shipment['client']): string {
  if (!c) return '—';
  return c.companyName || [c.firstName, c.lastName].filter(Boolean).join(' ') || '—';
}

function driverName(d?: Shipment['driver']): string {
  if (!d) return '—';
  return [d.firstName, d.lastName].filter(Boolean).join(' ') || '—';
}

const BLANK_FORM = {
  cargoType: '', cargoDesc: '', cargoWeight: '',
  pickupCity: '', pickupStreet: '',
  deliveryCity: '', deliveryStreet: '',
  clientId: '', driverId: '', truckId: '',
};

export default function ShipmentsPage() {
  const user = useAuthStore((s) => s.user);

  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [loading, setLoading]     = useState(true);
  const [search, setSearch]       = useState('');
  const [showModal, setShowModal] = useState(false);
  const [form, setForm]           = useState(BLANK_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError]   = useState('');
  const [clients, setClients]     = useState<UserOption[]>([]);
  const [drivers, setDrivers]     = useState<UserOption[]>([]);
  const [trucks, setTrucks]       = useState<TruckOption[]>([]);

  function fetchShipments() {
    shipmentsApi.list()
      .then((res) => {
        const body = res.data as { success: boolean; data: Shipment[] };
        if (body.success) setShipments(body.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }

  useEffect(() => { fetchShipments(); }, []);

  function openModal() {
    setShowModal(true);
    setFormError('');
    setForm(BLANK_FORM);
    usersApi.list({ role: 'client' }).then((r) => {
      const b = r.data as { success: boolean; data: UserOption[] };
      if (b.success) setClients(b.data);
    }).catch(() => {});
    usersApi.list({ role: 'driver' }).then((r) => {
      const b = r.data as { success: boolean; data: UserOption[] };
      if (b.success) setDrivers(b.data);
    }).catch(() => {});
    trucksApi.list().then((r) => {
      const b = r.data as { success: boolean; data: TruckOption[] };
      if (b.success) setTrucks(b.data);
    }).catch(() => {});
  }

  function set(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  async function handleAdd(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);
    try {
      const payload: Record<string, unknown> = {
        cargo: {
          type: form.cargoType.trim(),
          description: form.cargoDesc.trim(),
          weight: { value: parseFloat(form.cargoWeight), unit: 'kg' },
        },
        pickup:   { address: { city: form.pickupCity.trim(),   street: form.pickupStreet.trim() } },
        delivery: { address: { city: form.deliveryCity.trim(), street: form.deliveryStreet.trim() } },
      };
      if (form.clientId)  payload.client = form.clientId;
      if (form.driverId)  payload.driver = form.driverId;
      if (form.truckId)   payload.truck  = form.truckId;

      await shipmentsApi.create(payload);
      setShowModal(false);
      setForm(BLANK_FORM);
      setLoading(true);
      fetchShipments();
    } catch (err: unknown) {
      const apiErr = err as { response?: { data?: { message?: string; errors?: { msg: string }[] } } };
      setFormError(
        apiErr?.response?.data?.errors?.[0]?.msg ??
        apiErr?.response?.data?.message ??
        'فشل إضافة الشحنة'
      );
    } finally {
      setSubmitting(false);
    }
  }

  const isAdmin = user?.role === 'admin' || user?.role === 'supervisor' || user?.role === 'employee';

  const filtered = search
    ? shipments.filter((s) =>
        s.trackingNumber.toLowerCase().includes(search.toLowerCase()) ||
        clientName(s.client).includes(search)
      )
    : shipments;

  return (
    <DashboardShell title="الشحنات" description="لوحة إدارة الشحنات وتقارير حالة الطلبات">

      <Modal title="إضافة شحنة جديدة" open={showModal} onClose={() => setShowModal(false)}>
        <form onSubmit={handleAdd} className="grid gap-4">
          <p className="text-sm text-slate-400 -mt-2">بيانات البضاعة</p>
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="نوع البضاعة" placeholder="غذائية / إلكترونيات / مبردة" required value={form.cargoType} onChange={(e) => set('cargoType', e.target.value)} />
            <Input label="الوزن (كجم)" type="number" placeholder="500" required value={form.cargoWeight} onChange={(e) => set('cargoWeight', e.target.value)} />
          </div>
          <Input label="وصف البضاعة" placeholder="وصف تفصيلي للبضاعة" required value={form.cargoDesc} onChange={(e) => set('cargoDesc', e.target.value)} />

          <p className="text-sm text-slate-400">عنوان الاستلام</p>
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="المدينة" placeholder="الرياض" required value={form.pickupCity} onChange={(e) => set('pickupCity', e.target.value)} />
            <Input label="الشارع" placeholder="شارع الملك فهد" required value={form.pickupStreet} onChange={(e) => set('pickupStreet', e.target.value)} />
          </div>

          <p className="text-sm text-slate-400">عنوان التسليم</p>
          <div className="grid gap-3 sm:grid-cols-2">
            <Input label="المدينة" placeholder="جدة" required value={form.deliveryCity} onChange={(e) => set('deliveryCity', e.target.value)} />
            <Input label="الشارع" placeholder="شارع التحلية" required value={form.deliveryStreet} onChange={(e) => set('deliveryStreet', e.target.value)} />
          </div>

          {isAdmin && (
            <>
              <div>
                <label className="mb-2 block text-sm text-slate-300">العميل</label>
                <select value={form.clientId} onChange={(e) => set('clientId', e.target.value)}
                  className="w-full rounded-3xl border border-white/10 bg-slate-900/85 px-4 py-3 text-white outline-none transition focus:border-cyan-400">
                  <option value="">— اختر العميل —</option>
                  {clients.map((c) => (
                    <option key={c._id} value={c._id}>
                      {c.companyName || `${c.firstName} ${c.lastName}`}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grid gap-3 sm:grid-cols-2">
                <div>
                  <label className="mb-2 block text-sm text-slate-300">السائق (اختياري)</label>
                  <select value={form.driverId} onChange={(e) => set('driverId', e.target.value)}
                    className="w-full rounded-3xl border border-white/10 bg-slate-900/85 px-4 py-3 text-white outline-none transition focus:border-cyan-400">
                    <option value="">— بدون سائق —</option>
                    {drivers.map((d) => (
                      <option key={d._id} value={d._id}>{d.firstName} {d.lastName}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="mb-2 block text-sm text-slate-300">الشاحنة (اختياري)</label>
                  <select value={form.truckId} onChange={(e) => set('truckId', e.target.value)}
                    className="w-full rounded-3xl border border-white/10 bg-slate-900/85 px-4 py-3 text-white outline-none transition focus:border-cyan-400">
                    <option value="">— بدون شاحنة —</option>
                    {trucks.map((t) => (
                      <option key={t._id} value={t._id}>{t.plateNumber} — {t.make} {t.model}</option>
                    ))}
                  </select>
                </div>
              </div>
            </>
          )}

          {formError && <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{formError}</p>}
          <div className="flex gap-3 pt-2">
            <Button type="submit" disabled={submitting} className="flex-1">
              {submitting ? 'جاري الإضافة...' : 'إضافة الشحنة'}
            </Button>
            <Button type="button" variant="secondary" onClick={() => setShowModal(false)}>إلغاء</Button>
          </div>
        </form>
      </Modal>

      <div className="grid gap-5">
        <Card className="glass p-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-xl font-semibold">جميع الشحنات</h2>
              <p className="text-sm text-slate-400">إدارة حالة كل شحنة في الوقت الحقيقي.</p>
            </div>
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
              <Input
                placeholder="بحث بالرقم أو العميل"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <Button variant="secondary" onClick={openModal}>+ إضافة شحنة</Button>
            </div>
          </div>
        </Card>

        <Card className="glass p-6 overflow-x-auto">
          {loading ? (
            <p className="py-10 text-center text-slate-400">جاري التحميل...</p>
          ) : filtered.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-slate-400">لا توجد شحنات بعد</p>
              <p className="mt-2 text-sm text-slate-600">ستظهر الشحنات هنا بعد إضافتها للنظام</p>
            </div>
          ) : (
            <table className="w-full min-w-[760px] border-collapse text-left text-sm text-slate-300">
              <thead>
                <tr className="border-b border-white/10 text-slate-400">
                  <th className="py-4 pr-6">رقم الشحنة</th>
                  <th className="py-4 pr-6">العميل</th>
                  <th className="py-4 pr-6">السائق</th>
                  <th className="py-4 pr-6">الحالة</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((item) => (
                  <tr key={item._id} className="border-b border-white/10 transition hover:bg-slate-900/70">
                    <td className="py-4 pr-6 font-semibold text-white">{item.trackingNumber}</td>
                    <td className="py-4 pr-6">{clientName(item.client)}</td>
                    <td className="py-4 pr-6">{driverName(item.driver)}</td>
                    <td className="py-4 pr-6">
                      <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${STATUS_COLORS[item.status] ?? 'bg-slate-500/15 text-slate-300'}`}>
                        {STATUS_LABELS[item.status] ?? item.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Card>
      </div>
    </DashboardShell>
  );
}
