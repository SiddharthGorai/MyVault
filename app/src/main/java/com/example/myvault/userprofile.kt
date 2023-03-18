package com.example.myvault

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.example.myvault.Adapter.MyTabAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.database.FirebaseDatabase

import java.util.*

class userprofile : AppCompatActivity() {

    private lateinit var signoutAlert: AlertDialog.Builder
    private lateinit var uploadAlert: AlertDialog.Builder
    private lateinit var database: DatabaseReference
    private var storageRef = Firebase.storage
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    var menu: Menu? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager



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
        getSupportActionBar()?.setElevation(0F)
        getSupportActionBar()?.setBackgroundDrawable(ColorDrawable(getColor(R.color.pink)))

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        tabLayout.addTab(tabLayout.newTab().setText("MY PDF"))
        tabLayout.addTab(tabLayout.newTab().setText("MY IMG"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MyTabAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: Tab?) {
            }
            override fun onTabReselected(tab: Tab?) {
            }

        })

            val add = findViewById<FloatingActionButton>(R.id.add)
            val addPdf = findViewById<FloatingActionButton>(R.id.addPDF)
            val addImg = findViewById<FloatingActionButton>(R.id.addImg)
            var check = true

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

            addImg.setOnClickListener {
                selectImg()
            }

        addPdf.setOnClickListener {
            selectPdf()
            }

    }


        @Deprecated("Deprecated in Java")
        @SuppressLint("Range")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            val mProgressDialog = ProgressDialog(this)
            mProgressDialog.setTitle("My Vault")
            mProgressDialog.setMessage("Uploading..")
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mProgressDialog.setCancelable(false)

            // For loading PDF
            when (requestCode) {
                12 -> if (resultCode == RESULT_OK) {

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
                                uploadAlert = AlertDialog.Builder(this)
                                uploadAlert.setTitle("My Vault")
                                    .setMessage("Do you want to upload $pdfName to myvault ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes") { dialogInterface, it ->
                                        mProgressDialog.show()
                                        uploadPDF(uri, pdfName,mProgressDialog)

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


                13 -> if (resultCode == RESULT_OK) {

                    val uri: Uri = data?.data!!
                    val uriString: String = uri.toString()
                    var imgName: String? = null

                    if (uriString.startsWith("content://")) {
                        var myCursor: Cursor? = null

                        try {
                            myCursor = applicationContext!!.contentResolver.query(
                                uri, null, null, null, null
                            )
                            if (myCursor != null && myCursor.moveToFirst()) {
                                imgName =
                                    myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                uploadAlert = AlertDialog.Builder(this)
                                uploadAlert.setTitle("My Vault")
                                    .setMessage("Do you want to upload $imgName to myvault ?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes") { dialogInterface, it ->
                                        mProgressDialog.show()
                                        uploadImg(uri, imgName,mProgressDialog)

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




    private fun selectImg() {
        val imgIntent = Intent(Intent.ACTION_GET_CONTENT)
        imgIntent.type = "image/*"
        imgIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(imgIntent, 13)
    }
    private fun selectPdf() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    private fun uploadPDF(fUri: Uri, pName: String, pD: ProgressDialog) {

            pD.setMax(100)
            storageRef.getReference("PDFs").child(userID).child(pName)
                .putFile(fUri)
                .addOnSuccessListener {task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            val mapPDF = mapOf(
                                "url" to it.toString()
                            )
                            val databaseReference = FirebaseDatabase.getInstance().getReference("PDFs")
                            databaseReference.child(userID).child(pName.replace(".",",")).setValue(mapPDF)
                        }
                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()

                    pD.dismiss()
                }
                .addOnProgressListener {
                    val progress: Long = (it.bytesTransferred /it.totalByteCount) * 100
                    pD.setMessage("Uploading..")
//                    pD.setMessage(progress.toString() + "%")
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    pD.dismiss()


                }

        }
    private fun uploadImg(fUri: Uri, iName: String,pDD: ProgressDialog) {
        storageRef.getReference("IMGs").child(userID).child(iName)
            .putFile(fUri)
            .addOnSuccessListener {task ->
                task.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {
                        val mapImg = mapOf(
                            "url" to it.toString()
                        )
                        val databaseReference = FirebaseDatabase.getInstance().getReference("IMGs")
                        databaseReference.child(userID).child(iName.replace(".",",")).setValue(mapImg)
                    }
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_LONG).show()
                pDD.dismiss()

            }
            .addOnProgressListener {
                val progress: Long = (it.bytesTransferred /it.totalByteCount) * 100
                pDD.setMessage("Uploading..")
//                pDD.setMessage(progress.toString() + "%")
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                pDD.dismiss()
            }

    }


    }
