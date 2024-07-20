import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicviewer.R
import com.example.musicviewer.pdfClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfAdapter: PDFAdapter
    private lateinit var pdfList: MutableList<pdfClass>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        pdfList = mutableListOf()
        pdfAdapter = PDFAdapter(requireContext(), pdfList)
        recyclerView.adapter = pdfAdapter


//        val testPDF = pdfClass("Test PDF","https://www.example.com/test.pdf")
//        pdfList.add(testPDF)
//        pdfAdapter.notifyDataSetChanged()


        // Initialize database reference with the correct path
        databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pdfModel = dataSnapshot.getValue(pdfClass::class.java)

                    if (pdfModel != null) {
                        pdfList.add(pdfModel)
                    }
                }
                pdfAdapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
        pdfAdapter.notifyDataSetChanged()

        return view
    }
}
