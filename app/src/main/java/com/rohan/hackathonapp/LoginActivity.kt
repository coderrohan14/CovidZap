package com.rohan.hackathonapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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

const val REQUEST_CODE_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var signInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {
            loginUser()
        }
        txtRegister.setOnClickListener {
            Intent(this,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.WebClientId2))
            .requestEmail().requestProfile().build()
        signInClient = GoogleSignIn.getClient(this,options)

        btnGoogleSignIn.setOnClickListener {

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
                            finish()
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
                                finish()
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



    override fun onBackPressed() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN&&resultCode == Activity.RESULT_OK) {
            try{
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                }
            }catch(e:Exception){
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

}