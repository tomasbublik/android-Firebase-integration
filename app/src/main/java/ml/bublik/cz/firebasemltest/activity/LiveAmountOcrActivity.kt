package ml.bublik.cz.firebasemltest.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_amount_live_preview.*
import ml.bublik.cz.firebasemltest.common.CameraSource
import ml.bublik.cz.firebasemltest.textrecognition.TextRecognitionProcessor
import java.io.IOException
import java.util.ArrayList

class LiveAmountOcrActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LiveAmountOcrActivity"
        const val RECOGNIZED_TEXT_BUNDLE_KEY = "text"
        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted: $permission")
                return true
            }
            Log.i(TAG, "Permission NOT granted: $permission")
            return false
        }

        private const val PERMISSION_REQUESTS = 1
    }

    private var cameraSource: CameraSource? = null

    private val requiredPermissions: Array<String?>
        get() {
            return try {
                val info = this.packageManager
                    .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
                val ps = info.requestedPermissions
                if (ps != null && ps.isNotEmpty()) {
                    ps
                } else {
                    arrayOfNulls(0)
                }
            } catch (e: Exception) {
                arrayOfNulls(0)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ml.bublik.cz.firebasemltest.R.layout.activity_amount_live_preview)

        if (allPermissionsGranted()) {
            createCameraSource()
            startCameraSource()
        } else {
            getRuntimePermissions()
        }

        confirmButton.setOnClickListener {
            Log.i(
                TAG, "What is the recognized value?: " + firePreview.recognizedText
            )
            val conData = Bundle()
            conData.putString(RECOGNIZED_TEXT_BUNDLE_KEY, firePreview.recognizedText)
            val intent = Intent()
            intent.putExtras(conData)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, fireFaceOverlay)
        }

        cameraSource?.setMachineLearningFrameProcessor(TextRecognitionProcessor())
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermissions) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        cameraSource.let {
            try {
                if (firePreview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (fireFaceOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                it?.let { it1 -> firePreview.start(it1, fireFaceOverlay) }
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                it?.release()
                cameraSource = null
            }

        }
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = ArrayList<String>()
        for (permission in requiredPermissions) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    allNeededPermissions.add(it)
                }
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }
}