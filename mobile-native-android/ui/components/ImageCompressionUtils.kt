// ============================================
// 🚀 Edham Logistics - Image Compression Utils
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.ui.components

import android.content.Context
import android.graphics.*
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Image Compression Utils - أدوات ضغط الصور
// ============================================
 * ضغط الصور قبل رفعها للسحابة لتوفير الباندويث
 */

@Singleton
class ImageCompressionUtils @Inject constructor(
    private val context: Context
) {
    
    /**
     * ضغط الصورة للشبكة
     */
    suspend fun compressImageForNetwork(
        imageFile: File,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 85,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val compressedBytes = compressImage(imageFile, maxWidth, maxHeight, quality, format)
            Result.Success(compressedBytes)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * ضغط الصورة وحفظها
     */
    suspend fun compressAndSaveImage(
        imageFile: File,
        outputFile: File,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 85,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val compressedBytes = compressImage(imageFile, maxWidth, maxHeight, quality, format)
            
            FileOutputStream(outputFile).use { out ->
                out.write(compressedBytes)
            }
            
            Result.Success(outputFile)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * ضغط الصورة الأساسي
     */
    private fun compressImage(
        imageFile: File,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        format: Bitmap.CompressFormat
    ): ByteArray {
        // الحصول على أبعاد الصورة الأصلية
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight
        
        // حساب نسبة الضغط
        val scaleFactor = calculateScaleFactor(originalWidth, originalHeight, maxWidth, maxHeight)
        
        // تحميل الصورة المقاسة
        val scaledBitmap = loadScaledBitmap(imageFile, scaleFactor)
        
        // معالجة دوران الصورة
        val orientedBitmap = handleImageOrientation(scaledBitmap, imageFile)
        
        // ضغط الصورة
        val outputStream = ByteArrayOutputStream()
        orientedBitmap.compress(format, quality, outputStream)
        
        // تنظيف الذاكرة
        if (scaledBitmap != orientedBitmap) {
            scaledBitmap.recycle()
        }
        orientedBitmap.recycle()
        
        return outputStream.toByteArray()
    }
    
    /**
     * حساب نسبة الضغط
     */
    private fun calculateScaleFactor(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Float {
        val widthRatio = originalWidth.toFloat() / maxWidth.toFloat()
        val heightRatio = originalHeight.toFloat() / maxHeight.toFloat()
        
        return maxOf(widthRatio, heightRatio, 1.0f)
    }
    
    /**
     * تحميل الصورة المقاسة
     */
    private fun loadScaledBitmap(imageFile: File, scaleFactor: Float): Bitmap {
        val options = BitmapFactory.Options().apply {
            inSampleSize = scaleFactor.toInt()
            inPreferredConfig = Bitmap.Config.RGB_565 // تقليل استهلاك الذاكرة
        }
        
        return BitmapFactory.decodeFile(imageFile.absolutePath, options)
            ?: throw IllegalArgumentException("Failed to decode image")
    }
    
    /**
     * معالجة دوران الصورة
     */
    private fun handleImageOrientation(bitmap: Bitmap, imageFile: File): Bitmap {
        return try {
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            }
            
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            
            rotatedBitmap
        } catch (e: Exception) {
            // في حال فشل معالجة الدوران، نرجع الصورة الأصلية
            bitmap
        }
    }
    
    /**
     * الحصول على معلومات الصورة
     */
    suspend fun getImageInfo(imageFile: File): ImageInfo = withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        
        val fileSize = imageFile.length()
        val fileSizeMB = fileSize / (1024.0 * 1024.0)
        
        ImageInfo(
            width = options.outWidth,
            height = options.outHeight,
            fileSizeBytes = fileSize,
            fileSizeMB = fileSizeMB,
            mimeType = getMimeType(imageFile)
        )
    }
    
    /**
     * الحصول على نوع الملف
     */
    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/*"
        }
    }
    
    /**
     * ضغط دفعة من الصور
     */
    suspend fun compressBatch(
        imageFiles: List<File>,
        outputDir: File,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 85,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP
    ): Result<List<CompressedImageResult>> = withContext(Dispatchers.IO) {
        try {
            val results = mutableListOf<CompressedImageResult>()
            
            for (imageFile in imageFiles) {
                val originalInfo = getImageInfo(imageFile)
                val outputFile = File(outputDir, "compressed_${imageFile.name}")
                
                val compressResult = compressAndSaveImage(
                    imageFile, outputFile, maxWidth, maxHeight, quality, format
                )
                
                when (compressResult) {
                    is Result.Success -> {
                        val compressedInfo = getImageInfo(outputFile)
                        val compressionRatio = (originalInfo.fileSizeMB - compressedInfo.fileSizeMB) / originalInfo.fileSizeMB * 100
                        
                        results.add(
                            CompressedImageResult(
                                originalFile = imageFile,
                                compressedFile = outputFile,
                                originalInfo = originalInfo,
                                compressedInfo = compressedInfo,
                                compressionRatio = compressionRatio
                            )
                        )
                    }
                    is Result.Error -> {
                        results.add(
                            CompressedImageResult(
                                originalFile = imageFile,
                                compressedFile = null,
                                originalInfo = originalInfo,
                                compressedInfo = null,
                                compressionRatio = 0.0,
                                error = compressResult.exception
                            )
                        )
                    }
                }
            }
            
            Result.Success(results)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * ============================================
// Data Classes
// ============================================
 */

data class ImageInfo(
    val width: Int,
    val height: Int,
    val fileSizeBytes: Long,
    val fileSizeMB: Double,
    val mimeType: String
)

data class CompressedImageResult(
    val originalFile: File,
    val compressedFile: File?,
    val originalInfo: ImageInfo,
    val compressedInfo: ImageInfo?,
    val compressionRatio: Double,
    val error: Exception? = null
) {
    val isSuccessful: Boolean
        get() = compressedFile != null && error == null
    
    val sizeReductionKB: Long
        get() = if (compressedInfo != null) {
            (originalInfo.fileSizeBytes - compressedInfo.fileSizeBytes) / 1024
        } else 0L
}
