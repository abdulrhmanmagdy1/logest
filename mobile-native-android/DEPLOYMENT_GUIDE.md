# 🚀 دليل نشر تطبيق إدهام اللوجستي

## 📋 نظرة عامة

دليل شامل لنشر تطبيق "إدهام اللوجستي" على المنصات المختلفة بما في ذلك متاجر التطبيقات، استضافة الويب، والخوادم السحابية.

## 🏗️ المتطلبات الأساسية

### **الحسابات والخدمات**
- **Google Play Developer Account** - لنشر تطبيق الأندرويد
- **Apple Developer Account** - لنشر تطبيق iOS (إذا متوفر)
- **Domain Name** - اسم نطاق للتطبيق الويب
- **SSL Certificate** - شهادة SSL للاتصالات الآمنة
- **Cloud Services** - حسابات AWS/Azure/GCP

### **الأدوات**
- **Android Studio** - لتوقيع وبناء تطبيق الأندرويد
- **Docker** - للحاويات والنشر السهل
- **Git** - للتحكم في الإصدارات
- **CI/CD Pipeline** - للنشر التلقائي

## 📱 نشر تطبيق الأندرويد

### **1. التحضير للنشر**

#### **توقيع التطبيق**
```bash
# إنشاء مفتاح توقيع إنتاجي
keytool -genkey -v -keystore edham-production.keystore \
    -alias edham-logistics \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Edham Logistics, OU=Mobile, O=Edham Logistics, L=Riyadh, ST=Riyadh, C=SA"

# نقل الملف إلى مكان آمن
mv edham-production.keystore ~/.android/
```

#### **إعدادات التوقيع في build.gradle**
```kotlin
android {
    signingConfigs {
        release {
            storeFile file(System.getProperty("user.home") + "/.android/edham-production.keystore")
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias "edham-logistics"
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            
            // إعدادات النشر
            ndk {
                abiFilters 'arm64-v8a', 'armeabi-v7a'
            }
        }
    }
}
```

#### **بناء نسخة الإنتاج**
```bash
# تنظيف المشروع
./gradlew clean

# بناء نسخة Release
./gradlew assembleRelease

# بناء ملف AAB (مفضل لـ Google Play)
./gradlew bundleRelease
```

### **2. نشر على Google Play Store**

