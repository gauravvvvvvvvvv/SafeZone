package com.example.safezone.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.safezone.R

/**
 * A custom view that displays an interactive safety rating bar
 * with multiple safety icons that can be selected by the user
 */
class SafetyRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint for drawing text
    private val paintText = Paint().apply {
        isAntiAlias = true
        textSize = 42f
        color = Color.BLACK
    }

    // Icons for different safety levels (now using stars)
    private lateinit var starFilled: Drawable
    private lateinit var starOutline: Drawable
    
    // Rating properties
    private var selectedRating = 0 // 0 = none, 1 = danger, 2 = caution, 3 = safe
    private var iconSize = 160 // make stars much larger
    private var iconSpacing = 48 // increase spacing between stars
    private var ratingListener: ((Int) -> Unit)? = null

    init {
        // Load star drawables
        starFilled = ContextCompat.getDrawable(context, android.R.drawable.btn_star_big_on)!!
        starOutline = ContextCompat.getDrawable(context, android.R.drawable.btn_star_big_off)!!
        isClickable = true
        isFocusable = true

        // Extract custom attributes if provided
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.SafetyRatingView, defStyleAttr, 0
            )
            
            try {
                // Get custom attributes here if needed
            } finally {
                typedArray.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Calculate the ideal size based on the icon size and spacing
        val totalWidth = (iconSize * 3) + (iconSpacing * 2) // 3 icons with spacing between
        val totalHeight = iconSize + 60 // icon height + text height
        
        // Apply the calculated dimensions, respecting the constraints
        val finalWidth = resolveSize(totalWidth, widthMeasureSpec)
        val finalHeight = resolveSize(totalHeight, heightMeasureSpec)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2 - iconSize / 2 - 40 // move stars up for text
        val startX = (width - ((iconSize * 3) + (iconSpacing * 2))) / 2
        // Draw 3 stars for rating
        for (i in 0..2) {
            val icon = if (selectedRating > i) starFilled else starOutline
            val x = startX + i * (iconSize + iconSpacing)
            icon.setBounds(x, centerY, x + iconSize, centerY + iconSize)
            icon.alpha = 255
            icon.draw(canvas)
        }
        // Draw labels below stars, centered
        val textY = centerY + iconSize + 60f
        val labelWidth = paintText.measureText("Safe")
        canvas.drawText("Danger", startX + (iconSize / 2f) - labelWidth / 2, textY, paintText)
        canvas.drawText("Caution", startX + iconSize + iconSpacing + (iconSize / 2f) - labelWidth / 2, textY, paintText)
        canvas.drawText("Safe", startX + 2 * (iconSize + iconSpacing) + (iconSize / 2f) - labelWidth / 2, textY, paintText)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val x = event.x.toInt()
            val centerY = height / 2 - iconSize / 2 - 40
            val startX = (width - ((iconSize * 3) + (iconSpacing * 2))) / 2
            // Allow clicks anywhere vertically in the view, not just the icon row
            for (i in 0..2) {
                val iconStart = startX + i * (iconSize + iconSpacing)
                val iconEnd = iconStart + iconSize
                if (x >= iconStart && x <= iconEnd) {
                    selectedRating = i + 1
                    invalidate()
                    ratingListener?.invoke(selectedRating)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Set the current safety rating
     */
    fun setRating(rating: Int) {
        if (rating in 0..3) {
            selectedRating = rating
            invalidate()
        }
    }

    /**
     * Get the current safety rating
     */
    fun getRating(): Int = selectedRating

    /**
     * Set a listener for rating changes
     */
    fun setOnRatingChangeListener(listener: (Int) -> Unit) {
        ratingListener = listener
    }
}
