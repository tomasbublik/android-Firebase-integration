package ml.bublik.cz.firebasemltest.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*
import ml.bublik.cz.firebasemltest.R
import ml.bublik.cz.firebasemltest.persistence.PersistenceHandler


class MainActivity : BaseActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    private lateinit var persistenceHandler: PersistenceHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopProgressBar()
        verifyStoragePermissions(this)
        persistenceHandler = PersistenceHandler()

        cloudButton.setOnClickListener {
            startCamera()
            useCloud = true
        }
        deviceButton.setOnClickListener {
            startCamera()
            useCloud = false
        }
        receiptOcrButton.setOnClickListener {
            val intent = Intent(this, ReceiptOcrActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    override fun startCamera() {
        resultTextView.text = ""
        recognitionMetaDataView.text = ""
        photoHandler.dispatchTakePictureIntent()
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun persistAndShowResults(text: String, metaData: String, firebaseVisionText: FirebaseVisionText) {
        metadataTextView.visibility = View.VISIBLE
        resultTextView.text = text
        recognitionMetaDataView.text = metaData

        //persistenceHandler.storeData(text, metaData)
    }

    override fun stopProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
}