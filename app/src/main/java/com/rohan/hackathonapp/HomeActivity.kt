package com.rohan.hackathonapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    lateinit var signInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.WebClientId2))
            .requestEmail().requestProfile().build()
        signInClient = GoogleSignIn.getClient(this,options)

        if(auth.currentUser==null){
            Toast.makeText(this,"Not logged in",Toast.LENGTH_LONG).show()
        }
        auth.currentUser?.let{
            tvNameDis.text = it.displayName.toString()
            tvEmailDis.text = it.email.toString()

        }
        btnLogOut.setOnClickListener {
            auth.signOut()
            signInClient.signOut()
            Intent(this,LoginActivity::class.java).also{
                startActivity(it)
            }
        }

    }



    override fun onBackPressed() {
       onDestroy()
    }
}