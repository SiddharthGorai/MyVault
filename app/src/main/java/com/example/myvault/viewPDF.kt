package com.example.myvault

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class viewPDF : AppCompatActivity() {

    private lateinit var databaseRef1: DatabaseReference
    private val userID = FirebaseAuth.getInstance().currentUser!!.uid


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)

        val pdfView: PDFView = findViewById(R.id.pdfView)
        val bundleG = intent.extras

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
                                RetrievePDFFromURL(pdfView).execute(urll)
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

    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView

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
        }

    }
}