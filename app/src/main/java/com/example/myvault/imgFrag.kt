package com.example.myvault

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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [imgFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class imgFrag : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: MyIMGAdapter
    private lateinit var recyclerView: RecyclerView
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var databaseRefImg: DatabaseReference


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
        return inflater.inflate(R.layout.fragment_img, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment imgFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            imgFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAllimg(view)
        val textView: TextView = view.findViewById(R.id.msgTxtI)

        val swipeRefreshLayoutt = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefImg)
        swipeRefreshLayoutt.setOnRefreshListener {
            textView.visibility = View.GONE
            getAllimg(view)
            swipeRefreshLayoutt.isRefreshing = false
        }


    }

    private fun getAllimg(view: View) {

        val progBar = view.findViewById<ProgressBar>(R.id.progBarImg)
        progBar.visibility = View.VISIBLE

        val storage = FirebaseStorage.getInstance()
//        val imgstorageRef: StorageReference = storage.reference.child("IMGs/$userID")
        val IMGlist: ArrayList<userData> = ArrayList()
//        val listALLTask: Task<ListResult> = imgstorageRef.listAll()

        val textView: TextView = view.findViewById(R.id.msgTxtI)

        databaseRefImg = FirebaseDatabase.getInstance().getReference("IMGs").child(userID)
        databaseRefImg.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        var imgName: String = userSnapshot.key.toString()
//                        imgName = imgName.replace(",",".")
                        if(!IMGlist.contains(userData(null, null, null, imgName))){
                            IMGlist.add(
                                userData(
                                    null,
                                    null,
                                    null,
                                    imgName
                                )
                            )
                        }
                        val layoutManager = LinearLayoutManager(context)
                        recyclerView = view.findViewById(R.id.imgRec)
                        recyclerView.layoutManager = layoutManager
                        adapter = MyIMGAdapter(IMGlist)
                        recyclerView.adapter = adapter
                        progBar.visibility = View.INVISIBLE
                        textView.visibility = View.INVISIBLE

                    }
                }
                else{
                    textView.text = "No Image Found"
                    textView.visibility = View.VISIBLE
                    progBar.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

//        listALLTask.addOnCompleteListener { result ->
//            val items: List<StorageReference> = result.result!!.items
//            if (!items.isEmpty()) {
//
//                items.forEachIndexed { index, item ->
//                    if (item.name.contains(".jpg") || item.name.contains(".png")
//                        || item.name.contains(".jpeg") || item.name.contains(".bmp")
//                    ) {
//                        IMGlist.add(
//                            userData(
//                                null,
//                                null,
//                                null,
//                                item.name
//                            )
//                        )
//
//                    }
//
//                }
//
//                val layoutManager = LinearLayoutManager(context)
//                recyclerView = view.findViewById(R.id.imgRec)
//                recyclerView.layoutManager = layoutManager
//                adapter = MyIMGAdapter(IMGlist)
//                recyclerView.adapter = adapter
//                progBar.visibility = View.INVISIBLE
//
//            } else {
//                textView.text = "No Img Found"
//                textView.visibility = View.VISIBLE
//                progBar.visibility = View.INVISIBLE
//            }
//
//        }

    }
}
