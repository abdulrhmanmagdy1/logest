/**
 * ============================================
 * 📞 Contact Page - نظام إدهام
 * Edham Logistics - Contact Page
 * ============================================
 */

import React, { useState } from 'react';
import { Mail, Phone, MapPin, Send, Clock, MessageSquare } from 'lucide-react';
import Button from '../components/UI/Button';
import Input from '../components/UI/Input';

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  });
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Simulate API call
    setTimeout(() => {
      setLoading(false);
      setSubmitted(true);
      setFormData({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
      });
    }, 1000);
  };

  if (submitted) {
    return (
      <div className="min-h-screen bg-gray-900 flex items-center justify-center p-6">
        <div className="bg-gray-800 p-8 rounded-lg text-center max-w-md">
          <div className="w-16 h-16 bg-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
            <Send className="w-8 h-8 text-white" />
          </div>
          <h2 className="text-2xl font-bold text-white mb-2">تم الإرسال بنجاح</h2>
          <p className="text-gray-400 mb-6">شكراً لتواصلك معنا، سنرد عليك في أقرب وقت ممكن</p>
          <Button onClick={() => setSubmitted(false)}>
            إرسال رسالة أخرى
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-r from-blue-900 to-blue-700 py-20">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <h1 className="text-5xl font-bold text-white mb-4">تواصل معنا</h1>
          <p className="text-xl text-blue-100 mb-8">
            نحن هنا للإجابة على استفساراتكم ومساعدتكم
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-6 py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Contact Form */}
          <div className="bg-gray-800 p-8 rounded-lg">
            <h2 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
              <MessageSquare className="w-6 h-6" />
              أرسل لنا رسالة
            </h2>

            <form onSubmit={handleSubmit}>
              <Input
                label="الاسم"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
              <Input
                label="البريد الإلكتروني"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
              <Input
                label="رقم الهاتف"
                name="phone"
                type="tel"
                value={formData.phone}
                onChange={handleChange}
              />
              <Input
                label="الموضوع"
                name="subject"
                value={formData.subject}
                onChange={handleChange}
                required
              />
              <div className="mb-4">
                <label className="block text-gray-300 mb-2">الرسالة</label>
                <textarea
                  name="message"
                  value={formData.message}
                  onChange={handleChange}
                  rows="5"
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none resize-none"
                  placeholder="اكتب رسالتك هنا..."
                  required
                />
              </div>

              <Button
                type="submit"
                loading={loading}
                fullWidth
                icon={<Send className="w-4 h-4" />}
              >
                إرسال الرسالة
              </Button>
            </form>
          </div>

          {/* Contact Info */}
          <div>
            <h2 className="text-2xl font-bold text-white mb-6">معلومات الاتصال</h2>

            <div className="space-y-6 mb-8">
              <ContactItem
                icon={<Phone className="w-6 h-6" />}
                title="الهاتف"
                value="+966 50 123 4567"
              />
              <ContactItem
                icon={<Mail className="w-6 h-6" />}
                title="البريد الإلكتروني"
                value="info@edham.sa"
              />
              <ContactItem
                icon={<MapPin className="w-6 h-6" />}
                title="العنوان"
                value="الرياض، المملكة العربية السعودية"
              />
              <ContactItem
                icon={<Clock className="w-6 h-6" />}
                title="ساعات العمل"
                value="الأحد - الخميس: 8:00 ص - 6:00 م"
              />
            </div>

            {/* Map Placeholder */}
            <div className="bg-gray-800 p-6 rounded-lg">
              <h3 className="text-lg font-bold text-white mb-4">موقعنا</h3>
              <div className="bg-gray-700 h-64 rounded flex items-center justify-center">
                <div className="text-center">
                  <MapPin className="w-12 h-12 text-blue-500 mx-auto mb-2" />
                  <p className="text-gray-400">الرياض، المملكة العربية السعودية</p>
                </div>
              </div>
            </div>

            {/* Social Links */}
            <div className="mt-6 bg-gray-800 p-6 rounded-lg">
              <h3 className="text-lg font-bold text-white mb-4">تابعنا</h3>
              <div className="flex gap-4">
                <SocialLink name="تويتر" />
                <SocialLink name="فيسبوك" />
                <SocialLink name="لينكد إن" />
                <SocialLink name="انستغرام" />
              </div>
            </div>
          </div>
        </div>

        {/* FAQ Section */}
        <div className="mt-16">
          <h2 className="text-3xl font-bold text-white mb-6 text-center">الأسئلة الشائعة</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <FAQItem
              question="كيف يمكنني تتبع شحنتي؟"
              answer="يمكنك تتبع شحنتك من خلال تسجيل الدخول إلى حسابك والذهاب إلى صفحة التتبع، أو استخدام رقم الشحنة في صفحة التتبع العامة."
            />
            <FAQItem
              question="ما هي مناطق التغطية؟"
              answer="نغطي جميع المدن الرئيسية في المملكة العربية السعودية بما في ذلك الرياض، جدة، مكة، المدينة، الدمام، وغيرها."
            />
            <FAQItem
              question="كم تستغرق عملية التوصيل؟"
              answer="يعتمد ذلك على المسافة والوجهة، عادة ما تتراوح بين 1-3 أيام للمدن الرئيسية و3-5 أيام للمدن النائية."
            />
            <FAQItem
              question="هل تقدمون خدمات الشحن المبرد؟"
              answer="نعم، لدينا أسطول من الشاحنات المبردة لنقل البضائع التي تتطلب درجات حرارة محددة."
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function ContactItem({ icon, title, value }) {
  return (
    <div className="flex items-start gap-4">
      <div className="text-blue-500">{icon}</div>
      <div>
        <p className="text-gray-400 text-sm">{title}</p>
        <p className="text-white font-semibold">{value}</p>
      </div>
    </div>
  );
}

function SocialLink({ name }) {
  return (
    <button className="bg-gray-700 hover:bg-blue-600 text-white px-4 py-2 rounded transition">
      {name}
    </button>
  );
}

function FAQItem({ question, answer }) {
  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <h3 className="text-lg font-bold text-white mb-2">{question}</h3>
      <p className="text-gray-400">{answer}</p>
    </div>
  );
}
