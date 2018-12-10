package ml.bublik.cz.firebasemltest.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.text_picker_fragment.*
import ml.bublik.cz.firebasemltest.R
import ml.bublik.cz.firebasemltest.activity.ReceiptOcrActivity
import ml.bublik.cz.firebasemltest.activity.TypeofData
import ml.bublik.cz.firebasemltest.model.DetectedTextLine
import ml.bublik.cz.firebasemltest.model.LinesAdapter

class TextPicker : Fragment() {

    companion object {
        fun newInstance(detectedLines: List<DetectedTextLine>, typeOfData: TypeofData): TextPicker {
            val thisFragment = TextPicker()
            thisFragment.detectedLines = detectedLines
            thisFragment.typeOfData = typeOfData

            return thisFragment
        }
    }

    private lateinit var detectedLines: List<DetectedTextLine>
    private lateinit var typeOfData: TypeofData
    private lateinit var receiptOcrActivity: ReceiptOcrActivity

    private lateinit var linesAdapter: LinesAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        receiptOcrActivity = context as ReceiptOcrActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.text_picker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelButton.setOnClickListener {
            receiptOcrActivity.closeThePickerFragment()
        }
        okButton.setOnClickListener {
            receiptOcrActivity.setTextByTypeOfData(linesAdapter.detectedTextLines, typeOfData)
            receiptOcrActivity.closeThePickerFragment()
        }
        rvDetectedLinesRecycler.layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        super.onResume()
        refreshRecyclerData()
    }

    private fun refreshRecyclerData() {
        linesAdapter = LinesAdapter(detectedLines)
        rvDetectedLinesRecycler.adapter = linesAdapter
    }
}