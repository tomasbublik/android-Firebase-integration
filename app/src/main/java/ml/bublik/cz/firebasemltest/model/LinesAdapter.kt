package ml.bublik.cz.firebasemltest.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import ml.bublik.cz.firebasemltest.R


class LinesAdapter(val detectedTextLines: List<DetectedTextLine>) : RecyclerView.Adapter<LinesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.text_line_item, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return detectedTextLines.size
    }

    override fun onBindViewHolder(holder: LinesAdapter.ViewHolder, position: Int) {
        //val detectedLine = detectedTextLines[position]
        //holder.itemText.text = detectedLine.text
        holder.bind(position)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var itemCheckBox: CheckBox = itemView.findViewById(R.id.item_checkBox)
        private var itemText: TextView = itemView.findViewById(R.id.item_text)

        fun bind(position: Int) {
            itemText.text = detectedTextLines[position].text
            itemCheckBox.isChecked = detectedTextLines[position].checked
            itemCheckBox.setOnClickListener {
                detectedTextLines[adapterPosition].checked = itemCheckBox.isChecked
            }
        }
    }
}