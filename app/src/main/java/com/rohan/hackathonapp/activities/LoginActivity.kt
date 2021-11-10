package com.rohan.hackathonapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.rohan.hackathonapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val REQUEST_CODE_SIGN_IN = 123

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var signInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {
            loginUser()
        }
        txtRegister.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().requestProfile().build()
        signInClient = GoogleSignIn.getClient(this,options)

        btnGoogleSignIn.setOnClickListener {
            setProgressVisibility(true)
            signInClient.signInIntent.also{
                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
            }
        }

    }

    private fun setProgressVisibility(visible: Boolean){
        if(visible)
            pbLogin?.visibility = View.VISIBLE
        else
            pbLogin?.visibility = View.GONE
    }

    private fun loginUser(){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()


        if(email.isNotEmpty()&&password.isNotEmpty()) {
            setProgressVisibility(true)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password).await()
                    withContext(Dispatchers.Main){
                        Intent(this@LoginActivity, HomeActivity::class.java).also {
                            startActivity(it)
                            setProgressVisibility(false)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                        setProgressVisibility(false)
                    }
                }
            }
        }else{
            Toast.makeText(this@LoginActivity,"Please enter all the details.", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val name = user?.displayName
                    val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    user?.updateProfile(profile)
                    Intent(this@LoginActivity, HomeActivity::class.java).also {
                        startActivity(it)
                        setProgressVisibility(false)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity, "Login failed.", Toast.LENGTH_LONG).show()
                    setProgressVisibility(false)
                }
            }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                setProgressVisibility(false)
            }
        }else{
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
            setProgressVisibility(false)
        }
    }

}