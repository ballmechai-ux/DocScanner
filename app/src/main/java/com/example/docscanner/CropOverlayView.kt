package com.example.docscanner
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
class CropOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private var cropRect = RectF()
    private val cornerSize = 40f
    private val minSize = 100f
    private var draggingCorner = -1
    private val paintOverlay = Paint().apply { color = Color.argb(120, 0, 0, 0) }
    private val paintBorder = Paint().apply {
        color = Color.parseColor("#4FC3F7"); strokeWidth = 3f
        style = Paint.Style.STROKE; isAntiAlias = true
    }
    private val paintCorner = Paint().apply {
        color = Color.parseColor("#4FC3F7"); strokeWidth = 6f
        style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; isAntiAlias = true
    }
    private val paintGrid = Paint().apply {
        color = Color.argb(80, 79, 195, 247); strokeWidth = 1f; style = Paint.Style.STROKE
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cropRect = RectF(w * 0.1f, h * 0.15f, w * 0.9f, h * 0.85f)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val path = Path().apply {
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
            addRect(cropRect, Path.Direction.CCW)
        }
        canvas.drawPath(path, paintOverlay)
        canvas.drawRect(cropRect, paintBorder)
        val tw = cropRect.width() / 3; val th = cropRect.height() / 3
        for (i in 1..2) {
            canvas.drawLine(cropRect.left + tw*i, cropRect.top, cropRect.left + tw*i, cropRect.bottom, paintGrid)
            canvas.drawLine(cropRect.left, cropRect.top + th*i, cropRect.right, cropRect.top + th*i, paintGrid)
        }
        val c = cornerSize
        canvas.drawLine(cropRect.left, cropRect.top+c, cropRect.left, cropRect.top, paintCorner)
        canvas.drawLine(cropRect.left, cropRect.top, cropRect.left+c, cropRect.top, paintCorner)
        canvas.drawLine(cropRect.right-c, cropRect.top, cropRect.right, cropRect.top, paintCorner)
        canvas.drawLine(cropRect.right, cropRect.top, cropRect.right, cropRect.top+c, paintCorner)
        canvas.drawLine(cropRect.left, cropRect.bottom-c, cropRect.left, cropRect.bottom, paintCorner)
        canvas.drawLine(cropRect.left, cropRect.bottom, cropRect.left+c, cropRect.bottom, paintCorner)
        canvas.drawLine(cropRect.right-c, cropRect.bottom, cropRect.right, cropRect.bottom, paintCorner)
        canvas.drawLine(cropRect.right, cropRect.bottom, cropRect.right, cropRect.bottom-c, paintCorner)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> draggingCorner = getTouchedCorner(event.x, event.y)
            MotionEvent.ACTION_MOVE -> { if (draggingCorner >= 0) { moveCorner(draggingCorner, event.x, event.y); invalidate() } }
            MotionEvent.ACTION_UP -> draggingCorner = -1
        }
        return true
    }
    private fun getTouchedCorner(x: Float, y: Float): Int {
        val t = 60f
        listOf(PointF(cropRect.left,cropRect.top), PointF(cropRect.right,cropRect.top),
            PointF(cropRect.left,cropRect.bottom), PointF(cropRect.right,cropRect.bottom))
            .forEachIndexed { i, p -> if (Math.hypot((x-p.x).toDouble(),(y-p.y).toDouble()) < t) return i }
        return -1
    }
    private fun moveCorner(corner: Int, x: Float, y: Float) {
        when (corner) {
            0 -> { if (cropRect.right-x > minSize) cropRect.left=x; if (cropRect.bottom-y > minSize) cropRect.top=y }
            1 -> { if (x-cropRect.left > minSize) cropRect.right=x; if (cropRect.bottom-y > minSize) cropRect.top=y }
            2 -> { if (cropRect.right-x > minSize) cropRect.left=x; if (y-cropRect.top > minSize) cropRect.bottom=y }
            3 -> { if (x-cropRect.left > minSize) cropRect.right=x; if (y-cropRect.top > minSize) cropRect.bottom=y }
        }
    }
    fun getCropRect(): RectF = cropRect
}
