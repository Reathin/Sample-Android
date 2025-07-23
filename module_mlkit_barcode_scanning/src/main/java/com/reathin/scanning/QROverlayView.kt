package com.reathin.scanning

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class QROverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boundingBoxes: List<RectF> = emptyList()
    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    fun updateBoundingBoxes(rects: List<Rect>, imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int, rotationDegrees: Int = 0) {
        boundingBoxes = rects.map { rect ->
            transformRect(rect, imageWidth, imageHeight, viewWidth, viewHeight, rotationDegrees)
        }
        invalidate()
    }

    private fun transformRect(rect: Rect, imageWidth: Int, imageHeight: Int, viewWidth: Int, viewHeight: Int, rotationDegrees: Int): RectF {
        return when (rotationDegrees) {
            0 -> {
                // 无旋转 - 使用统一缩放比例保持宽高比
                val scale = minOf(
                    viewWidth.toFloat() / imageWidth,
                    viewHeight.toFloat() / imageHeight
                )
                val offsetX = (viewWidth - imageWidth * scale) / 2
                val offsetY = (viewHeight - imageHeight * scale) / 2

                RectF(
                    rect.left * scale + offsetX,
                    rect.top * scale + offsetY,
                    rect.right * scale + offsetX,
                    rect.bottom * scale + offsetY
                )
            }
            90 -> {
                // 顺时针90度旋转 - 使用统一缩放比例
                val scale = minOf(
                    viewWidth.toFloat() / imageHeight,
                    viewHeight.toFloat() / imageWidth
                )
                val offsetX = (viewWidth - imageHeight * scale) / 2
                val offsetY = (viewHeight - imageWidth * scale) / 2

                RectF(
                    rect.top * scale + offsetX,
                    (imageWidth - rect.right) * scale + offsetY,
                    rect.bottom * scale + offsetX,
                    (imageWidth - rect.left) * scale + offsetY
                )
            }
            180 -> {
                // 180度旋转 - 使用统一缩放比例
                val scale = minOf(
                    viewWidth.toFloat() / imageWidth,
                    viewHeight.toFloat() / imageHeight
                )
                val offsetX = (viewWidth - imageWidth * scale) / 2
                val offsetY = (viewHeight - imageHeight * scale) / 2

                RectF(
                    (imageWidth - rect.right) * scale + offsetX,
                    (imageHeight - rect.bottom) * scale + offsetY,
                    (imageWidth - rect.left) * scale + offsetX,
                    (imageHeight - rect.top) * scale + offsetY
                )
            }
            270 -> {
                // 逆时针90度旋转 - 使用统一缩放比例
                val scale = minOf(
                    viewWidth.toFloat() / imageHeight,
                    viewHeight.toFloat() / imageWidth
                )
                val offsetX = (viewWidth - imageHeight * scale) / 2
                val offsetY = (viewHeight - imageWidth * scale) / 2

                RectF(
                    (imageHeight - rect.bottom) * scale + offsetX,
                    rect.left * scale + offsetY,
                    (imageHeight - rect.top) * scale + offsetX,
                    rect.right * scale + offsetY
                )
            }
            else -> {
                // 默认情况 - 使用统一缩放比例
                val scale = minOf(
                    viewWidth.toFloat() / imageWidth,
                    viewHeight.toFloat() / imageHeight
                )
                val offsetX = (viewWidth - imageWidth * scale) / 2
                val offsetY = (viewHeight - imageHeight * scale) / 2

                RectF(
                    rect.left * scale + offsetX,
                    rect.top * scale + offsetY,
                    rect.right * scale + offsetX,
                    rect.bottom * scale + offsetY
                )
            }
        }
    }

    fun clearBoundingBoxes() {
        boundingBoxes = emptyList()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        boundingBoxes.forEachIndexed { index, rect ->
            // 为不同的二维码使用不同颜色
            val colors = arrayOf(Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN)
            val currentColor = colors[index % colors.size]

            // 更新画笔颜色
            paint.color = currentColor

            // 绘制边框
            canvas.drawRect(rect, paint)

            // 绘制四个角的装饰线
            val cornerLength = 50f
            val cornerPaint = Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 12f
            }

            // 左上角
            canvas.drawLine(rect.left, rect.top,
                rect.left + cornerLength, rect.top, cornerPaint)
            canvas.drawLine(rect.left, rect.top,
                rect.left, rect.top + cornerLength, cornerPaint)

            // 右上角
            canvas.drawLine(rect.right, rect.top,
                rect.right - cornerLength, rect.top, cornerPaint)
            canvas.drawLine(rect.right, rect.top,
                rect.right, rect.top + cornerLength, cornerPaint)

            // 左下角
            canvas.drawLine(rect.left, rect.bottom,
                rect.left + cornerLength, rect.bottom, cornerPaint)
            canvas.drawLine(rect.left, rect.bottom,
                rect.left, rect.bottom - cornerLength, cornerPaint)

            // 右下角
            canvas.drawLine(rect.right, rect.bottom,
                rect.right - cornerLength, rect.bottom, cornerPaint)
            canvas.drawLine(rect.right, rect.bottom,
                rect.right, rect.bottom - cornerLength, cornerPaint)

            // 在边框旁边绘制序号
            val textPaint = Paint().apply {
                color = currentColor
                textSize = 40f
                style = Paint.Style.FILL
            }
            canvas.drawText("${index + 1}", rect.left + 10, rect.top - 10, textPaint)
        }
    }
}