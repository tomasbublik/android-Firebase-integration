package ml.bublik.cz.firebasemltest

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.util.*


class PersistenceHandler {

    companion object {
        private const val TAG = "PersistenceHandler"
        private const val RECOGNITION_COLLECTION_NAME = "recognitions"
    }

    private lateinit var db: FirebaseFirestore

    /*private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        db.firestoreSettings = settings

        listCollection()
    }*/

    private fun listCollection() {
        initIfNecessary()
        db.collection(RECOGNITION_COLLECTION_NAME)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
    }

    fun storeData(ocrText: String, metaData: String) {
        initIfNecessary()
        val recognition = HashMap<String, Any>()
        recognition["ocrText"] = ocrText
        recognition["metaData"] = metaData
        recognition["date"] = Date()

        // Add a new document with a generated ID
        db.collection(RECOGNITION_COLLECTION_NAME)
                .add(recognition)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "RecognitionSnapshot added with ID: " + documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
    }

    private fun initIfNecessary() {
        if (db == null) {
            val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
            db.firestoreSettings = settings
            db = FirebaseFirestore.getInstance()
        }
    }
}