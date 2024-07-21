import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicviewer.R
import com.example.musicviewer.pdfClass

class PDFAdapter(
    private val pdfList: MutableList<pdfClass>,
    private val itemClickListener: (pdfClass) -> Unit
) : RecyclerView.Adapter<PDFAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pdf_item, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind(pdfList[position])
    }

    override fun getItemCount(): Int = pdfList.size

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pdfTitle: TextView = itemView.findViewById(R.id.pdf_name)

        fun bind(pdf: pdfClass) {
            pdfTitle.text = pdf.name // Bind the title to the TextView
            itemView.setOnClickListener {
                itemClickListener(pdf)
            }
        }
    }
}
