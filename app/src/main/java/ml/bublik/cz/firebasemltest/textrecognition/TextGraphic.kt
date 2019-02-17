package ml.bublik.cz.firebasemltest.textrecognition

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.firebase.ml.vision.text.FirebaseVisionText
import ml.bublik.cz.firebasemltest.common.GraphicOverlay

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic(
    overlay: GraphicOverlay,
    val text: FirebaseVisionText.Element?
) : GraphicOverlay.Graphic(overlay) {

    private val textPaint: Paint = Paint()
    var paintedText: String = ""

    init {
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas.  */
    override fun draw(canvas: Canvas, success: Boolean?) {
        if (text == null) {
            throw IllegalStateException("Attempting to draw a null text.")
        }

        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)

        // Renders the text inside teh box
        canvas.drawText(text.text, rect.left, rect.bottom, textPaint)
        paintedText = text.text
    }

    companion object {
        private const val TEXT_COLOR = Color.WHITE
        private const val TEXT_SIZE = 54.0f
    }
}