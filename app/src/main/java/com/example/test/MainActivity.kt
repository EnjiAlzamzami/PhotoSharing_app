package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginbutton: Button
    private lateinit var logoutbutton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth= FirebaseAuth.getInstance()
        val current_user=auth.currentUser
        //get values
        email=findViewById(R.id.user_email)
        password=findViewById(R.id.user_password)
        loginbutton=findViewById(R.id.loginbutton)
        logoutbutton=findViewById(R.id.Registeration)

        if(current_user != null){
            val Intent=Intent(this,main_user::class.java)
            startActivity(Intent)
            finish()
        }

    }
    fun loginaction(view: View){
        val email = email.text.toString()
        var password = password.text.toString()
        //Authentication process with database
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val current_user = auth.currentUser?.email.toString()
                Toast.makeText(this,"Welcome: ${current_user}",Toast.LENGTH_LONG).show()
                val intent= Intent(this, main_user::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

    fun registeraction(view: View){
        val email = email.text.toString()
        var password = password.text.toString()
        //Add user on database
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent= Intent(this, main_user::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
}

