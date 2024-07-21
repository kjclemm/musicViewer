import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicviewer.PdfViewActivity
import com.example.musicviewer.R
import com.example.musicviewer.pdfClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfAdapter: PDFAdapter
    private lateinit var pdfList: MutableList<pdfClass>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        pdfList = mutableListOf()
        pdfAdapter = PDFAdapter(pdfList) { pdf ->
            val intent = Intent(activity, PdfViewActivity::class.java).apply {
                putExtra("pdfUrl", pdf.url)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = pdfAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pdfModel = dataSnapshot.getValue(pdfClass::class.java)
                    pdfModel?.let {
                        pdfList.add(it)
                    }
                }
                pdfAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
