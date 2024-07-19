import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicviewer.R
import com.example.musicviewer.pdfClass

class PDFAdapter(private val context: Context, private val pdfList: List<pdfClass>) :
    RecyclerView.Adapter<PDFAdapter.PDFViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pdf_item, parent, false)
        return PDFViewHolder(view)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val pdfModel = pdfList[position]
        holder.pdfName.text = pdfModel.name

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(pdfModel.url), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    class PDFViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfName: TextView = itemView.findViewById(R.id.pdf_name)
    }
}