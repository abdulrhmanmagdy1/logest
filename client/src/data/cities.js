// قائمة المدن السعودية للنظام
export const SAUDI_CITIES = [
  { id: "riyadh", name: "الرياض", nameEn: "Riyadh", region: "منطقة الرياض" },
  { id: "jeddah", name: "جدة", nameEn: "Jeddah", region: "منطقة مكة المكرمة" },
  { id: "makkah", name: "مكة المكرمة", nameEn: "Makkah", region: "منطقة مكة المكرمة" },
  { id: "madinah", name: "المدينة المنورة", nameEn: "Madinah", region: "منطقة المدينة المنورة" },
  { id: "dammam", name: "الدمام", nameEn: "Dammam", region: "المنطقة الشرقية" },
  { id: "khobar", name: "الخبر", nameEn: "Khobar", region: "المنطقة الشرقية" },
  { id: "tabuk", name: "تبوك", nameEn: "Tabuk", region: "منطقة تبوك" },
  { id: "taif", name: "الطائف", nameEn: "Taif", region: "منطقة مكة المكرمة" },
  { id: "abha", name: "أبها", nameEn: "Abha", region: "منطقة عسير" },
  { id: "hail", name: "حائل", nameEn: "Hail", region: "منطقة حائل" },
  { id: "buraidah", name: "بريدة", nameEn: "Buraidah", region: "منطقة القصيم" },
  { id: "khamis", name: "خميس مشيط", nameEn: "Khamis Mushait", region: "منطقة عسير" },
  { id: "yanbu", name: "ينبع", nameEn: "Yanbu", region: "منطقة المدينة المنورة" },
  { id: "jazan", name: "جازان", nameEn: "Jazan", region: "منطقة جازان" },
  { id: "najran", name: "نجران", nameEn: "Najran", region: "منطقة نجران" },
  { id: "sakaka", name: "سكاكا", nameEn: "Sakaka", region: "منطقة الجوف" },
  { id: "arar", name: "عرعر", nameEn: "Arar", region: "منطقة الحدود الشمالية" },
  { id: "rafha", name: "رفحاء", nameEn: "Rafha", region: "منطقة الحدود الشمالية" },
  { id: "jubail", name: "الجبيل", nameEn: "Jubail", region: "المنطقة الشرقية" },
  { id: "hofuf", name: "الهفوف", nameEn: "Hofuf", region: "المنطقة الشرقية" },
  { id: "qatif", name: "القطيف", nameEn: "Qatif", region: "المنطقة الشرقية" },
  { id: "dhahran", name: "الظهران", nameEn: "Dhahran", region: "المنطقة الشرقية" },
  { id: "unayzah", name: "عنيزة", nameEn: "Unayzah", region: "منطقة القصيم" },
  { id: "rass", name: "رأس تنورة", nameEn: "Ras Tanura", region: "المنطقة الشرقية" },
  { id: "alula", name: "العلا", nameEn: "AlUla", region: "منطقة المدينة المنورة" },
];

// دالة للحصول على قائمة المدن للقوائم المنسدلة
export const getCitiesOptions = () => {
  return SAUDI_CITIES.map((city) => ({
    value: city.id,
    label: city.name,
    labelEn: city.nameEn,
  }));
};

// دالة للحصول على اسم المدينة بالعربية
export const getCityName = (cityId) => {
  const city = SAUDI_CITIES.find((c) => c.id === cityId);
  return city ? city.name : cityId;
};

// دالة للحصول على اسم المدينة بالإنجليزية
export const getCityNameEn = (cityId) => {
  const city = SAUDI_CITIES.find((c) => c.id === cityId);
  return city ? city.nameEn : cityId;
};
