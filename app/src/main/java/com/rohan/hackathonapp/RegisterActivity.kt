package com.rohan.hackathonapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.etEmail
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()
        btnRegister.setOnClickListener {
            registerUser()

        }
    }

    private fun registerUser() {
        val name = etNameRegister.text.toString()
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()


        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.currentUser?.updateProfile(profile)?.await()
                            withContext(Dispatchers.Main) {
                                Intent(this@RegisterActivity, HomeActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this@RegisterActivity,
                "Please enter all the details.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}