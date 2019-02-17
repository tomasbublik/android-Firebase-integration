package ml.bublik.cz.firebasemltest.textrecognition

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import ml.bublik.cz.firebasemltest.common.GraphicOverlay

/**
 * Graphic to render the box for the amount. It also detects whether the recognized
 * text belongs to the rendered box.
 */
class DrawGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {

    companion object {
        private const val BOX_COLOR = Color.WHITE
        private const val BOX_SUCCESS_COLOR = Color.GREEN
        private const val STROKE_WIDTH = 4.0f
        private const val BOX_WIDTH = 0.6
        private const val BOX_HEIGHT = 0.1
    }

    private val rectPaint: Paint = Paint()

    private var leftBound: Float = 1F
    private var topBound: Float = 1F
    private var rightBound: Float = 1F
    private var bottomBound: Float = 1F

    init {
        rectPaint.color = BOX_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        postInvalidate()
    }

    /**
     * Detects whether the recognized text belongs to the rendered box
     */
    fun belongsToTheBox(graphic: GraphicOverlay.Graphic): Boolean {
        val text = (graphic as TextGraphic).text
        if (text != null) {
            if (allBoundsSet()) {
                val cornerPoints = text.boundingBox
                cornerPoints?.apply {
                    if (translateX(left.toFloat()) in leftBound..rightBound) {
                        if (translateY(top.toFloat()) in topBound..bottomBound) {
                            if (translateX(right.toFloat()) in leftBound..rightBound) {
                                if (translateY(bottom.toFloat()) in topBound..bottomBound) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }

        return false
    }

    private fun allBoundsSet() = leftBound != 1F && topBound != 1F && rightBound != 1F && bottomBound != 1F

    /**
     * Values box dimension are for 60% of width and 10% of height
     */
    override fun draw(canvas: Canvas, success: Boolean?) {
        val rect = RectF()
        canvas.apply {
            leftBound = (width / 2 - (width * (BOX_WIDTH / 2))).toFloat()
            topBound = (height / 2 - (height * (BOX_HEIGHT / 2))).toFloat()
            rightBound = (width / 2 + (width * (BOX_WIDTH / 2))).toFloat()
            bottomBound = (height / 2 + (height * (BOX_HEIGHT / 2))).toFloat()

            rect.left = leftBound
            rect.top = topBound
            rect.right = rightBound
            rect.bottom = bottomBound

            if (success == null || !success) {
                rectPaint.color = BOX_COLOR
            } else {
                rectPaint.color = BOX_SUCCESS_COLOR
            }

            canvas.drawRect(rect, rectPaint)
        }
    }
}