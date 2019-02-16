package ml.bublik.cz.firebasemltest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_amount_ocr.*
import kotlinx.android.synthetic.main.activity_receipt_ocr.*
import ml.bublik.cz.firebasemltest.R
import ml.bublik.cz.firebasemltest.activity.LiveAmountOcrActivity.Companion.RECOGNIZED_TEXT_BUNDLE_KEY
import org.jetbrains.anko.AnkoLogger

class AmountOcrActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        const val PICK_OCR_AMOUNT_CODE = 1
        private const val TAG = "AmountOcrActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amount_ocr)
        setSupportActionBar(toolbar)

        cameraAmountOcrActivationButton.setOnClickListener {
            val intent = Intent(this, LiveAmountOcrActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent, PICK_OCR_AMOUNT_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_OCR_AMOUNT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    Log.i(TAG, "Do we have data? $it")
                    val stringFromRecognition = it.getStringExtra(RECOGNIZED_TEXT_BUNDLE_KEY)
                    amountOcrEditText.setText(stringFromRecognition)
                }
            }
        }
    }
}