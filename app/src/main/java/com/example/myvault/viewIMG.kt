package com.example.myvault

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

class viewIMG : AppCompatActivity() {
    private lateinit var pbarr: ProgressBar
    var menu: Menu? = null
    private lateinit var delAlert: AlertDialog.Builder
    private lateinit var storage: FirebaseStorage
    private lateinit var dataBase: FirebaseDatabase
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.delete), menu)
        this.menu = menu
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.del -> delPDF()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun delPDF() {
        val bundleG = intent.extras
        val imgName = bundleG?.getString("imgName")
        delAlert = AlertDialog.Builder(this@viewIMG)
        delAlert.setTitle("My Vault")
            .setMessage("Do you want to delete $imgName ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialogInterface, it ->
                storage = FirebaseStorage.getInstance()
                dataBase = FirebaseDatabase.getInstance()
                val dataRef = dataBase.getReference("IMGs")
                    .child(userID).child(imgName!!.replace(".",","))
                val storeRef = storage.getReference("IMGs").child(userID).child(imgName)
                storeRef.delete().addOnSuccessListener {
                    dataRef.removeValue()
                    Toast.makeText(this,"Deleted Sucessfully",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,userprofile::class.java)
                    startActivity(intent)
                    finishAffinity()
                }.addOnFailureListener {
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                }

            }.setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }.show()


    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_img)

        val bundleG = intent.extras
        val nameUrl = bundleG?.get("imgUrl").toString()
        val imgNAME = bundleG?.get("imgName").toString()
        val imgView = findViewById<ImageView>(R.id.imageViewU)
        val downBtn = findViewById<FloatingActionButton>(R.id.downloadImgBtn)
        pbarr = findViewById(R.id.pBarr)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)


        getSupportActionBar()?.setTitle(imgNAME)
        if (nameUrl !== null) {
            Glide.with(this)
                .load(nameUrl)
                .into(imgView)
        } else {
            imgView.setImageResource(R.drawable.baseline_image_24)
        }

        downBtn.setOnClickListener {
            pbarr.visibility = View.VISIBLE
            downloadImg(nameUrl,imgNAME)

        }

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun downloadImg(nameUrl: String,imgNAME: String) {
        val downloadManager = getSystemService(DownloadManager:: class.java)
        val request = DownloadManager.Request(nameUrl.toUri())
            .setMimeType("images/jpg")
            .addRequestHeader("Authorization","Bearer <token>")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,imgNAME)
        downloadManager.enqueue(request)
        pbarr.visibility = View.GONE
        Toast.makeText(this,"Saved to Downloads",Toast.LENGTH_SHORT).show()

    }
}