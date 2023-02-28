package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class userprofile : AppCompatActivity() {

    private lateinit var signoutAlert: AlertDialog.Builder
    private lateinit var database: DatabaseReference

    var menu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.navbar),menu)
        this.menu = menu
        changeMenuBar()
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
    private fun changeMenuBar() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        database = Firebase.database.reference
        database.child("User").child(userId).get().addOnSuccessListener {
            val uName = it.child("usrName").value.toString()
            val uEmail = it.child("usrEmail").value.toString()

            val dName = menu?.findItem(R.id.dname)
            val dEmail = menu?.findItem(R.id.demail)
            dEmail?.title = uEmail
            dName?.title = uName


        }.addOnFailureListener{
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)




    }
}