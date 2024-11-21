package com.xbcad.xbcad7319_physiotherapyapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var lastX = 0f
    private var lastY = 0f


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        canvas?.drawColor(Color.TRANSPARENT) // Clear background
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the existing bitmap
        canvas.drawBitmap(bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888), 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                // Prevent parent from intercepting touch events
                parent.requestDisallowInterceptTouchEvent(true)
                return true // Touch event handled
            }
            MotionEvent.ACTION_MOVE -> {
                // Draw line from last to current point
                canvas?.drawLine(lastX, lastY, x, y, paint)
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_UP -> {
                // Finish the drawing line
                canvas?.drawLine(lastX, lastY, x, y, paint)
                // Allow parent to intercept touch events again
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        invalidate() // Redraw the view
        return true // Touch event handled
    }

    fun getSignatureBitmap(): Bitmap {
        return bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun clearSignature() {
        bitmap?.eraseColor(Color.TRANSPARENT) // Clear the bitmap
        invalidate() // Request redraw
    }
}
