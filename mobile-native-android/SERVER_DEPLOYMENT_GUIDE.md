# دليل رفع وتشغيل السيرفر (Server Deployment Guide) 🚀

هذا الدليل يشرح بالتفصيل كيفية بناء، رفع، وتشغيل الواجهة الخلفية (Backend API) لمشروع إدهام.

## 1. المتطلبات التقنية (Prerequisites)
- **الجهاز:** سيرفر Linux (يفضل Ubuntu 22.04) أو Azure VM.
- **البيئة:** Java 17+, Maven 3.6+.
- **قاعدة البيانات:** MySQL 8.0+.

---

## 2. بناء ملف السيرفر (Building the Jar)
على جهاز التطوير الخاص بك، اذهب لمجلد الـ Backend وقم بتنفيذ الأمر التالي:
```bash
cd backend-api
mvn clean package -DskipTests
```
سيتم إنتاج ملف في مسار `target/edham-logistics-api-1.0.0.jar`.

---

## 3. رفع السيرفر (Uploading to Server)
استخدم بروتوكول **SCP** أو تطبيق مثل **WinSCP** لرفع الملف للسيرفر:
```bash
scp target/edham-logistics-api-1.0.0.jar username@your-server-ip:/home/username/app/
```

---

## 4. رفع الملفات والمرفقات (File Upload System)
مشروع إدهام يعتمد على مجلد محلي على السيرفر لتخزين الصور والمرفقات (Attachments).

### **مسار التخزين الافتراضي:**
يتم تخزين الملفات في المسار: `/var/www/edham/uploads/`

### **كيفية رفع ملفات يدوياً (Manual Upload):**
إذا أردت رفع ملفات للسيرفر لتكون متاحة للتطبيق:
1. انشئ المجلد: `mkdir -p /var/www/edham/uploads/photos`
2. ارفع الصور إليه باستخدام SCP.
3. تأكد من إعطاء صلاحيات القراءة: `chmod -R 755 /var/www/edham/uploads/`

---

## 5. تشغيل السيرفر (Running the API)
يفضل استخدام **Systemd** لضمان استمرار عمل السيرفر حتى بعد إعادة التشغيل.

1. انشئ ملف الخدمة: `sudo nano /etc/systemd/system/edham-api.service`
2. ضع المحتوى التالي:
```ini
[Unit]
Description=Edham Logistics API
After=syslog.target

[Service]
User=username
ExecStart=/usr/bin/java -jar /home/username/app/edham-logistics-api-1.0.0.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always

[Install]
WantedBy=multi-user.target
```
3. شغل الخدمة:
```bash
sudo systemctl daemon-reload
sudo systemctl enable edham-api
sudo systemctl start edham-api
```

---

## 6. المسارات الهامة للـ API (Important Paths)
- **رابط السيرفر:** `http://your-server-ip:8080/api/v1/`
- **مسار الصور:** `http://your-server-ip:8080/api/v1/files/download/{fileName}`
- **مسار التتبع (Tracking):** `POST /api/v1/tracking/update`

---
**ملاحظة:** تأكد من تحديث ملف `application-prod.properties` على السيرفر ببيانات قاعدة البيانات الحقيقية.
