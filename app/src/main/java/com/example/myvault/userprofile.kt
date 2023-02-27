package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class userprofile : AppCompatActivity() {

    private lateinit var signoutAlert: AlertDialog.Builder

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.navbar),menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOut -> doThis()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doThis() {
        signoutAlert = AlertDialog.Builder(this)
        signoutAlert.setTitle("Alert!")
            .setMessage("Do you want to Sign out ?")
            .setCancelable(true)
            .setPositiveButton("Yes"){ dialogInterface, it ->
                Firebase.auth.signOut()
                val intent = Intent(this,loginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No"){dialogInterface, it ->
                dialogInterface.cancel()
            }.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)




    }
}