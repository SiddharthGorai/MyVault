package com.example.myvault

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class viewPDF : AppCompatActivity() {

    private lateinit var databaseRef1: DatabaseReference
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var delAlert: AlertDialog.Builder
    private lateinit var storage: FirebaseStorage
    private lateinit var dataBase: FirebaseDatabase


    var menu: Menu? = null


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
        val pdfName = bundleG?.getString("pdfName")
            delAlert = AlertDialog.Builder(this@viewPDF)
            delAlert.setTitle("My Vault")
                .setMessage("Do you want to delete $pdfName ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialogInterface, it ->
                    storage = FirebaseStorage.getInstance()
                    dataBase = FirebaseDatabase.getInstance()
                    val dataRef = dataBase.getReference("PDFs").child(userID).child((pdfName + ",pdf"))
                    val storeRef = storage.getReference("PDFs").child(userID).child((pdfName + ".pdf"))
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
        setContentView(R.layout.activity_view_pdf)

        val pdfView: PDFView = findViewById(R.id.pdfView)
        val bundleG = intent.extras
        val downBtn : FloatingActionButton = findViewById(R.id.downPDfBtn)
        val progressBar: ProgressBar = findViewById(R.id.progressBarViewpdf)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        progressBar.visibility = View.VISIBLE

        if (bundleG != null){
            val pdfName = bundleG.getString("pdfName")
            databaseRef1 = FirebaseDatabase.getInstance().getReference("PDFs").child(userID)
            databaseRef1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapshot in snapshot.children){
                            var pdfNameD: String = userSnapshot.key.toString()
                            pdfNameD = pdfNameD.replace(",pdf","")
                            if (pdfName == pdfNameD){
                                var urll = userSnapshot.getValue().toString()
                                urll = urll.replace("{url=","")
                                urll = urll.replace("}","")
                                RetrievePDFFromURL(pdfView,progressBar).execute(urll)
                                downBtn.setOnClickListener {
                                    downloadPDF(pdfNameD,urll)
                                }
                                getSupportActionBar()?.setTitle(pdfName)
                                break

                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )


    }



    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    private fun downloadPDF(pdfNAME: String,pdfUrl:String){
        val mProgDialog = ProgressDialog(this)
        mProgDialog.setTitle("My Vault")
        mProgDialog.setMessage("Downloading: $pdfNAME")
        mProgDialog.setCancelable(false)
        mProgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgDialog.setIndeterminate(false)

        val storage_Directory = "/Download"
        val pdfNamee = pdfNAME + ".pdf"
        val storageDirectory = getExternalStorageDirectory().toString() + storage_Directory + "/${pdfNamee}"
        val file = File(getExternalStorageDirectory().toString() + storage_Directory)

        if(!file.exists()){
            file.mkdirs()
        }
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(pdfUrl)
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
                mProgDialog.dismiss()

                MediaScannerConnection.scanFile(this@viewPDF, arrayOf(file.toString()),
                    null, null)
                withContext(Dispatchers.Main){
                    Toast.makeText(this@viewPDF, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                }


            }

        }

    }

    class RetrievePDFFromURL(pdfView: PDFView,progressBar: ProgressBar) :
        AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView
        val progD = progressBar

        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params.get(0))

                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                // on below line we are checking if the response
                // is successful with the help of response code
                // 200 response code means response is successful
                if (urlConnection.responseCode == 200) {
                    // on below line we are initializing our input stream
                    // if the response is successful.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                // on below line we are simply printing
                // our exception and returning null
                e.printStackTrace()
                return null
            }
            // on below line we are returning input stream.
            return inputStream

        }

        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()
            progD.visibility = View.INVISIBLE

        }

    }
}