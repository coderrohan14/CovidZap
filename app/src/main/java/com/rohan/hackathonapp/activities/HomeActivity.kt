package com.rohan.hackathonapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.rohan.hackathonapp.LocationAddress
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.fragments.HelplineFragment
import com.rohan.hackathonapp.fragments.HomeFragment
import com.rohan.hackathonapp.fragments.HospitalsFragment
import com.rohan.hackathonapp.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_hospitals.*
import kotlin.math.log

class HomeActivity : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    lateinit var signInClient:GoogleSignInClient
    lateinit var toggle: ActionBarDrawerToggle
    private val PERMISSION_ID = 2002
    lateinit var fusedLocationClient: FusedLocationProviderClient
//     var lastLatitude:Double? = null
//     var lastLongitude:Double?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        //Toast.makeText(this,"The latitude is $lastLatitude",Toast.LENGTH_LONG).show()
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

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            var lastLatitude = mLastLocation.latitude
            var lastLongitude = mLastLocation.longitude
            //Toast.makeText(this@HomeActivity,"The longitude is $lastLongitude",Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }


    @SuppressLint("MissingPermission")
     fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val lastLatitude = location.latitude
                        val lastLongitude = location.longitude

                        val locationAddress = LocationAddress()
                        var s:String?="hello1"
                        locationAddress.getAddressFromLocation(
                            lastLatitude, lastLongitude, applicationContext, GeoCoderHandler()
                        )
                        val sharedPreferences: SharedPreferences = this.getSharedPreferences("Location Details",
                            MODE_PRIVATE
                        )
                        val editor = sharedPreferences.edit()
                        editor.putString("last_lat",lastLatitude.toString())
                        editor.putString("last_long",lastLongitude.toString())
                        //editor.putString("locState", s.toString())
                        editor.apply()
                        editor.commit()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    init{
        instance=this
    }
    companion object{
        private var instance: HomeActivity? = null
        var cState:String?=null

         class GeoCoderHandler() : Handler() {
            var cState:String?="hello"

            override fun handleMessage(message: Message) {
                val locationAddress: String?
                locationAddress = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("address")
                    }
                    else -> "null"
                }
                 cState = locationAddress.toString()
                val sharedPreferences: SharedPreferences = instance!!.applicationContext.getSharedPreferences("Location Details",
                    MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString("locState", cState.toString())
                editor.apply()
                editor.commit()
               // Toast.makeText(instance!!.applicationContext,cState,Toast.LENGTH_LONG).show()
            }

        }

    }


}