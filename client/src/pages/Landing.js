import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Globe,
  Truck,
  Headphones,
  ArrowRight,
  Menu,
  X,
  Phone,
  Mail,
  MapPin,
  Moon,
  Sun
} from 'lucide-react';
import Logo from '../components/Logo';

const Landing = () => {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false);
  const [sidebarOpen, setSidebarOpen] = React.useState(false);
  const [darkMode, setDarkMode] = React.useState(() => {
    const savedMode = localStorage.getItem('darkMode');
    return savedMode ? JSON.parse(savedMode) : true;
  });
  const [language, setLanguage] = React.useState(() => {
    const savedLang = localStorage.getItem('language');
    return savedLang || 'ar';
  });

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    localStorage.setItem('darkMode', JSON.stringify(!darkMode));
    if (!darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  const toggleLanguage = () => {
    setLanguage(language === 'ar' ? 'en' : 'ar');
    localStorage.setItem('language', language === 'ar' ? 'en' : 'ar');
    document.documentElement.dir = language === 'ar' ? 'ltr' : 'rtl';
    document.documentElement.lang = language === 'ar' ? 'en' : 'ar';
  };

  React.useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    }
  }, []);

  React.useEffect(() => {
    document.documentElement.dir = language === 'ar' ? 'rtl' : 'ltr';
    document.documentElement.lang = language;
  }, []);

  const services = [
    {
      icon: Globe,
      title: 'لوجستيات التصدير',
      titleEn: 'Export Logistics',
      description: 'حلول لوجستية للتصدير تبسط العملية وتضمن نقلًا سلسًا وفي الوقت المناسب',
      descriptionEn: 'Our export logistics solutions streamline the process, ensuring smooth & timely transportation'
    },
    {
      icon: Truck,
      title: 'توصيل سريع',
      titleEn: 'Fast Delivery',
      description: 'نقدم حلول لوجستية مبردة وجافة وعادية مع حلول بسيطة ونقل سلس وفي الوقت المناسب',
      descriptionEn: 'We work to provide refrigerated, dry and regular logistics solutions with simple solutions'
    },
    {
      icon: Headphones,
      title: 'دعم 24/7',
      titleEn: '24/7 Support',
      description: 'نعمل مع شركائنا لتقديم خدمة على مدار الساعة طوال أيام الأسبوع',
      descriptionEn: 'We work with our partners to provide 24/7 service'
    }
  ];

  const stats = [
    { value: '80K+', label: 'عملاء وشركاء', labelEn: 'Clients & Partners' },
    { value: '$2.7B', label: 'طلبات التجارة الإلكترونية', labelEn: 'E-commerce Orders' },
    { value: '150+', label: 'دولة', labelEn: 'Countries' },
    { value: '99%', label: 'رضا العملاء', labelEn: 'Customer Satisfaction' }
  ];

  return (
    <div className="min-h-screen bg-edham-black">
      {/* Navigation */}
      <nav className="bg-edham-dark border-b border-edham-gray">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            {/* Logo */}
            <div className="flex items-center gap-3">
              <Logo size="md" />
              <div>
                <h1 className="text-2xl font-bold text-edham-white">إدهام</h1>
                <p className="text-xs text-edham-gold font-semibold">EDHAM for refrigerated Transportation</p>
              </div>
            </div>

            {/* Desktop Menu */}
            <div className="hidden md:flex items-center gap-8">
              <a href="#home" className="text-edham-white hover:text-edham-gold transition-colors">الرئيسية</a>
              <a href="#about" className="text-edham-white hover:text-edham-gold transition-colors">من نحن</a>
              <a href="#services" className="text-edham-white hover:text-edham-gold transition-colors">الخدمات</a>
              <a onClick={() => navigate('/fleet')} className="text-edham-white hover:text-edham-gold transition-colors cursor-pointer">الأسطول</a>
              <a href="#tracking" className="text-edham-white hover:text-edham-gold transition-colors">تتبع الشحنة</a>
              <a href="#contact" className="text-edham-white hover:text-edham-gold transition-colors">اتصل بنا</a>
            </div>

            {/* CTA Button */}
            <div className="hidden md:flex items-center gap-4">
              <button
                onClick={toggleLanguage}
                className="p-2 rounded-lg bg-edham-white/10 hover:bg-edham-white/20 text-edham-white transition-colors font-semibold"
              >
                {language === 'ar' ? 'EN' : 'AR'}
              </button>
              <button
                onClick={toggleDarkMode}
                className="p-2 rounded-lg bg-edham-white/10 hover:bg-edham-white/20 text-edham-white transition-colors"
              >
                {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
              </button>
              <button
                onClick={() => navigate('/register')}
                className="bg-transparent text-edham-white border border-edham-white px-6 py-2 rounded-lg hover:bg-edham-white hover:text-edham-black transition-colors font-semibold"
              >
                إنشاء حساب
              </button>
              <button
                onClick={() => navigate('/login')}
                className="bg-edham-primary text-white px-6 py-2 rounded-lg hover:bg-edham-primaryLight transition-colors font-semibold"
              >
                تسجيل الدخول
              </button>
            </div>

            {/* Mobile Menu Button */}
            <button 
              className="md:hidden text-edham-white"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>

          {/* Mobile Menu */}
          {mobileMenuOpen && (
            <div className="md:hidden mt-4 pb-4">
              <div className="flex flex-col gap-4">
                <a href="#home" className="text-edham-white hover:text-edham-gold transition-colors">الرئيسية</a>
                <a href="#about" className="text-edham-white hover:text-edham-gold transition-colors">من نحن</a>
                <a href="#services" className="text-edham-white hover:text-edham-gold transition-colors">الخدمات</a>
                <a onClick={() => navigate('/fleet')} className="text-edham-white hover:text-edham-gold transition-colors cursor-pointer">الأسطول</a>
                <a href="#tracking" className="text-edham-white hover:text-edham-gold transition-colors">تتبع الشحنة</a>
                <a href="#contact" className="text-edham-white hover:text-edham-gold transition-colors">اتصل بنا</a>
                <button
                  onClick={toggleDarkMode}
                  className="flex items-center gap-2 text-edham-white hover:text-edham-gold transition-colors"
                >
                  {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
                  {darkMode ? 'الوضع النهاري' : 'الوضع الليلي'}
                </button>
                <button
                  onClick={() => navigate('/login')}
                  className="bg-edham-primary text-white px-6 py-2 rounded-lg hover:bg-edham-primaryLight transition-colors font-semibold"
                >
                  تسجيل الدخول
                </button>
              </div>
            </div>
          )}
        </div>
      </nav>

      {/* Hero Section */}
      <section id="home" className="relative min-h-[80vh] flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-edham-black via-edham-dark to-edham-gray"></div>
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-20 left-20 w-32 h-32 bg-edham-white rounded-full blur-3xl"></div>
          <div className="absolute bottom-20 right-20 w-48 h-48 bg-edham-primary rounded-full blur-3xl"></div>
        </div>
        
        <div className="relative container mx-auto px-4 text-center z-10">
          <div className="mb-8">
            <div className="inline-flex items-center justify-center bg-edham-white/10 backdrop-blur-sm p-4 rounded-full mb-6">
              <Logo size="xl" />
            </div>
          </div>
          <h1 className="text-5xl md:text-7xl font-bold text-edham-white mb-4">
            إدهام
          </h1>
          <p className="text-2xl md:text-3xl text-edham-gold font-semibold mb-6">
            EDHAM for refrigerated Transportation
          </p>
          <p className="text-xl text-edham-white/80 mb-8 max-w-2xl mx-auto">
            خدمات لوجستية وتخزين ونقل مبرد متكاملة لنقل بضائعك بأمان وكفاءة
          </p>
          <div className="flex flex-col md:flex-row gap-4 justify-center">
            <button
              onClick={() => navigate('/login')}
              className="bg-edham-primary text-white px-8 py-4 rounded-xl hover:bg-edham-primaryLight transition-colors font-semibold text-lg flex items-center justify-center gap-2"
            >
              ابدأ الآن
              <ArrowRight className="w-5 h-5" />
            </button>
            <a
              href="#services"
              className="bg-edham-white/10 backdrop-blur-sm text-white px-8 py-4 rounded-xl hover:bg-edham-white/20 transition-colors font-semibold text-lg border border-edham-white/30"
            >
              اكتشف خدماتنا
            </a>
          </div>
        </div>
      </section>

      {/* Statistics Section */}
      <section className="bg-edham-dark py-16">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, index) => (
              <div key={index} className="text-center">
                <div className="text-4xl md:text-5xl font-bold text-edham-white mb-2">{stat.value}</div>
                <div className="text-edham-white/70">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section id="services" className="py-20 bg-edham-black">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-4xl md:text-5xl font-bold text-edham-white mb-4">
              خدماتنا
            </h2>
            <p className="text-xl text-edham-white/70 mb-2">
              مجموعة واسعة من الخدمات اللوجستية
            </p>
            <p className="text-edham-white/50">Wide variety of logistics services</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-12">
            {services.map((service, index) => (
              <div key={index} className="bg-edham-dark rounded-2xl overflow-hidden hover:bg-edham-gray transition-colors group">
                <div className="relative h-48 overflow-hidden">
                  <img 
                    src={index === 0 ? "/Screenshot 2026-04-18 221548.png" : 
                         index === 1 ? "/Screenshot 2026-04-18 224815.png" : 
                         "/Screenshot 2026-04-18 224823.png"} 
                    alt={service.title} 
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" 
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
                </div>
                <div className="p-6">
                  <div className="bg-edham-primary/20 w-12 h-12 rounded-xl flex items-center justify-center mb-4 group-hover:bg-edham-primary/30 transition-colors">
                    <service.icon className="w-6 h-6 text-edham-primary" />
                  </div>
                  <h3 className="text-xl font-bold text-edham-white mb-2">{service.title}</h3>
                  <p className="text-edham-gold text-sm mb-3 font-semibold">{service.titleEn}</p>
                  <p className="text-edham-white/70 mb-2 text-sm">{service.description}</p>
                  <p className="text-edham-white/50 text-xs">{service.descriptionEn}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="text-center">
            <a
              href="#about"
              className="inline-flex items-center gap-2 text-edham-primary hover:text-edham-primaryLight transition-colors font-semibold"
            >
              عرض جميع الخدمات
              <ArrowRight className="w-5 h-5" />
            </a>
          </div>
        </div>
      </section>

      {/* Fleet Gallery Section */}
      <section id="fleet" className="py-20 bg-edham-black">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-4xl md:text-5xl font-bold text-edham-white mb-4">
              أسطولنا
            </h2>
            <p className="text-xl text-edham-white/70 mb-2">
              أسطول حديث ومتطور لنقل البضائع المبردة
            </p>
            <p className="text-edham-white/50">Modern and advanced fleet for refrigerated goods transport</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 221548.png" alt="Truck 1" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل مبرد كبيرة</h3>
                <p className="text-edham-white/70 text-sm">Large Refrigerated Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 221606.png" alt="Truck 2" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل ثقيلة</h3>
                <p className="text-edham-white/70 text-sm">Heavy Duty Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224751.png" alt="Truck 3" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">مركبة نقل متوسطة</h3>
                <p className="text-edham-white/70 text-sm">Medium Transport Vehicle</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224802.png" alt="Truck 4" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل سريعة</h3>
                <p className="text-edham-white/70 text-sm">Fast Delivery Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224815.png" alt="Truck 5" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">مركبة نقل صغيرة</h3>
                <p className="text-edham-white/70 text-sm">Small Transport Vehicle</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224823.png" alt="Truck 6" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل مبرد متوسطة</h3>
                <p className="text-edham-white/70 text-sm">Medium Refrigerated Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224845.png" alt="Truck 7" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل بضائع</h3>
                <p className="text-edham-white/70 text-sm">Cargo Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224902.png" alt="Truck 8" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">شاحنة نقل ضخمة</h3>
                <p className="text-edham-white/70 text-sm">Large Cargo Truck</p>
              </div>
            </div>

            <div className="bg-edham-dark rounded-2xl overflow-hidden group">
              <div className="relative h-64 overflow-hidden">
                <img src="/Screenshot 2026-04-18 224914.png" alt="Truck 9" className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
              </div>
              <div className="p-6">
                <h3 className="text-xl font-bold text-edham-white mb-2">مركبة نقل متعددة</h3>
                <p className="text-edham-white/70 text-sm">Multi-purpose Vehicle</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="py-20 bg-edham-dark">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl font-bold text-edham-white mb-6">
                من نحن
              </h2>
              <p className="text-edham-white/70 mb-4 text-lg">
                إدهام للخدمات اللوجستية والتخزين والنقل المبرد هي شركة رائدة في مجال الخدمات اللوجستية، نقدم حلولاً متكاملة لنقل البضائع المبردة والجافة بأعلى معايير الجودة والأمان.
              </p>
              <p className="text-edham-white/50 mb-6">
                Edham for Logistics, Storage and Refrigerated Transportation is a leading company in logistics services, providing integrated solutions for refrigerated and dry goods transport with the highest quality and safety standards.
              </p>
              <div className="flex flex-wrap gap-4">
                <div className="bg-edham-black px-6 py-3 rounded-lg">
                  <p className="text-edham-white font-semibold">نقل مبرد</p>
                  <p className="text-edham-white/50 text-sm">Refrigerated Transport</p>
                </div>
                <div className="bg-edham-black px-6 py-3 rounded-lg">
                  <p className="text-edham-white font-semibold">تخزين</p>
                  <p className="text-edham-white/50 text-sm">Storage</p>
                </div>
                <div className="bg-edham-black px-6 py-3 rounded-lg">
                  <p className="text-edham-white font-semibold">لوجستيات</p>
                  <p className="text-edham-white/50 text-sm">Logistics</p>
                </div>
              </div>
            </div>
            <div className="bg-edham-black rounded-2xl p-8">
              <div className="text-center mb-8">
                <img src="/Screenshot 2026-04-18 224233.png" alt="Edham Logo" className="w-24 h-24 object-contain mx-auto mb-4" />
                <h3 className="text-2xl font-bold text-edham-white">رؤيتنا</h3>
                <p className="text-edham-white/70 mt-2">Our Vision</p>
              </div>
              <p className="text-edham-white/70 text-center">
                أن نكون الخيار الأول للشركات والأفراد في مجال الخدمات اللوجستية والنقل المبرد من خلال تقديم خدمات عالية الجودة وابتكار مستمر.
              </p>
              <p className="text-edham-white/50 text-center mt-4 text-sm">
                To be the first choice for companies and individuals in logistics and refrigerated transport services by providing high-quality services and continuous innovation.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Tracking Section */}
      <section id="tracking" className="py-20 bg-edham-black">
        <div className="container mx-auto px-4">
          <div className="max-w-2xl mx-auto">
            <div className="text-center mb-12">
              <h2 className="text-4xl font-bold text-edham-white mb-4">
                تتبع شحنتك
              </h2>
              <p className="text-edham-white/70">
                أدخل رقم الشحنة لتتبع حالتها في الوقت الفعلي
              </p>
            </div>
            <div className="bg-edham-dark rounded-2xl p-8">
              <div className="flex gap-4">
                <input
                  type="text"
                  placeholder="أدخل رقم الشحنة"
                  className="flex-1 px-6 py-4 bg-edham-black border border-edham-gray rounded-xl text-edham-white placeholder-edham-white/50 focus:outline-none focus:border-edham-primary"
                />
                <button className="bg-edham-primary text-white px-8 py-4 rounded-xl hover:bg-edham-primaryLight transition-colors font-semibold">
                  تتبع
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Contact Section */}
      <section id="contact" className="py-20 bg-edham-dark">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-4xl font-bold text-edham-white mb-4">
              اتصل بنا
            </h2>
            <p className="text-edham-white/70">
              نحن هنا لمساعدتك على مدار الساعة
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-4xl mx-auto">
            <div className="bg-edham-black rounded-2xl p-8 text-center">
              <div className="bg-edham-primary/20 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <Phone className="w-8 h-8 text-edham-primary" />
              </div>
              <h3 className="text-xl font-bold text-edham-white mb-2">الهاتف</h3>
              <p className="text-edham-white/70">+966 50 XXX XXXX</p>
            </div>
            <div className="bg-edham-black rounded-2xl p-8 text-center">
              <div className="bg-edham-primary/20 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <Mail className="w-8 h-8 text-edham-primary" />
              </div>
              <h3 className="text-xl font-bold text-edham-white mb-2">البريد الإلكتروني</h3>
              <p className="text-edham-white/70">info@edham.com</p>
            </div>
            <div className="bg-edham-black rounded-2xl p-8 text-center">
              <div className="bg-edham-primary/20 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <MapPin className="w-8 h-8 text-edham-primary" />
              </div>
              <h3 className="text-xl font-bold text-edham-white mb-2">العنوان</h3>
              <p className="text-edham-white/70">الرياض، المملكة العربية السعودية</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-edham-black border-t border-edham-gray py-12">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-3 mb-4">
                <Logo size="sm" />
                <div>
                  <h3 className="text-lg font-bold text-edham-white">إدهام</h3>
                  <p className="text-xs text-edham-gold font-semibold">EDHAM</p>
                </div>
              </div>
              <p className="text-edham-white/50 text-sm">
                خدمات لوجستية وتخزين ونقل مبرد متكاملة
              </p>
            </div>
            <div>
              <h4 className="text-edham-white font-semibold mb-4">روابط سريعة</h4>
              <ul className="space-y-2">
                <li><a href="#home" className="text-edham-white/50 hover:text-edham-gold transition-colors">الرئيسية</a></li>
                <li><a href="#about" className="text-edham-white/50 hover:text-edham-gold transition-colors">من نحن</a></li>
                <li><a href="#services" className="text-edham-white/50 hover:text-edham-gold transition-colors">الخدمات</a></li>
                <li><a href="#contact" className="text-edham-white/50 hover:text-edham-gold transition-colors">اتصل بنا</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-edham-white font-semibold mb-4">الخدمات</h4>
              <ul className="space-y-2">
                <li className="text-edham-white/50">نقل مبرد</li>
                <li className="text-edham-white/50">تخزين</li>
                <li className="text-edham-white/50">لوجستيات التصدير</li>
                <li className="text-edham-white/50">توصيل سريع</li>
              </ul>
            </div>
            <div>
              <h4 className="text-edham-white font-semibold mb-4">تواصل معنا</h4>
              <ul className="space-y-2">
                <li className="text-edham-white/50 flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  +966 50 XXX XXXX
                </li>
                <li className="text-edham-white/50 flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  info@edham.com
                </li>
              </ul>
            </div>
          </div>
          <div className="border-t border-edham-gray pt-8 text-center">
            <p className="text-edham-white/50">
              © 2024 إدهام للخدمات اللوجستية. جميع الحقوق محفوظة.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Landing;
