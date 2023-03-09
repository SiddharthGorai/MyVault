package com.example.myvault

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class pdfFrag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private  lateinit var adapter: MyPDFAdapter
    private  lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment pdfFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            pdfFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAllPdf(view)
        val textView: TextView = view.findViewById(R.id.msgTxt)

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRef)
        swipeRefreshLayout.setOnRefreshListener {
            textView.visibility = View.GONE
            getAllPdf(view)
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun getAllPdf(view: View) {

        val progBar = view.findViewById<ProgressBar>(R.id.progBar)
        progBar.visibility = View.VISIBLE
        val storage = FirebaseStorage.getInstance()
        val pdfstorageRef: StorageReference = storage.reference.child("PDFs/$userID")
        val PDFlist: ArrayList<userData> = ArrayList()
        val listALLTask: Task<ListResult> = pdfstorageRef.listAll()

        val textView: TextView = view.findViewById(R.id.msgTxt)

        listALLTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            if (!items.isEmpty()) {

                items.forEachIndexed { index, item ->
                    if(item.name.contains(".pdf")){
                        PDFlist.add(
                            userData(
                                null,
                                null,
                                item.name,
                                null
                            )
                        )

                    }
                }
                val layoutManager = LinearLayoutManager(context)
                recyclerView = view.findViewById(R.id.pdfRec)
                recyclerView.layoutManager = layoutManager
                adapter = MyPDFAdapter(PDFlist)
                recyclerView.adapter   = adapter
                progBar.visibility = View.INVISIBLE

            } else {
                textView.text = "No Pdf Found"
                textView.visibility = View.VISIBLE
                progBar.visibility = View.INVISIBLE
            }

        }
    }



}