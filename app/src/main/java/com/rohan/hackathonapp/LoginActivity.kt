package com.rohan.hackathonapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

const val REQUEST_CODE_SIGN_IN = 0

class LoginActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        btnLogin.setOnClickListener {
            loginUser()
        }
        txtRegister.setOnClickListener {
            Intent(this,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        btnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClient_id))
                .requestEmail().requestProfile().build()

            val signInClient = GoogleSignIn.getClient(this,options)
            signInClient.signInIntent.also{
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }

    }

    private fun loginUser(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()


        if(email.isNotEmpty()&&password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        Intent(this@LoginActivity,HomeActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }else{
            Toast.makeText(this@LoginActivity,"Please enter all the details.", Toast.LENGTH_LONG).show()
        }
    }

    private fun googleAuthForFirebase(account:GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                    auth.signInWithCredential(credentials).await()
                    val name = account.displayName
                    val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.currentUser?.updateProfile(profile)?.await()
                        withContext(Dispatchers.Main) {
                            Intent(this@LoginActivity, HomeActivity::class.java).also {
                                startActivity(it)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== REQUEST_CODE_SIGN_IN){
           val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let{
                googleAuthForFirebase(it)
            }
        }

    }

}