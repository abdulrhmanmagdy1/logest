'use client';

import { useState, useEffect } from 'react';
import { Card } from '@/components/ui/Card';
import { DashboardShell } from '@/components/layout/DashboardShell';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { usersApi } from '@/lib/api';
import { useAuthStore } from '@/store/useAuthStore';
import type { AuthUser } from '@/store/useAuthStore';

function normalizePhone(raw: string): string {
  const d = raw.replace(/\s+/g, '');
  if (d.startsWith('00966')) return '0' + d.slice(5);
  if (d.startsWith('+966'))  return '0' + d.slice(4);
  if (d.startsWith('966'))   return '0' + d.slice(3);
  return d;
}

export default function SettingsPage() {
  const user    = useAuthStore((s) => s.user);
  const setUser = useAuthStore((s) => s.setUser);

  function buildInitial(u: AuthUser | null) {
    return {
      firstName:   u?.firstName ?? '',
      lastName:    u?.lastName  ?? '',
      companyName: u?.companyName ?? '',
      phone:       u?.phone ?? '',
    };
  }

  const [form, setForm]       = useState(() => buildInitial(user));
  const [saving, setSaving]   = useState(false);
  const [saved, setSaved]     = useState(false);
  const [saveError, setSaveError] = useState('');

  useEffect(() => { setForm(buildInitial(user)); }, [user]);

  function set(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
    setSaved(false);
    setSaveError('');
  }

  function handleCancel() {
    setForm(buildInitial(user));
    setSaved(false);
    setSaveError('');
  }

  async function handleSave(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setSaveError('');
    setSaving(true);
    try {
      const id = user?._id ?? user?.id;
      if (!id) { setSaveError('لم يتم التعرف على المستخدم'); setSaving(false); return; }

      const payload: Record<string, unknown> = {};
      if (form.firstName.trim())   payload.firstName   = form.firstName.trim();
      if (form.lastName.trim())    payload.lastName    = form.lastName.trim();
      if (form.companyName.trim()) payload.companyName = form.companyName.trim();
      if (form.phone.trim())       payload.phone       = normalizePhone(form.phone);

      const res = await usersApi.update(id, payload);
      const body = res.data as { success: boolean; data: AuthUser };
      if (body.success && body.data) setUser(body.data);
      setSaved(true);
    } catch (err: unknown) {
      const apiErr = err as { response?: { data?: { message?: string; errors?: { msg: string }[] } } };
      setSaveError(
        apiErr?.response?.data?.errors?.[0]?.msg ??
        apiErr?.response?.data?.message ??
        'فشل حفظ الإعدادات'
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <DashboardShell title="الإعدادات" description="ضبط الشركة والصلاحيات وسياسات الأمان.">
      <div className="grid gap-5 lg:grid-cols-[1.2fr_0.8fr]">
        <Card className="glass p-6">
          <h2 className="text-xl font-semibold text-white">إعدادات الحساب</h2>
          <p className="mt-1 text-sm text-slate-500">البريد الإلكتروني: {user?.email ?? '—'}</p>
          <form onSubmit={handleSave} className="mt-6 grid gap-4">
            <div className="grid gap-3 sm:grid-cols-2">
              <Input
                label="الاسم الأول"
                placeholder="محمد"
                value={form.firstName}
                onChange={(e) => set('firstName', e.target.value)}
              />
              <Input
                label="اسم العائلة"
                placeholder="العمري"
                value={form.lastName}
                onChange={(e) => set('lastName', e.target.value)}
              />
            </div>
            <Input
              label="اسم الشركة"
              placeholder="شركة ادهام للنقل"
              value={form.companyName}
              onChange={(e) => set('companyName', e.target.value)}
            />
            <Input
              label="رقم الهاتف"
              placeholder="00966 5X XXX XXXX"
              type="tel"
              value={form.phone}
              onChange={(e) => set('phone', e.target.value)}
            />
            {saveError && (
              <p className="rounded-3xl bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{saveError}</p>
            )}
            {saved && (
              <p className="rounded-3xl bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">تم حفظ الإعدادات بنجاح</p>
            )}
            <div className="flex items-center gap-3">
              <Button type="submit" disabled={saving}>{saving ? 'جاري الحفظ...' : 'حفظ التغييرات'}</Button>
              <Button type="button" variant="secondary" onClick={handleCancel}>إلغاء</Button>
            </div>
          </form>
        </Card>
        <Card className="glass p-6">
          <h2 className="text-xl font-semibold text-white">صلاحيات المستخدمين</h2>
          <p className="mt-3 text-sm text-slate-400">قم بإدارة الأدوار والمستويات وصولًا إلى المشرفين والسائقين والعملاء.</p>
          <div className="mt-5 space-y-4">
            <div className="rounded-3xl bg-slate-900/70 p-4 text-slate-200">
              <h3 className="font-semibold">Admin</h3>
              <p className="mt-2 text-sm text-slate-400">صلاحيات كاملة على النظام.</p>
            </div>
            <div className="rounded-3xl bg-slate-900/70 p-4 text-slate-200">
              <h3 className="font-semibold">Operations Manager</h3>
              <p className="mt-2 text-sm text-slate-400">إدارة الشحنات والأسطول.</p>
            </div>
          </div>
        </Card>
      </div>
    </DashboardShell>
  );
}
