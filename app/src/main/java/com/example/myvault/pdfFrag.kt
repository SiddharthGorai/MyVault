package com.example.myvault

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
    private lateinit var databaseRef: DatabaseReference


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAllPdf(view)
        val textView: TextView = view.findViewById(R.id.msgTxt)

        swipeRefreshLayout = view.findViewById(R.id.swipeRef)
        swipeRefreshLayout.setOnRefreshListener {
            textView.visibility = View.GONE
            getAllPdf(view)
            swipeRefreshLayout.isRefreshing = false
        }



    }

    private fun getAllPdf(view: View) {

        val progBar = view.findViewById<ProgressBar>(R.id.progBar)
        progBar.visibility = View.VISIBLE
        val PDFlist: ArrayList<userData> = ArrayList()

        val textView: TextView = view.findViewById(R.id.msgTxt)

        databaseRef = FirebaseDatabase.getInstance().getReference("PDFs").child(userID)
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   for(userSnapshot in snapshot.children){
                       var pdfName: String = userSnapshot.key.toString()
                       pdfName = pdfName.replace(",pdf","")
                       if(!PDFlist.contains(userData(null, null, pdfName, null,null))){
                       PDFlist.add(
                            userData(
                                null,
                                null,
                                pdfName,
                                null,
                                null
                            )
                       )
                   }

                   }
                   val layoutManager = LinearLayoutManager(context)
                   recyclerView = view.findViewById(R.id.pdfRec)
                   recyclerView.layoutManager = layoutManager
                   adapter = MyPDFAdapter(PDFlist)
                   recyclerView.adapter = adapter
                   progBar.visibility = View.INVISIBLE
                   textView.visibility = View.INVISIBLE

                   adapter.setOnItemClickListener(object : MyPDFAdapter.onItemClickListener{
                       override fun onItemClick(position: Int) {

                           val title: String = (recyclerView.findViewHolderForAdapterPosition(position)
                               ?.itemView?.findViewById<TextView>(R.id.pdfNameR))?.text.toString()

                           val bundle = Bundle()
                           bundle.putString("pdfName", title)
                           val intent = Intent (activity, viewPDF::class.java)
                           intent.putExtras(bundle)
                           startActivity(intent)

                       }
                   })

               }
                else{
                   textView.text = "No Pdf Found"
                   textView.visibility = View.VISIBLE
                   progBar.visibility = View.INVISIBLE
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }


}