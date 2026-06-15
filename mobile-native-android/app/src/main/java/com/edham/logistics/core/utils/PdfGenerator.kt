package com.edham.logistics.core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.edham.logistics.core.network.api.StatementOfAccount
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfGenerator {

    fun generateSoA(context: Context, soa: StatementOfAccount) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Branding & Metadata
        paint.color = Color.parseColor("#104C64") // Edham Teal
        paint.textSize = 24f
        canvas.drawText("Edham Logistics — كشف حساب رسمي", 40f, 50f, paint)
        
        paint.color = Color.GRAY
        paint.textSize = 10f
        val timeStamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        canvas.drawText("تاريخ الإصدار: $timeStamp", 40f, 70f, paint)
        canvas.drawText("الرقم المرجعي: ${java.util.UUID.randomUUID().toString().take(8).uppercase()}", 40f, 85f, paint)

        paint.color = Color.BLACK
        paint.textSize = 14f
        canvas.drawText("العميل: ${soa.clientName}", 40f, 100f, paint)
        canvas.drawText("إجمالي الفواتير: ${soa.totalInvoiced}", 40f, 120f, paint)
        canvas.drawText("المبلغ المسدد: ${soa.totalPaid}", 40f, 140f, paint)
        canvas.drawText("المتبقي: ${soa.remaining}", 40f, 160f, paint)

        // Table Header
        paint.style = Paint.Style.STROKE
        canvas.drawRect(40f, 200f, 550f, 230f, paint)
        paint.style = Paint.Style.FILL
        canvas.drawText("التاريخ", 50f, 220f, paint)
        canvas.drawText("البيان", 150f, 220f, paint)
        canvas.drawText("المبلغ", 450f, 220f, paint)

        var yPos = 250f
        soa.entries.forEach { entry ->
            canvas.drawText(entry.date, 50f, yPos, paint)
            canvas.drawText(entry.id, 150f, yPos, paint)
            canvas.drawText(entry.amount.toString(), 450f, yPos, paint)
            yPos += 30f
        }

        pdfDocument.finishPage(page)

        val fileName = "SoA_${soa.clientId}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "تم حفظ الملف في: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "فشل إنشاء الملف", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}
