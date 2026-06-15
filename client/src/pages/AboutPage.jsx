/**
 * ============================================
 * ℹ️ About Page - نظام إدهام
 * Edham Logistics - About Page
 * ============================================
 */

import React from 'react';
import { Truck, Target, Users, Award, Shield, Globe } from 'lucide-react';

export default function AboutPage() {
  return (
    <div className="min-h-screen bg-gray-900">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-r from-blue-900 to-blue-700 py-20">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="text-5xl font-bold text-white mb-4">نظام إدهام للوجستيات</h1>
          <p className="text-xl text-blue-100 mb-8">
            شريكك الموثوق في حلول النقل والخدمات اللوجستية في المملكة العربية السعودية
          </p>
        </div>
      </div>

      {/* About Content */}
      <div className="max-w-7xl mx-auto px-6 py-16">
        {/* Mission */}
        <div className="mb-16">
          <h2 className="text-3xl font-bold text-white mb-6 text-center">رؤيتنا ورسالتنا</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="bg-gray-800 p-8 rounded-lg">
              <Target className="w-12 h-12 text-blue-500 mb-4" />
              <h3 className="text-xl font-bold text-white mb-3">رؤيتنا</h3>
              <p className="text-gray-300">
                أن نكون الخيار الأول والمفضل في حلول النقل والخدمات اللوجستية في المملكة العربية السعودية والمنطقة، من خلال تقديم خدمات متميزة تدعم نمو الأعمال وتلبي تطلعات عملائنا.
              </p>
            </div>
            <div className="bg-gray-800 p-8 rounded-lg">
              <Truck className="w-12 h-12 text-blue-500 mb-4" />
              <h3 className="text-xl font-bold text-white mb-3">رسالتنا</h3>
              <p className="text-gray-300">
                تقديم حلول لوجستية متكاملة ومبتكرة تتجاوز توقعات العملاء، مع الالتزام بأعلى معايير الجودة والسلامة، والمساهمة في تحقيق رؤية المملكة 2030.
              </p>
            </div>
          </div>
        </div>

        {/* Values */}
        <div className="mb-16">
          <h2 className="text-3xl font-bold text-white mb-6 text-center">قيمنا</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <ValueCard
              icon={<Shield className="w-8 h-8" />}
              title="السلامة أولاً"
              description="نضع سلامة الشحنات والسائقين في قمة أولوياتنا"
            />
            <ValueCard
              icon={<Users className="w-8 h-8" />}
              title="التميز في الخدمة"
              description="نسعى دائماً لتقديم أفضل تجربة لعملائنا"
            />
            <ValueCard
              icon={<Award className="w-8 h-8" />}
              title="الجودة والموثوقية"
              description "نلتزم بأعلى معايير الجودة في جميع خدماتنا"
            />
            <ValueCard
              icon={<Globe className="w-8 h-8" />}
              title="الابتكار المستمر"
              description="نستخدم أحدث التقنيات لتحسين خدماتنا"
            />
            <ValueCard
              icon={<Users className="w-8 h-8" />}
              title="الشراكة الاستراتيجية"
              description="نبني علاقات طويلة الأمد مع عملائنا"
            />
            <ValueCard
              icon={<Shield className="w-8 h-8" />}
              title="الشفافية"
              description="نحرص على الشفافية في جميع تعاملاتنا"
            />
          </div>
        </div>

        {/* Stats */}
        <div className="mb-16">
          <h2 className="text-3xl font-bold text-white mb-6 text-center">إنجازاتنا بالأرقام</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            <StatCard number="500+" label="شحنة منجزة" />
            <StatCard number="50+" label="شاحنة نشطة" />
            <StatCard number="100+" label="سائق محترف" />
            <StatCard number="25+" label="مدينة مغطاة" />
          </div>
        </div>

        {/* Why Choose Us */}
        <div className="mb-16">
          <h2 className="text-3xl font-bold text-white mb-6 text-center">لماذا تختار إدهام؟</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FeatureCard
              title="تتبع مباشر"
              description="تتبع شحناتك في الوقت الفعلي عبر نظام متطور"
            />
            <FeatureCard
              title="فريق محترف"
              description="سائقون وموظفون مدربون على أعلى مستوى"
            />
            <FeatureCard
              title="أسعار تنافسية"
              description="أسعار معقولة تناسب جميع الميزانيات"
            />
            <FeatureCard
              title="دعم على مدار الساعة"
              description="فريق دعم متاح للإجابة على استفساراتكم"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function ValueCard({ icon, title, description }) {
  return (
    <div className="bg-gray-800 p-6 rounded-lg text-center">
      <div className="text-blue-500 mb-4 flex justify-center">{icon}</div>
      <h3 className="text-lg font-bold text-white mb-2">{title}</h3>
      <p className="text-gray-400 text-sm">{description}</p>
    </div>
  );
}

function StatCard({ number, label }) {
  return (
    <div className="bg-blue-600 p-6 rounded-lg text-center">
      <p className="text-4xl font-bold text-white mb-2">{number}</p>
      <p className="text-blue-100">{label}</p>
    </div>
  );
}

function FeatureCard({ title, description }) {
  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <h3 className="text-lg font-bold text-white mb-2">{title}</h3>
      <p className="text-gray-400">{description}</p>
    </div>
  );
}
