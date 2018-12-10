package ml.bublik.cz.firebasemltest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.google.firebase.ml.vision.text.FirebaseVisionText
import ml.bublik.cz.firebasemltest.image.ImageDetection
import ml.bublik.cz.firebasemltest.image.PhotoHandler

abstract class BaseActivity : AppCompatActivity() {
    lateinit var photoHandler: PhotoHandler
    lateinit var imageDetection: ImageDetection
    var useCloud: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoHandler = PhotoHandler(this)
    }

    protected var mLastFragment: Fragment? = null

    abstract fun startCamera()

    abstract fun startProgressBar()

    abstract fun stopProgressBar()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PhotoHandler.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //Log.i(TAG, "What is the file name and size?: " + photoHandler.fileWithPhoto.path + " " + photoHandler.fileWithPhoto.length())
            //imageDetection = ImageDetection(Uri.fromFile(photoHandler.fileWithPhoto), this, useCloud)
            startProgressBar()
            imageDetection = ImageDetection(photoHandler.imageUri, this, useCloud)
            imageDetection.activateVision()
        }
    }

    abstract fun persistAndShowResults(text: String, metaData: String, firebaseVisionText: FirebaseVisionText)

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        mLastFragment = fragment
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    fun AppCompatActivity.removeFragmentIfExists(frameId: Int): Boolean {
        val fragment = supportFragmentManager.findFragmentById(frameId)
        return if (fragment != null) {
            supportFragmentManager.inTransaction { remove(fragment) }
            true
        } else false
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }
}