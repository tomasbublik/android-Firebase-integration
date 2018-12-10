package ml.bublik.cz.firebasemltest.activity

import android.os.Bundle
import android.view.View
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_receipt_ocr.*
import kotlinx.android.synthetic.main.content_receipt_ocr.*
import ml.bublik.cz.firebasemltest.R
import ml.bublik.cz.firebasemltest.activity.TypeofData.*
import ml.bublik.cz.firebasemltest.fragment.TextPicker.Companion.newInstance
import ml.bublik.cz.firebasemltest.model.DetectedTextLine
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class ReceiptOcrActivity : BaseActivity(), AnkoLogger {

    private val fakeListOfLines = listOf(
            DetectedTextLine("first line", false),
            DetectedTextLine("secionfšěšý line", false),
            DetectedTextLine("fjh čár áčí", false),
            DetectedTextLine("6d8f52 6dsfd", false),
            DetectedTextLine(" 2erfer6f41g4f6b ", false),
            DetectedTextLine("666333", false),
            DetectedTextLine("sdfefe8f49 19r84f9r841f941 9rf9f1d9sgvs  1df9g49gs", false),
            DetectedTextLine("first line", false),
            DetectedTextLine("secionfšěšý line", false),
            DetectedTextLine("fjh čár áčí", false),
            DetectedTextLine("6d8f52 6dsfd", false),
            DetectedTextLine(" 2erfer6f41g4f6b ", false),
            DetectedTextLine("666333", false),
            DetectedTextLine("sdfefe8f49 19r84f9r841f941 9rf9f1d9sgvs  1df9g49gs", false),
            DetectedTextLine("first line", false),
            DetectedTextLine("secionfšěšý line", false),
            DetectedTextLine("fjh čár áčí", false),
            DetectedTextLine("6d8f52 6dsfd", false),
            DetectedTextLine(" 2erfer6f41g4f6b ", false),
            DetectedTextLine("666333", false),
            DetectedTextLine("sdfefe8f49 19r84f9r841f941 9rf9f1d9sgvs  1df9g49gs", false)
    )

    private var detectedLinesOfText = fakeListOfLines

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_ocr)
        setSupportActionBar(toolbar)
        stopProgressBar()

        cameraActivationButton.setOnClickListener {
            startCamera()
            useCloud = false
        }
        fragmentTestButton.setOnClickListener {
            startThePickerFragment(detectedLinesOfText, NAME)
        }
        nameSearch.setOnClickListener { startThePickerFragment(detectedLinesOfText, NAME) }
        addressSearch.setOnClickListener { startThePickerFragment(detectedLinesOfText, ADDRESS) }
        dateSearch.setOnClickListener { startThePickerFragment(detectedLinesOfText, DATE) }
        amountSearch.setOnClickListener { startThePickerFragment(detectedLinesOfText, AMOUNT) }

        nameSearch.inputType = 0x00000000
        nameSearch.setIconifiedByDefault(false)
        dateSearch.inputType = 0x00000000
        dateSearch.setIconifiedByDefault(false)
        amountSearch.inputType = 0x00000000
        amountSearch.setIconifiedByDefault(false)
        addressSearch.inputType = 0x00000000
        addressSearch.setIconifiedByDefault(false)
    }

    override fun startCamera() {
        photoHandler.dispatchTakePictureIntent()
    }

    override fun persistAndShowResults(text: String, metaData: String, firebaseVisionText: FirebaseVisionText) {
        info("Received the following text")
        if (!firebaseVisionText.textBlocks.isEmpty()) {
            detectedLinesOfText = ArrayList()
            for (block in firebaseVisionText.textBlocks) {
                (detectedLinesOfText as ArrayList<DetectedTextLine>).add(DetectedTextLine(block.text, false))
            }
        }
    }

    override fun startProgressBar() {
        ocrProgressBar.visibility = View.INVISIBLE
    }

    override fun stopProgressBar() {
        ocrProgressBar.visibility = View.INVISIBLE
    }

    private fun startThePickerFragment(detectedLines: List<DetectedTextLine>, typeofData: TypeofData) {
        addFragment(newInstance(detectedLines, typeofData), R.id.fragmentContainer)
    }

    fun closeThePickerFragment(): Boolean {
        fragmentContainer.isClickable = false
        return removeFragmentIfExists(R.id.fragmentContainer)
    }

    fun setTextByTypeOfData(detectedLines: List<DetectedTextLine>, typeofData: TypeofData) {
        val returnedText = assembleChosenText(detectedLines, typeofData)
        when (typeofData) {
            NAME -> nameEditText.setText(returnedText)
            ADDRESS -> addressEditText.setText(returnedText)
            DATE -> addressEditText.setText(returnedText)
            AMOUNT -> amountEditText.setText(returnedText)
        }
    }

    private fun assembleChosenText(detectedLines: List<DetectedTextLine>, typeOfData: TypeofData): String {
        if (typeOfData != ADDRESS) {
            for (detectedLine in detectedLines) {
                if (detectedLine.checked) {
                    return detectedLine.text
                }
            }
        } else {
            val multiLineText = StringBuilder()
            for (detectedLine in detectedLines) {
                if (detectedLine.checked) {
                    multiLineText.append(detectedLine.text).append("\n\r")
                }
            }

            return multiLineText.toString()
        }

        return ""
    }
}

enum class TypeofData {
    NAME, ADDRESS, DATE, AMOUNT
}