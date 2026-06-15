package com.edham.logistics.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.edham.logistics.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tactical Analog-style Temperature Gauge for the Driver Cockpit.
 */
class TemperatureGauge @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var temperature: Float = 0f
    private val minTemp = -30f
    private val maxTemp = 30f

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 10f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 48f
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    fun setTemperature(temp: Float) {
        this.temperature = temp.coerceIn(minTemp, maxTemp)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = (width.coerceAtMost(height) / 2) - 40f
        val centerX = width / 2
        val centerY = height / 2

        // Draw Background Arc
        arcPaint.color = Color.parseColor("#1AFFFFFF")
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 135f, 270f, false, arcPaint)

        // Draw Temperature Color Arc
        val sweepAngle = ((temperature - minTemp) / (maxTemp - minTemp)) * 270f
        arcPaint.color = when {
            temperature < -10f -> ContextCompat.getColor(context, R.color.status_info)
            temperature < 5f -> ContextCompat.getColor(context, R.color.status_success)
            temperature < 15f -> ContextCompat.getColor(context, R.color.status_warning)
            else -> ContextCompat.getColor(context, R.color.status_error)
        }
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 135f, sweepAngle, false, arcPaint)

        // Draw Needle
        val angle = (135f + sweepAngle) * (Math.PI / 180).toFloat()
        val needleLen = radius - 20f
        val needleX = centerX + needleLen * cos(angle.toDouble()).toFloat()
        val needleY = centerY + needleLen * sin(angle.toDouble()).toFloat()
        
        needlePaint.color = Color.WHITE
        canvas.drawLine(centerX, centerY, needleX, needleY, needlePaint)
        canvas.drawCircle(centerX, centerY, 15f, needlePaint)

        // Draw Text
        canvas.drawText("${temperature.toInt()}°C", centerX, centerY + radius / 2, textPaint)
    }
}