#### **الإعداد الأولي**
1. **تسجيل الدخول** إلى [Google Play Console](https://play.google.com/console)
2. **إنشاء تطبيق جديد**
3. **ملء معلومات التطبيق الأساسية**
   - اسم التطبيق: "إدهام اللوجستي"
   - الحزمة: `com.edham.logistics`
   - الفئة: Business
   - المحتوى: Everyone

#### **رفع التطبيق**
```bash
# استخدام Google Play CLI
google-play-cli upload \
    --package-name com.edham.logistics \
    --aab-file app/build/outputs/bundle/release/app-release.aab \
    --track production
```

#### **معلومات المتجر**
```
اسم التطبيق: إدهام اللوجستي
الوصف المختصر: نظام لوجستي احترافي مع تقنيات الجيل الرابع
الوصف الكامل: تطبيق إدهام اللوجستي هو نظام لوجستي متكامل يوفر إدارة شحنات ذكية، تتبع مباشر، وتحليلات متقدمة باستخدام أحدث التقنيات.

الكلمات المفتاحية:
- لوجستيك، شحنات، تتبع، إدارة، نقل، بضائع، توصيل

المميزات:
- تتبع مباشر للشحنات
- ذكاء اصطناعي للتنبؤات
- واقع معزز للقياسات
- بلوك تشين للتوثيق
- إدارة متقدمة للعملاء
```

#### **إعدادات المتجر**
- **الأيقونات**: 512x512px
- **الصور**: 1024x500px (Hero Image)
- **لقطات الشاشة**: 2-8 صور من الأجهزة المختلفة
- **المحتوى**: تقييم المحتوى (PEGI 3+)

### **3. النشر المباشر (Direct APK)**

#### **إنشاء صفحة تحميل**
```html
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>تحميل تطبيق إدهام اللوجستي</title>
    <style>
        body { font-family: 'Arial', sans-serif; text-align: center; padding: 20px; }
        .download-btn { background: #FF6B35; color: white; padding: 15px 30px; border: none; border-radius: 8px; font-size: 18px; cursor: pointer; }
        .qr-code { margin: 20px auto; max-width: 200px; }
    </style>
</head>
<body>
    <h1>تطبيق إدهام اللوجستي</h1>
    <p>نظام لوجستي احترافي من الجيل الرابع</p>
    
    <div class="qr-code">
        <!-- QR Code pointing to APK -->
        <img src="edham-logistics-qr.png" alt="QR Code">
    </div>
    
    <a href="app-release.apk" class="download-btn">تحميل التطبيق</a>
    
    <p><small>حجم الملف: ~25MB | يتطلب Android 7.0 أو أحدث</small></p>
</body>
</html>
```

## 🌐 نشر تطبيق الويب

### **1. التحضير للنشر**

#### **بناء نسخة الإنتاج**
```bash
cd client

# تثبيت الاعتماديات
npm ci --only=production

# بناء نسخة الإنتاج
npm run build

# تحسين الحجم
npm run optimize
```

#### **إعدادات الإنتاج في package.json**
```json
{
  "homepage": "https://edham-logistics.com",
  "scripts": {
    "build": "react-scripts build",
    "build:analyze": "npm run build && npx webpack-bundle-analyzer build/static/js/*.js",
    "optimize": "npm run build && npx serve -s build -l 3000"
  }
}
```

### **2. النشر على Netlify**

#### **النشر التلقائي**
```yaml
# netlify.toml
[build]
  publish = "build"
  command = "npm run build"

[build.environment]
  NODE_VERSION = "18"
  REACT_APP_API_URL = "https://api.edham-logistics.com"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[[headers]]
  for = "/*"
  [headers.values]
    X-Frame-Options = "DENY"
    X-XSS-Protection = "1; mode=block"
    X-Content-Type-Options = "nosniff"
    Referrer-Policy = "strict-origin-when-cross-origin"
```

#### **النشر عبر CLI**
```bash
# تثبيت Netlify CLI
npm install -g netlify-cli

# تسجيل الدخول
netlify login

# النشر
netlify deploy --prod --dir=build --site=edham-logistics.netlify.app
```

### **3. النشر على Vercel**

#### **إعدادات vercel.json**
```json
{
  "version": 2,
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": {
        "distDir": "build"
      }
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ],
  "env": {
    "REACT_APP_API_URL": "https://api.edham-logistics.com"
  }
}
```

#### **النشر**
```bash
# تثبيت Vercel CLI
npm install -g vercel

# النشر
vercel --prod
```

### **4. النشر على AWS S3 + CloudFront**

#### **إعدادات S3**
```bash
# إنشاء S3 bucket
aws s3 mb s3://edham-logistics-web

# تمكين استضافة المواقع الثابتة
aws s3 website s3://edham-logistics-web \
    --index-document index.html \
    --error-document index.html

# رفع الملفات
aws s3 sync build/ s3://edham-logistics-web --delete

# تعيين الأذونات
aws s3api put-bucket-policy --bucket edham-logistics-web --policy '{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::edham-logistics-web/*"
        }
    ]
}'
```

#### **إعدادات CloudFront**
```bash
# إنشاء توزيع CloudFront
aws cloudfront create-distribution --distribution-config '{
    "CallerReference": "edham-logistics-web-'$(date +%s)'",
    "Comment": "Edham Logistics Web App",
    "DefaultRootObject": "index.html",
    "Origins": {
        "Quantity": 1,
        "Items": [
            {
                "Id": "S3-edham-logistics-web",
                "DomainName": "edham-logistics-web.s3.amazonaws.com",
                "S3OriginConfig": {
                    "OriginAccessIdentity": ""
                }
            }
        ]
    },
    "DefaultCacheBehavior": {
        "TargetOriginId": "S3-edham-logistics-web",
        "ViewerProtocolPolicy": "redirect-to-https",
        "TrustedSigners": {
            "Enabled": false,
            "Quantity": 0
        },
        "ForwardedValues": {
            "QueryString": false,
            "Cookies": {
                "Forward": "none"
            }
        },
        "MinTTL": 86400
    },
    "Enabled": true
}'
```

## 🖥️ نشر الخادم الخلفي

### **1. التحضير للنشر**

#### **إعدادات الإنتاج**
```javascript
// config/production.js
module.exports = {
    port: process.env.PORT || 3000,
    database: {
        uri: process.env.MONGODB_URI,
        options: {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            maxPoolSize: 10,
            serverSelectionTimeoutMS: 5000,
            socketTimeoutMS: 45000,
        }
    },
    redis: {
        url: process.env.REDIS_URL,
        options: {
            retryDelayOnFailover: 100,
            enableReadyCheck: false,
            maxRetriesPerRequest: null,
        }
    },
    jwt: {
        secret: process.env.JWT_SECRET,
        expiresIn: '7d',
        refreshExpiresIn: '30d'
    },
    cors: {
        origin: process.env.ALLOWED_ORIGINS?.split(',') || ['https://edham-logistics.com'],
        credentials: true
    },
    rateLimit: {
        windowMs: 15 * 60 * 1000, // 15 minutes
        max: 100 // limit each IP to 100 requests per windowMs
    }
};
```

#### **Dockerfile**
```dockerfile
# Multi-stage build
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./
COPY tsconfig.json ./

# Install dependencies
RUN npm ci --only=production && npm cache clean --force

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM node:18-alpine AS production

WORKDIR /app

# Install PM2 for process management
RUN npm install -g pm2

# Copy built application
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/ecosystem.config.js ./ecosystem.config.js

# Create non-root user
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nodejs -u 1001

# Change ownership
RUN chown -R nodejs:nodejs /app
USER nodejs

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:3000/health || exit 1

# Start the application
CMD ["pm2-runtime", "ecosystem.config.js"]
```

#### **ecosystem.config.js**
```javascript
module.exports = {
  apps: [{
    name: 'edham-logistics-api',
    script: 'dist/server.js',
    instances: 'max',
    exec_mode: 'cluster',
    env: {
      NODE_ENV: 'production',
      PORT: 3000
    },
    error_file: '/var/log/edham-logistics/error.log',
    out_file: '/var/log/edham-logistics/out.log',
    log_file: '/var/log/edham-logistics/combined.log',
    time: true,
    max_memory_restart: '1G',
    node_args: '--max-old-space-size=1024'
  }]
};
```

### **2. النشر على Heroku**

#### **إعدادات Heroku**
```json
{
  "name": "edham-logistics-api",
  "scripts": {
    "start": "node dist/server.js",
    "heroku-postbuild": "npm run build"
  },
  "engines": {
    "node": "18.x",
    "npm": "9.x"
  },
  "heroku": {
    "buildpacks": [
      "heroku/nodejs"
    ]
  }
}
```

#### **النشر**
```bash
# تسجيل الدخول
heroku login

# إنشاء تطبيق
heroku create edham-logistics-api

# إعدادات البيئة
heroku config:set NODE_ENV=production
heroku config:set MONGODB_URI=mongodb://user:pass@host:port/db
heroku config:set JWT_SECRET=your-super-secret-key
heroku config:set REDIS_URL=redis://user:pass@host:port

# رفع الكود
git push heroku main

# تشغيل التطبيق
heroku ps:scale web=1

# فتح التطبيق
heroku open
```

### **3. النشر على AWS EC2**

#### **User Data Script**
```bash
#!/bin/bash

# Update system
yum update -y

# Install Node.js
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
yum install -y nodejs

# Install PM2
npm install -g pm2

# Install Nginx
yum install -y nginx

# Start and enable Nginx
systemctl start nginx
systemctl enable nginx

# Create app directory
mkdir -p /var/www/edham-logistics
cd /var/www/edham-logistics

# Clone repository
git clone https://github.com/your-repo/edham-logistics.git .

# Install dependencies
npm ci --only=production

# Build application
npm run build

# Start application with PM2
pm2 start ecosystem.config.js --env production
pm2 save
pm2 startup

# Configure Nginx
cat > /etc/nginx/conf.d/edham-logistics.conf << EOF
server {
    listen 80;
    server_name api.edham-logistics.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
    }
}
EOF

# Restart Nginx
systemctl restart nginx
```

### **4. النشر على DigitalOcean**

#### **Docker Compose**
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=production
      - MONGODB_URI=mongodb://mongo:27017/edham-logistics
      - REDIS_URL=redis://redis:6379
    depends_on:
      - mongo
      - redis
    restart: unless-stopped

  mongo:
    image: mongo:6.0
    volumes:
      - mongo_data:/data/db
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=password
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mongo_data:
  redis_data:
```

## 🔧 إعدادات النشر المتقدم

### **1. CI/CD Pipeline**

#### **GitHub Actions**
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - run: npm ci
      - run: npm test

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker image
        run: |
          docker build -t edham-logistics-api .
          docker tag edham-logistics-api:latest ${{ secrets.DOCKER_REGISTRY }}/edham-logistics-api:latest
      
      - name: Push to registry
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push ${{ secrets.DOCKER_REGISTRY }}/edham-logistics-api:latest
      
      - name: Deploy to production
        run: |
          ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} "
            docker pull ${{ secrets.DOCKER_REGISTRY }}/edham-logistics-api:latest
            docker-compose down
            docker-compose up -d
          "
```

### **2. المراقبة والتسجيل**

#### **إعدادات Prometheus + Grafana**
```yaml
# monitoring/docker-compose.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus

  grafana:
    image: grafana/grafana
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana

volumes:
  prometheus_data:
  grafana_data:
```

### **3. النسخ الاحتياطي**

#### **Script النسخ الاحتياطي**
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/edham-logistics"

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup MongoDB
mongodump --uri="$MONGODB_URI" --out="$BACKUP_DIR/mongo_$DATE"

# Backup Redis
redis-cli --rdb "$BACKUP_DIR/redis_$DATE.rdb"

# Backup application files
tar -czf "$BACKUP_DIR/app_$DATE.tar.gz" /var/www/edham-logistics

# Upload to S3
aws s3 sync $BACKUP_DIR s3://edham-logistics-backups/$(date +%Y/%m/%d)/

# Clean old backups (keep last 30 days)
find $BACKUP_DIR -type f -mtime +30 -delete

echo "Backup completed: $DATE"
```

## 🔒 الأمان في الإنتاج

### **1. إعدادات SSL/TLS**

#### **Let's Encrypt Certificate**
```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Get certificate
sudo certbot --nginx -d api.edham-logistics.com -d edham-logistics.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

### **2. إعدادات جدار الحماية**

#### **UFW Rules**
```bash
# Enable firewall
sudo ufw enable

# Allow SSH
sudo ufw allow ssh

# Allow HTTP/HTTPS
sudo ufw allow 80
sudo ufw allow 443

# Allow application port (if needed)
sudo ufw allow 3000

# Check status
sudo ufw status
```

### **3. إعدادات الأمان**

#### **Security Headers**
```javascript
// middleware/security.js
const helmet = require('helmet');

module.exports = helmet({
    contentSecurityPolicy: {
        directives: {
            defaultSrc: ["'self'"],
            styleSrc: ["'self'", "'unsafe-inline'"],
            scriptSrc: ["'self'"],
            imgSrc: ["'self'", "data:", "https:"],
            connectSrc: ["'self'", "https://api.edham-logistics.com"]
        }
    },
    hsts: {
        maxAge: 31536000,
        includeSubDomains: true,
        preload: true
    }
});
```

## 📊 المراقبة والتحليل

### **1. Application Performance Monitoring**

#### **New Relic Integration**
```javascript
// monitoring/newrelic.js
const newrelic = require('newrelic');

newrelic.configure({
    app_name: 'Edham Logistics API',
    license_key: process.env.NEW_RELIC_LICENSE_KEY,
    logging: {
        level: 'info'
    }
});
```

### **2. Error Tracking**

#### **Sentry Integration**
```javascript
// monitoring/sentry.js
const Sentry = require('@sentry/node');

Sentry.init({
    dsn: process.env.SENTRY_DSN,
    environment: process.env.NODE_ENV,
    tracesSampleRate: 1.0,
});
```

## 🚀 التحقق من النشر

### **1. Health Check Endpoint**
```javascript
// routes/health.js
app.get('/health', async (req, res) => {
    try {
        // Check database
        await mongoose.connection.db.admin().ping();
        
        // Check Redis
        await redis.ping();
        
        res.status(200).json({
            status: 'healthy',
            timestamp: new Date().toISOString(),
            version: process.env.npm_package_version,
            uptime: process.uptime()
        });
    } catch (error) {
        res.status(503).json({
            status: 'unhealthy',
            error: error.message
        });
    }
});
```

### **2. Post-Deployment Tests**
```bash
#!/bin/bash
# post-deploy-tests.sh

# Test API health
curl -f https://api.edham-logistics.com/health || exit 1

# Test database connection
curl -f https://api.edham-logistics.com/api/test/db || exit 1

# Test authentication
curl -f -X POST https://api.edham-logistics.com/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"test"}' || exit 1

echo "All tests passed!"
```

---

**🎉 تهانينا! الآن تطبيق إدهام اللوجستي منشور وجاهز للاستخدام الإنتاجي!**
