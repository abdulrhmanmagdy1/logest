@echo off
echo ========================================
echo سكربت بناء تطبيق إدهام للأندرويد
echo ========================================
echo.

cd /d %~dp0

echo [1/5] تنظيف المشروع...
flutter clean
if %errorlevel% neq 0 (
    echo خطأ في تنظيف المشروع
    pause
    exit /b 1
)

echo [2/5] تحديث المكتبات...
flutter pub get
if %errorlevel% neq 0 (
    echo خطأ في تحديث المكتبات
    pause
    exit /b 1
)

echo [3/5] بناء APK للإصدار...
flutter build apk --release
if %errorlevel% neq 0 (
    echo خطأ في بناء APK
    echo.
    echo جرب الحلول التالية:
    echo 1. تحديث Flutter: flutter upgrade
    echo 2. تحديث Gradle في android/build.gradle
    echo 3. استخدام Flutter version أقدم
    pause
    exit /b 1
)

echo [4/5] البناء اكتمل بنجاح!
echo.
echo موقع ملف APK:
echo build\app\outputs\flutter-apk\app-release.apk
echo.

echo [5/5] فتح المجلد...
explorer build\app\outputs\flutter-apk

echo ========================================
echo تم بناء التطبيق بنجاح!
echo ========================================
pause
