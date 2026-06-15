import { type ReactNode } from 'react';

interface CardProps {
  children: ReactNode;
  className?: string;
}

export function Card({ children, className = '' }: CardProps) {
  return (
    <div className={`rounded-[32px] border border-white/10 bg-slate-950/70 shadow-edham ${className}`}>
      {children}
    </div>
  );
}
