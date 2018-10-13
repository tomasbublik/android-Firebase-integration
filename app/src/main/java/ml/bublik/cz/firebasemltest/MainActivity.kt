package ml.bublik.cz.firebasemltest

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var imageDetection: ImageDetection
    private lateinit var photoHandler: PhotoHandler
    private lateinit var persistenceHandler: PersistenceHandler
    private var useCloud: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopProgressBar()
        verifyStoragePermissions(this)
        photoHandler = PhotoHandler(this)
        persistenceHandler = PersistenceHandler()

        cloudButton.setOnClickListener {
            startCamera()
        }
        deviceButton.setOnClickListener {
            startCamera()
            useCloud = false
        }
    }

    private fun startCamera() {
        resultTextView.text = ""
        recognitionMetaDataView.text = ""
        photoHandler.dispatchTakePictureIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PhotoHandler.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //Log.i(TAG, "What is the file name and size?: " + photoHandler.fileWithPhoto.path + " " + photoHandler.fileWithPhoto.length())
            //imageDetection = ImageDetection(Uri.fromFile(photoHandler.fileWithPhoto), this, useCloud)
            startProgressBar()
            imageDetection = ImageDetection(photoHandler.imageUri, this, useCloud)
            imageDetection.activateVision()
        }
    }

    fun persistAndShowResults(text: String, metaData: String) {
        metadataTextView.visibility = VISIBLE
        resultTextView.text = text
        recognitionMetaDataView.text = metaData

        //persistenceHandler.storeData(text, metaData)
    }

    fun startProgressBar() {
        progressBar.visibility = VISIBLE
    }

    fun stopProgressBar() {
        progressBar.visibility = INVISIBLE
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

    companion object {
        private const val TAG: String = "MainActivity"
    }
}
