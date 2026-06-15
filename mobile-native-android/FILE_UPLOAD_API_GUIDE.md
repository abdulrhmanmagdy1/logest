# دليل رفع الملفات من خلال الـ API (File Upload Guide) 📁

هذا الملف مخصص للمبرمجين الذين سيقومون بتطوير ميزة رفع الصور (السائق، المحاسب، الورشة).

## 1. مسار الرفع الرئيسي (Upload Endpoint)
`POST /api/v1/files/upload`

- **النوع:** `multipart/form-data`
- **البارامترات:**
  - `file`: الملف نفسه (Image/PDF).
  - `type`: نوع الملف (مثل `SHIPMENT_PROOF`, `OIL_CHANGE_RECEIPT`, `USER_PHOTO`).

---

## 2. كيفية الاستخدام في الأندرويد (Retrofit Example)
```kotlin
@Multipart
@POST("files/upload")
suspend fun uploadFile(
    @Part file: MultipartBody.Part,
    @Part("type") type: RequestBody
): Response<FileUploadResponse>
```

---

## 3. مسار حفظ الملفات على السيرفر
الملفات يتم تنظيمها برمجياً على السيرفر في المسار:
`root_upload_dir / {user_id} / {type} / filename.jpg`

- السيرفر يقوم تلقائياً بضغط الصور لتقليل الحجم.
- يتم توليد اسم فريد (UUID) لكل ملف لتجنب التكرار.

---

## 4. تحميل الملفات (Viewing/Downloading)
لجلب أي ملف تم رفعه سابقاً، استخدم المسار:
`GET /api/v1/files/view/{fileId}`

أو المسار المباشر إذا كنت تمتلك الاسم:
`GET /api/v1/files/download/{fileName}`

---
**تحذير أمني:** لا تقبل رفع ملفات بصيغة `.exe` أو `.sh`. الـ API يدعم فقط الصور والوثائق المستندية.
