package ml.bublik.cz.firebasemltest.image

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.CAMERA_SERVICE
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import ml.bublik.cz.firebasemltest.activity.BaseActivity

class ImageDetection(private val mCurrentPhotoPath: Uri?, private val activity: BaseActivity, private val useCloud: Boolean) {

    companion object {
        private const val TAG = "ImageDetection"
        private val ORIENTATIONS = SparseIntArray().apply {
            append(Surface.ROTATION_0, 90)
            append(Surface.ROTATION_90, 0)
            append(Surface.ROTATION_180, 270)
            append(Surface.ROTATION_270, 180)
        }
    }

    fun activateVision() {
        if (mCurrentPhotoPath == null) {
            return
        }
        val imageBitmap = narrowTheImage(MediaStore.Images.Media.getBitmap(activity.contentResolver, mCurrentPhotoPath), mCurrentPhotoPath)

        val visionImage = FirebaseVisionImage.fromBitmap(imageBitmap)
        val textRecognizer: FirebaseVisionTextRecognizer? = if (useCloud) {
            FirebaseVision.getInstance().cloudTextRecognizer
        } else {
            FirebaseVision.getInstance().onDeviceTextRecognizer
        }

        textRecognizer!!.processImage(visionImage)
                .addOnSuccessListener {
                    Log.i(TAG, "Successfully recognized")
                    showTheResult(it)
                }
                .addOnFailureListener {
                    Log.e(TAG, "What is the exception: " + it.localizedMessage)
                    sendFailure()
                }
    }

    private fun narrowTheImage(realImage: Bitmap, uri: Uri): Bitmap {
        val exif = ExifInterface(getRealPathFromURI(uri))

        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION))
        when {
            exif.getAttribute(ExifInterface.TAG_ORIENTATION) == "6" -> return rotate(realImage, 90)
            exif.getAttribute(ExifInterface.TAG_ORIENTATION) == "8" -> return rotate(realImage, 270)
            exif.getAttribute(ExifInterface.TAG_ORIENTATION) == "3" -> return rotate(realImage, 180)
        }
        return realImage
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        val mtx = Matrix()
        //       mtx.postRotate(degree);
        mtx.setRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        val cameraManager = activity.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                result = FirebaseVisionImageMetadata.ROTATION_0
                Log.e(TAG, "Bad rotation value: $rotationCompensation")
            }
        }
        return result
    }

    /**
     * Gets the id for the camera specified by the direction it is facing. Returns -1 if no such
     * camera was found.
     *
     * @param facing the desired camera (front-facing or rear-facing)
     */
    private fun getIdForRequestedCamera(facing: Int): Int {
        val cameraInfo = Camera.CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == facing) {
                return i
            }
        }
        return -1
    }

    private fun sendFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showTheResult(firebaseVisionText: FirebaseVisionText) {
        val resultText = firebaseVisionText.text
        Log.i(TAG, "What is the text: $resultText")

        val metaData = StringBuilder()
        metaData.append("Number of boxes: ${firebaseVisionText.textBlocks.size} \r\n")
        for (block in firebaseVisionText.textBlocks) {
            metaData.append("-- block -- \r\n")
            metaData.append("text: ${block.text} \r\n")
            metaData.append("confidence: ${block.confidence} \r\n")
            metaData.append("recognizedLanguages: ${block.recognizedLanguages} \r\n")
            metaData.append("cornerPoints: ${block.cornerPoints} \r\n")
            metaData.append("boundingBox: ${block.boundingBox} \r\n")

            metaData.append("Number of lines: ${block.lines.size} \r\n")
            for (line in block.lines) {
                metaData.append("---- line -- \r\n")
                metaData.append("text: ${line.text} \r\n")
                metaData.append("confidence: ${line.confidence} \r\n")
                metaData.append("recognizedLanguages: ${line.recognizedLanguages} \r\n")
                metaData.append("cornerPoints: ${line.cornerPoints} \r\n")
                metaData.append("boundingBox: ${line.boundingBox} \r\n")

                metaData.append("How many elements: ${line.elements.size} \r\n")
                for (element in line.elements) {
                    metaData.append("------ element -- \r\n")
                    metaData.append("text: ${element.text} \r\n")
                    metaData.append("confidence: ${element.confidence} \r\n")
                    metaData.append("recognizedLanguages: ${element.recognizedLanguages} \r\n")
                    metaData.append("cornerPoints: ${element.cornerPoints} \r\n")
                    metaData.append("boundingBox: ${element.boundingBox} \r\n")
                    metaData.append("------ end of element -- \r\n")
                }
                metaData.append("---- end of line -- \r\n")
            }
            metaData.append("-- end of block -- \r\n")
        }

        if (metaData.isNotEmpty()) {
            Log.i(TAG, "Meta data: $metaData")
        }

        activity.stopProgressBar(true)
        activity.persistAndShowResults(resultText, metaData.toString(), firebaseVisionText)
    }
}