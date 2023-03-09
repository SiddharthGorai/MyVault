package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class loginActivity : AppCompatActivity() {
    private lateinit var logEmail: EditText
    private lateinit var logPass: EditText
    private lateinit var btnLog: Button
    private lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, userprofile::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // create account
        val regButton: Button = findViewById(R.id.createAcc)
        regButton.setOnClickListener {
            val intent = Intent(this, createAccount::class.java)
            startActivity(intent)

        }

        logEmail = findViewById(R.id.lEmail)
        logPass = findViewById(R.id.lPass)
        btnLog = findViewById(R.id.lButton)
        auth = Firebase.auth



        btnLog.setOnClickListener {
            signin(logEmail, logPass)
        }
    }

    private fun signin(logEmail: EditText, logPass: EditText) {

        val pBar = findViewById<ProgressBar>(R.id.pBar)

        val llEmail: String = logEmail.text.toString().trim()
        val llPass: String = logPass.text.toString().trim()

        if (llEmail.isEmpty()) {
            logEmail.error = "Please enter email."
            logEmail.requestFocus()
        }
        else if (llPass.isEmpty()){
            logPass.error = "Please enter password."
            logPass.requestFocus()
        }
        else {
            pBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(llEmail, llPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val verification = auth.currentUser?.isEmailVerified
                        if (verification == true) {
                            updateUI(pBar)
                            finish()

                        } else {
                            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT)
                                .show()
                            pBar.visibility = View.INVISIBLE

                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        pBar.visibility = View.INVISIBLE
                    }
                }
        }
    }

    private fun updateUI(Bar: ProgressBar) {
        val intent = Intent(this, userprofile::class.java)
        startActivity(intent);
        Bar.visibility = View.INVISIBLE
    }

}