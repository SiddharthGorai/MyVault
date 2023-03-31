package com.example.myvault

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.*
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class viewIMG : AppCompatActivity() {
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



        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)



        getSupportActionBar()?.setTitle(imgNAME)
        if (nameUrl !== null) {
            Glide.with(this)
                .load(nameUrl)
                .into(imgView)

            downBtn.setOnClickListener {
                downloadImg(nameUrl,imgNAME)
            }
        } else {
            imgView.setImageResource(R.drawable.baseline_image_24)
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    private fun downloadImg(nameUrl:String,imgNAME: String){

        val mProgDialog = ProgressDialog(this)
        mProgDialog.setTitle("My Vault")
        mProgDialog.setMessage("Downloading: $imgNAME")
        mProgDialog.setCancelable(false)
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgDialog.setIndeterminate(false)

        val storage_Directory = "/Download"
        val storageDirectory = getExternalStorageDirectory().toString() + storage_Directory + "/${imgNAME}"
        val file = File(getExternalStorageDirectory().toString() + storage_Directory)

        if(!file.exists()){
            file.mkdirs()
        }
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(nameUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept-Encoding","identity")
            connection.connect()
            if(connection.responseCode in 200 .. 299){
                val filesize = connection.contentLength
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(storageDirectory)

                var bytesCopied: Long = 0
                var buffer = ByteArray(1024)
                var bytes = inputStream.read(buffer)
                while(bytes >= 0){
                    bytesCopied += bytes
                    val downloadProgress = (bytesCopied.toFloat() / filesize.toFloat() * 100).toInt()
                    withContext(Dispatchers.Main){
                        mProgDialog.setProgress(downloadProgress)
                        mProgDialog.show()

                    }
                    outputStream.write(buffer,0,bytes)
                    bytes = inputStream.read(buffer)

                }
                outputStream.close()
                inputStream.close()
                MediaScannerConnection.scanFile(this@viewIMG, arrayOf(file.toString()),
                    null, null)
                withContext(Dispatchers.Main){
                    Toast.makeText(this@viewIMG, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                }
                mProgDialog.dismiss()
            }

    }

}
}
