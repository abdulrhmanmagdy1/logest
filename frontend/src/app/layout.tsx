import '../styles/globals.css';
import type { Metadata } from 'next';
import { AuthProvider } from '@/components/auth/AuthProvider';

export const metadata: Metadata = {
  title: 'Edham Logistics Platform',
  description: 'لوحة تحكم لوجستية ذكية لإدارة الشحنات والأسطول والتتبع المباشر.',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ar" dir="rtl" className="scroll-smooth">
      <body>
        <AuthProvider>{children}</AuthProvider>
      </body>
    </html>
  );
}
