import { type InputHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export function Input({ label, className = '', ...props }: InputProps) {
  return (
    <label className="block text-sm text-slate-200">
      {label && <span className="mb-2 block text-slate-300">{label}</span>}
      <input
        className={`w-full rounded-3xl border border-white/10 bg-slate-900/85 px-4 py-3 text-white outline-none transition duration-200 placeholder:text-slate-500 focus:border-cyan-400 focus:ring-2 focus:ring-cyan-400/10 ${className}`}
        {...props}
      />
    </label>
  );
}
