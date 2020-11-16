package com.rohan.hackathonapp.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.fragments.HelplineFragment
import com.rohan.hackathonapp.fragments.HomeFragment
import com.rohan.hackathonapp.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeActivity : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    lateinit var signInClient:GoogleSignInClient
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        openHome()
        auth = FirebaseAuth.getInstance()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.WebClientId2))
            .requestEmail().requestProfile().build()
        signInClient = GoogleSignIn.getClient(this,options)
        setUpToolbar()
        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_contact_details -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, HelplineFragment())
                        .commit()
                    supportActionBar?.title = "Helpline Numbers"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_log_out -> {
                    val alert = AlertDialog.Builder(this@HomeActivity, R.style.MyDialogTheme)
                    alert.setTitle("Log Out?")
                    alert.setMessage("Are you sure you want to Log Out?")
                    alert.setPositiveButton("Yes") { text, listener ->
                        auth.signOut()
                        signInClient.signOut()
                        val new4 = Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(new4)
                        finish()
                    }
                    alert.setNegativeButton("No") { text, listener ->
                        openHome()
                        drawerLayout.closeDrawers()
                    }
                    alert.setCancelable(false)
                    alert.create()
                    alert.show()
                }
            }
                true
            }

        }


    fun setUpToolbar() {
        supportActionBar?.height
        supportActionBar?.title = "Welcome"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        navView.setCheckedItem(R.id.menu_home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
       finish()
    }

}