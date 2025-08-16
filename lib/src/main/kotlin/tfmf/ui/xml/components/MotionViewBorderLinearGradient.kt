package com.microsoft.fluentmotion.ui.xml.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.microsoft.fluentmotion.ui.util.MotionUtil

/**
 * A custom View that draws a border with a linear gradient and rounded corners.
 *
 * @property rect The rectangle that defines the boundaries of the border.
 * @property cornerRadius The radius of the corners of the border.
 * @property linearGradient The colors to use for the linear gradient of the border.
 * @property paint The Paint object used to draw the border.
 *
 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 */
class MotionViewBorderLinearGradient(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val rect = RectF()
    private val cornerRadius = 100f
    lateinit var linearGradient: IntArray

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = MotionUtil.dpToPx(context, 1)
    }

    /**
     * Called when the size of this view is changed.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    /**
     * Called when the view should render its content.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            linearGradient,
            null,
            Shader.TileMode.CLAMP,
        )
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }
}