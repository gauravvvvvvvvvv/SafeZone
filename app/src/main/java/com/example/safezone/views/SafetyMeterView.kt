package com.example.safezone.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.safezone.R

/**
 * Custom view that displays a safety level meter
 * Shows a circular progress indicator with color coding based on the safety level
 */
class SafetyMeterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_SAFETY_LEVEL = 75 // Default safety level (out of 100)
        private const val START_ANGLE = 135f // Starting angle for the arc (degrees)
        private const val SWEEP_ANGLE = 270f // Total sweep angle for the arc (degrees)
    }

    // Paint objects for drawing
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Drawing bounds
    private val rectF = RectF()
    
    // Current safety level (0-100)
    private var safetyLevel = DEFAULT_SAFETY_LEVEL
    
    init {
        // Setup background paint
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = 30f
        backgroundPaint.color = context.getColor(R.color.divider)
        
        // Setup progress paint
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 30f
        updateProgressColor()
        
        // Setup text paint
        textPaint.color = context.getColor(R.color.primary_text)
        textPaint.textSize = 50f
        textPaint.textAlign = Paint.Align.CENTER
    }

    /**
     * Set the current safety level and redraw
     */
    fun setSafetyLevel(level: Int) {
        safetyLevel = level.coerceIn(0, 100)
        updateProgressColor()
        invalidate()
    }

    /**
     * Get the current safety level
     */
    fun getSafetyLevel(): Int = safetyLevel

    /**
     * Update the progress color based on the safety level
     */
    private fun updateProgressColor() {
        val color = when {
            safetyLevel < 30 -> context.getColor(R.color.danger)
            safetyLevel < 70 -> context.getColor(R.color.warning)
            else -> context.getColor(R.color.safe)
        }
        progressPaint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = Math.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate the bounds for the arcs
        val padding = 50f
        rectF.set(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the background arc
        canvas.drawArc(
            rectF,
            START_ANGLE,
            SWEEP_ANGLE,
            false,
            backgroundPaint
        )
        
        // Draw the progress arc
        val sweepAngle = SWEEP_ANGLE * safetyLevel / 100
        canvas.drawArc(
            rectF,
            START_ANGLE,
            sweepAngle,
            false,
            progressPaint
        )
        
        // Draw the text in the center
        val text = "$safetyLevel%"
        val xPos = width / 2f
        val yPos = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)
    }
}
