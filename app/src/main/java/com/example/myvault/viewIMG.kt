package com.example.myvault

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class viewIMG : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_img)

        val bundleG = intent.extras
        val nameUrl = bundleG?.get("imgUrl").toString()
        val imgNAME = bundleG?.get("imgName").toString()
        val imgView = findViewById<ImageView>(R.id.imageViewU)
        val downBtn = findViewById<FloatingActionButton>(R.id.downloadImgBtn)


        getSupportActionBar()?.setTitle(imgNAME)
        if (nameUrl !== null) {
            Glide.with(this)
                .load(nameUrl)
                .into(imgView)
        } else {
            imgView.setImageResource(R.drawable.baseline_image_24)
        }

        downBtn.setOnClickListener {
            downloadImg(nameUrl,imgNAME)
            Toast.makeText(this,"Saved to Gallery",Toast.LENGTH_SHORT).show()

        }

    }

    private fun downloadImg(nameUrl: String,imgNAME: String) {
        val downloadManager = getSystemService(DownloadManager:: class.java)
        val request = DownloadManager.Request(nameUrl.toUri())
            .setMimeType("images/jpg")
            .addRequestHeader("Authorization","Bearer <token>")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,imgNAME)
        downloadManager.enqueue(request)

    }
}