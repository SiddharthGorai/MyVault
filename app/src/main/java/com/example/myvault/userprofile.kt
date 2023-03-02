package com.example.myvault

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.android.material.bottomnavigation.BottomNavigationView

class userprofile : AppCompatActivity() {

    private lateinit var signoutAlert: AlertDialog.Builder
    private lateinit var uploadPdfAlert: AlertDialog.Builder
    private lateinit var uploadImgAlert: AlertDialog.Builder
    private lateinit var database: DatabaseReference
    private var storageRef = Firebase.storage
    private lateinit var pdfUri: Uri
    private lateinit var RecyclerView: RecyclerView
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    var menu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.navbar), menu)
        this.menu = menu
        changeMenuBar()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> doThis()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doThis() {
        signoutAlert = AlertDialog.Builder(this)
        signoutAlert.setTitle("Alert!")
            .setMessage("Do you want to Sign out ?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                Firebase.auth.signOut()
                val intent = Intent(this, loginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }.show()

    }

    private fun changeMenuBar() {
        database = Firebase.database.reference
        database.child("User").child(userID).get().addOnSuccessListener {
            val uName = it.child("usrName").value.toString()
            val uEmail = it.child("usrEmail").value.toString()

            val dName = menu?.findItem(R.id.dname)
            val dEmail = menu?.findItem(R.id.demail)
            dEmail?.title = uEmail
            dName?.title = uName


        }.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)


            val storage = FirebaseStorage.getInstance()
            val pdfstorageRef: StorageReference = storage.reference.child("PDFs/$userID")
            val PDFlist: ArrayList<userData> = ArrayList()
            val listALLTask: Task<ListResult> = pdfstorageRef.listAll()

            val textView: TextView = findViewById(R.id.textView7)

            listALLTask.addOnCompleteListener { result ->
                val items: List<StorageReference> = result.result!!.items
                if (!items.isEmpty()) {
                    items.forEachIndexed { index, item ->
                        PDFlist.add(
                            userData(
                                null,
                                null,
                                item.name,
                                null
                            )
                        )

                    }
                    RecyclerView.adapter = MyAdapter(PDFlist)
                } else {
                    textView.text = "No Pdf Found"
                    textView.visibility = View.VISIBLE
                }


            }



            RecyclerView = findViewById<RecyclerView>(R.id.recyclerview)
            RecyclerView.layoutManager = LinearLayoutManager(this)

            val add = findViewById<FloatingActionButton>(R.id.add)
            val addPdf = findViewById<FloatingActionButton>(R.id.addPDF)
            val addImg = findViewById<FloatingActionButton>(R.id.addImg)
            var check: Boolean = true

            add.setOnClickListener {
                if (check) {
                    addPdf.show()
                    addImg.show()
                    check = false
                } else {
                    addPdf.hide()
                    addImg.hide()
                    check = true
                }

            }

            val galImg = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    val intent = Intent(this, editImg::class.java)
                    intent.putExtra("Data", it.toString())
                    startActivity(intent)
                }
            )

            addImg.setOnClickListener {
                galImg.launch("image/*")
            }

            addPdf.setOnClickListener(View.OnClickListener { view: View? ->
                selectPdf()
            }
            )

        }



        private fun selectPdf() {
            val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
            pdfIntent.type = "application/pdf"
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(pdfIntent, 12)
        }

        @Deprecated("Deprecated in Java")
        @SuppressLint("Range")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            // For loading PDF
            when (requestCode) {
                12 -> if (resultCode == RESULT_OK) {

                    pdfUri = data?.data!!
                    val uri: Uri = data?.data!!
                    val uriString: String = uri.toString()
                    var pdfName: String? = null

                    if (uriString.startsWith("content://")) {
                        var myCursor: Cursor? = null

                        try {
                            myCursor = applicationContext!!.contentResolver.query(
                                uri, null, null, null, null
                            )
                            if (myCursor != null && myCursor.moveToFirst()) {
                                pdfName =
                                    myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                uploadPdfAlert = AlertDialog.Builder(this)
                                uploadPdfAlert.setTitle("My Vault")
                                    .setMessage("Do you want to upload $pdfName to myvault ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes") { dialogInterface, it ->
                                        uploadPDF(uri, pdfName)

                                    }
                                    .setNegativeButton("No") { dialogInterface, it ->
                                        dialogInterface.cancel()
                                    }.show()
                            }
                        } finally {
                            myCursor?.close()
                        }
                    }
                }
            }
        }



        private fun uploadPDF(fUri: Uri, pName: String) {
            storageRef.getReference("PDFs").child(userID).child(pName)
                .putFile(fUri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }

        }


    }
