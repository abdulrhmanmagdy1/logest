# 🚀 **دليل النشر والتشغيل - نظام إدهام اللوجستي**

## 🎯 **مقدمة**

هذا الدليل يشرح خطوات نشر وتشغيل **نظام إدهام اللوجستي المتكامل** في بيئة الإنتاج.

---

## 📋 **المتطلبات الأساسية**

### **🖥️ النظام التشغيلي**
- **Windows 10/11** أو **Linux Ubuntu 20.04+**
- **8GB RAM** كحد أدنى
- **50GB** مساحة تخزين حرة
- **Docker** و **Docker Compose**

### **🌐 الشبكة**
- **اتصال إنترنت مستقر**
- **نطاق عرض كافي** (10Mbps+)
- **IP ثابت** (مفضل للإنتاج)

### **🔧 البرامج المطلوبة**
- **Docker Desktop** (لـ Windows)
- **Git** للـ version control
- **محرر أكواد** (VS Code محدد)

---

## 🐳 **تثبيت Docker**

### **📥 تثبيت Docker Desktop**
1. **حمل Docker Desktop** من [docker.com](https://www.docker.com/products/docker-desktop/)
2. **قم بتثبيت البرنامج** مع Keep defaults
3. **أعد تشغيل الجهاز** بعد التثبيت
4. **تحقق من التثبيت:**
   ```bash
   docker --version
   docker-compose --version
   ```

### **🔧 إعدادات Docker**
1. **افتح Docker Desktop**
2. **اذهب إلى Settings** → Resources
3. **خصص الموارد:**
   - **Memory:** 8GB+
   - **CPUs:** 4+ cores
   - **Disk:** 50GB+

---

## 📁 **هيكلية الملفات**

```
d:\logest\
├── 📄 .env                 # متغيرات البيئة
├── 📄 docker-compose.yml     # Docker configuration
├── 📁 client/              # React Frontend
├── 📁 backend/             # Node.js API
├── 📁 mobile-native-ios/    # iOS Swift App
├── 📁 mobile-native-android/ # Android Kotlin App
├── 📁 nginx/               # Nginx Configuration
├── 📁 docs/                # Documentation
└── 📁 logs/                # Application Logs
```

---

## 🔧 **إعداد متغيرات البيئة**

### **📝 إنشاء ملف .env**
```bash
# أنشئ ملف .env في d:\logest\
# انسخ المحتوى التالي:
```

### **🔑 متغيرات البيئة (.env)**
```bash
# ============================================
# 🗄️ Database Configuration
# ============================================
MONGODB_URI=mongodb://localhost:27017/edham_logistics
MONGODB_DB_NAME=edham_logistics

# ============================================
# 🔴 Redis Configuration  
# ============================================
REDIS_URL=redis://localhost:6379

# ============================================
# 🚀 Server Configuration
# ============================================
NODE_ENV=production
PORT=5000
API_VERSION=v1

# ============================================
# 🔐 JWT Configuration
# ============================================
JWT_SECRET=your_super_secret_jwt_key_here_change_in_production
JWT_EXPIRE=7d

# ============================================
# 💳 Payment Configuration
# ============================================
STRIPE_SECRET_KEY=sk_live_your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=pk_live_your_stripe_publishable_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# ============================================
# 📧 Email Configuration
# ============================================
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=your_email@edham.com
EMAIL_PASS=your_app_password

# ============================================
# 📱 Mobile Configuration
# ============================================
FCM_SERVER_KEY=your_fcm_server_key
APNS_KEY_ID=your_apns_key_id
APNS_TEAM_ID=your_team_id

# ============================================
# 🌐 Frontend Configuration
# ============================================
REACT_APP_API_URL=https://api.edham.com
REACT_APP_UNIFIED_MODE=true
REACT_APP_VERSION=2.0.0

# ============================================
# 🐳 Docker Configuration
# ============================================
COMPOSE_PROJECT_NAME=edham-logistics
COMPOSE_FILE=docker-compose.yml
```

---

## 🚀 **خطوات النشر**

### **1️⃣ استنساخ المشروع**
```bash
# استنساخ المشروع من GitHub
git clone https://github.com/your-org/edham-logistics.git
cd edham-logistics

# أو إذا كان لديك المشروع محلياً
cd d:\logest
```

### **2️⃣ إعداد متغيرات البيئة**
```bash
# نسخ ملف المتغيرات النموذجي
cp .env.example .env

# تعديل المتغيرات حسب بيئتك
notepad .env
```

### **3️⃣ بناء الحاويات (Build)**
```bash
# بناء جميع الحاويات
docker-compose build

# بناء حاوية معينة
docker-compose build backend
docker-compose build unified-app
```

### **4️⃣ تشغيل النظام (Start)**
```bash
# تشغيل جميع الخدمات في الخلفية
docker-compose up -d

# تشغيل مع عرض الـ logs
docker-compose up -d --follow
```

### **5️⃣ التحقق من الحالة**
```bash
# عرض حالة جميع الخدمات
docker-compose ps

# عرض الـ logs لحاوية معينة
docker-compose logs backend
docker-compose logs unified-app
```

---

## 🔍 **التحقق من التشغيل**

### **🌐 اختبار الوصول للخدمات**

#### **1. الموقع الإلكتروني**
```bash
# افتح المتصفح وادخل إلى:
http://localhost:3000

# أو للإنتاج:
https://edham.com
```

#### **2. API Documentation**
```bash
# وثائق API:
http://localhost:5000/api/docs

# أو للإنتاج:
https://api.edham.com/docs
```

#### **3. لوحة التحكم الإدارية**
```bash
# لوحة التحكم:
http://localhost:3000/admin

# تسجيل الدخول الأولي:
Email: admin@edham.com
Password: admin123
```

### **📊 اختبار قاعدة البيانات**
```bash
# الاتصال بـ MongoDB
docker exec -it logest-mongodb-1 mongo

# عرض قواعد البيانات
show dbs
use edham_logistics
show collections
```

### **🔴 اختبار Redis**
```bash
# الاتصال بـ Redis
docker exec -it logest-redis-1 redis-cli

# اختبار الاتصال
ping
```

---

## 🔄 **إدارة الخدمات**

### **🛑 إيقاف الخدمات**
```bash
# إيقاف جميع الخدمات
docker-compose down

# إيقاف وإزالة الحاويات
docker-compose down -v
```

### **🔄 إعادة تشغيل الخدمات**
```bash
# إعادة تشغيل جميع الخدمات
docker-compose restart

# إعادة تشغيل خدمة معينة
docker-compose restart backend
docker-compose restart unified-app
```

### **📊 عرض الـ Logs**
```bash
# عرض جميع الـ logs
docker-compose logs

# عرض logs لخدمة معينة
docker-compose logs --tail=100 backend

# عرض logs في الوقت الفعلي
docker-compose logs -f backend
```

---

## 🔧 **الصيانة والتحديثات**

### **📦 تحديث المشروع**
```bash
# سحب آخر التحديثات
git pull origin main

# تحديث الحاويات مع التغييرات
docker-compose up -d --build
```

### **🗄️ نسخ احتياطي لقاعدة البيانات**
```bash
# إنشاء نسخة احتياطية
docker exec logest-mongodb-1 mongodump --out /backup/backup-$(date +%Y%m%d)

# استعادة النسخة الاحتياطية
docker exec logest-mongodb-1 mongorestore /backup/backup-20240501
```

### **📊 مراقبة الأداء**
```bash
# عرض استهلاك الموارد
docker stats

# عرض استخدام القرص
docker system df

# تنظيف الحاويات الغير مستخدمة
docker system prune -f
```

---

## 🌍 **النشر في الإنتاج (Production)**

### **🔐 إعدادات الأمان للإنتاج**
```bash
# تغيير المتغيرات الحساسة
JWT_SECRET=your_production_jwt_secret_256_bits_long
STRIPE_SECRET_KEY=sk_live_your_production_stripe_key
EMAIL_PASS=your_production_email_password

# إعدادات SSL/TLS
REACT_APP_API_URL=https://api.edham.com
```

### **🌐 إعدادات Nginx للإنتاج**
```nginx
# في nginx/nginx.conf
server {
    listen 443 ssl http2;
    server_name edham.com www.edham.com;
    
    # SSL Configuration
    ssl_certificate /etc/ssl/edham.com.crt;
    ssl_certificate_key /etc/ssl/edham.com.key;
    
    # Security Headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
}
```

### **🔧 إعدادات Docker للإنتاج**
```yaml
# في docker-compose.prod.yml
version: '3.8'
services:
  backend:
    restart: always
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'
  
  unified-app:
    restart: always
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
```

---

## 🚨 **استكشاف الأخطاء الشائعة**

### **🔍 مشاكل المنافذ (Ports)**
```bash
# التحقق من المنافذ المستخدمة
netstat -ano | findstr :3000
netstat -ano | findstr :5000

# إيقاف الخدمات التي تستخدم نفس المنافذ
taskkill /PID <PID> /F

# تغيير المنافذ في docker-compose.yml
ports:
  - "3001:3000"  # تغيير من 3000 إلى 3001
```

### **🗄️ مشاكل قاعدة البيانات**
```bash
# التحقق من حالة MongoDB
docker-compose logs mongodb

# إعادة تشغيل MongoDB
docker-compose restart mongodb

# إصلاح قاعدة البيانات
docker exec -it logest-mongodb-1 mongo edham_logistics --eval "db.repairDatabase()"
```

### **🐳 مشاكل Docker**
```bash
# تنظيف Docker
docker system prune -a

# إعادة بناء الحاويات
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

## 📞 **الدعم والمساعدة**

### **🆘 معلومات الدعم**
- **📧 البريد الفني:** support@edham.com
- **📱 هاتف الطوارئ:** +966 50 XXX XXXX
- **🌐 موقع الدعم:** https://support.edham.com
- **📖 وثائق فنية:** https://docs.edham.com

### **📋 قائمة المشاكل الشائعة**
1. **منافذ مشغولة:** استخدم `netstat` للتحقق
2. **مساحة قرص ممتلئة:** استخدم `docker system prune`
3. **ذاكرة غير كافية:** زد RAM في Docker Desktop
4. **مشاكل الشبكة:** تحقق من إعدادات Firewall
5. **فشل في البناء:** تحقق من Docker version

---

## 🎯 **الخلاصة**

باتباع هذا الدليل، يمكنك:
- ✅ **نشر النظام** في بيئة التطوير أو الإنتاج
- ✅ **إدارة الخدمات** بكفاءة
- ✅ **صيانة النظام** بانتظام
- ✅ **استكشاف الأخطاء** وحلها بسرعة

**نظام إدهام اللوجستي الآن جاهز للتشغيل!** 🚀

---

*آخر تحديث: مايو 2026*  
*الإصدار: 2.0.0*  
*الحالة: ✅ جاهز للإنتاج*
