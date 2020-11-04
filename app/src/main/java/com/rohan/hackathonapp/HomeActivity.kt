package com.rohan.hackathonapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser==null){
            Toast.makeText(this,"Not logged in",Toast.LENGTH_LONG).show()
        }
        auth.currentUser?.let{
            tvNameDis.text = it.displayName.toString()
            tvEmailDis.text = it.email.toString()

        }

    }
}