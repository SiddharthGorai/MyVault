package com.example.myvault

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedInputStream
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

    private fun downloadPDF(pdfName: String, urll: String) {
        val pdfNamee = pdfName + ".jpg"
        val downloadManager = getSystemService(DownloadManager:: class.java)
        val request = DownloadManager.Request(urll.toUri())
            .setMimeType("application/pdf")
            .addRequestHeader("Authorization","Bearer <token>")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,pdfNamee)

        downloadManager.enqueue(request)
        Toast.makeText(this,"Saved to Downloads",Toast.LENGTH_SHORT).show()
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