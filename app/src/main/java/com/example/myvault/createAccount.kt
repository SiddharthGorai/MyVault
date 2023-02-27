package com.example.myvault

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class createAccount : AppCompatActivity() {

    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var inputConPass: EditText
    private lateinit var inputUsername: EditText
    private lateinit var btnReg: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val inputEmail: EditText = findViewById(R.id.usrEmail)
        val inputPass: EditText = findViewById(R.id.usrPass)
        val inputConPass: EditText = findViewById(R.id.usrConPass)
        val inputUsername: EditText = findViewById(R.id.usrName)
        val btnReg: Button = findViewById(R.id.regButton)

        btnReg.setOnClickListener {
         startAuthentication(inputEmail, inputPass,inputConPass,inputUsername)
          }
    }

    private fun startAuthentication(
        inputEmail: EditText,
        inputPass: EditText,
        inputConPass: EditText,
        inputUsername: EditText
    ) {
        val strEmail: String = inputEmail.text.toString().trim()
        val strPass: String= inputPass.text.toString().trim()
        val strConPass: String = inputConPass.text.toString().trim()
        val strUsrname: String = inputUsername.text.toString().trim()


        if (strEmail.isEmpty())
        {
            inputEmail.error = "Please enter correct email."
            inputEmail.requestFocus()
        }
        if (strPass.isEmpty() || strPass.length < 6){
            inputPass.error = "Please enter password greater than 6 characters."
            inputPass.requestFocus()
        }
        if (strUsrname.isEmpty()){
            inputPass.error = "Please enter username."
            inputPass.requestFocus()
        }
        else if (strConPass != strPass){
            inputConPass.error = "Password not matching."
            inputConPass.requestFocus()
        }
        else{
            auth = Firebase.auth
            database = Firebase.database.reference

            auth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                       auth.currentUser?.sendEmailVerification()
                           ?.addOnSuccessListener {
                               Toast.makeText(this, "verification sent", Toast.LENGTH_SHORT).show()
                               saveData(strEmail,strUsrname)
                               updateUI()
                           }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }

            }

    private fun saveData(strEmail: String,strUsrname:String) {

        val user = userData(strEmail,strUsrname)
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).setValue(user)

    }

    private fun updateUI() {
        Firebase.auth.signOut()
        val intent = Intent(this, verification::class.java)
        startActivity(intent)
        finish()

    }


}