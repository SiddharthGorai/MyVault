//package com.example.myvault
//
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import com.yalantis.ucrop.UCrop
//import com.yalantis.ucrop.UCropActivity
//import java.io.File
//import java.lang.Exception
//import java.util.UUID
//
//class editImg : AppCompatActivity() {
//
//    private lateinit var imgUri: Uri
//    private lateinit var options: UCrop.Options
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_img)
//
//        val raw: String = intent.getStringExtra("Data").toString()
//        val dest: String = UUID.randomUUID().toString() + ".jpg"
//
//        imgUri = Uri.parse(raw)
//        try{
//        options.setBrightnessEnabled(true)
//        options.setShowCropGrid(true)
//
//         UCrop.of(imgUri, Uri.fromFile(File(cacheDir,dest)) )
//            .withOptions(options)
//            .withAspectRatio(0F, 0F)
//            .withMaxResultSize(2000,2000)
//            .start(this)
//
//
//    }
//     catch(e: Exception) {
//         Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
//     }
//
//
//
//
//}
//}