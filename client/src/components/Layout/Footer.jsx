/**
 * ============================================
 * 🦶 Footer Component - نظام إدهام
 * Edham Logistics - Footer
 * ============================================
 */

import React from 'react';
import { Facebook, Twitter, Linkedin, Instagram, Mail, Phone, MapPin } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-gray-800 text-white py-8">
      <div className="max-w-7xl mx-auto px-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Company Info */}
          <div>
            <h3 className="text-xl font-bold mb-4">نظام إدهام</h3>
            <p className="text-gray-400 text-sm mb-4">
              شريكك الموثوق في حلول النقل والخدمات اللوجستية في المملكة العربية السعودية
            </p>
            <div className="flex gap-2">
              <SocialIcon icon={<Facebook className="w-5 h-5" />} />
              <SocialIcon icon={<Twitter className="w-5 h-5" />} />
              <SocialIcon icon={<Linkedin className="w-5 h-5" />} />
              <SocialIcon icon={<Instagram className="w-5 h-5" />} />
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="font-semibold mb-4">روابط سريعة</h4>
            <ul className="space-y-2 text-gray-400 text-sm">
              <li><a href="/about" className="hover:text-white transition">من نحن</a></li>
              <li><a href="/contact" className="hover:text-white transition">اتصل بنا</a></li>
              <li><a href="/services" className="hover:text-white transition">خدماتنا</a></li>
              <li><a href="/privacy" className="hover:text-white transition">سياسة الخصوصية</a></li>
            </ul>
          </div>

          {/* Services */}
          <div>
            <h4 className="font-semibold mb-4">خدماتنا</h4>
            <ul className="space-y-2 text-gray-400 text-sm">
              <li>نقل البضائع</li>
              <li>الشحن المبرد</li>
              <li>التخزين</li>
              <li>التتبع المباشر</li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h4 className="font-semibold mb-4">تواصل معنا</h4>
            <ul className="space-y-2 text-gray-400 text-sm">
              <li className="flex items-center gap-2">
                <Phone className="w-4 h-4" />
                +966 50 123 4567
              </li>
              <li className="flex items-center gap-2">
                <Mail className="w-4 h-4" />
                info@edham.sa
              </li>
              <li className="flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                الرياض، المملكة العربية السعودية
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-gray-700 mt-8 pt-6 text-center text-gray-400 text-sm">
          <p>© {new Date().getFullYear()} نظام إدهام للوجستيات. جميع الحقوق محفوظة.</p>
        </div>
      </div>
    </footer>
  );
}

function SocialIcon({ icon }) {
  return (
    <button className="bg-gray-700 hover:bg-blue-600 p-2 rounded transition">
      {icon}
    </button>
  );
}
