package com.rohan.hackathonapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.rohan.hackathonapp.R
import com.rohan.hackathonapp.fragments.HelplineFragment
import com.rohan.hackathonapp.fragments.HomeFragment
import com.rohan.hackathonapp.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_hospitals.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var auth :FirebaseAuth
    private lateinit var signInClient:GoogleSignInClient
    private lateinit var toggle: ActionBarDrawerToggle
    private val PERMISSION_ID = 2002
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        sharedPreferences =  this.getSharedPreferences("Location Details", MODE_PRIVATE)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        openHome()
        auth = FirebaseAuth.getInstance()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
                    alert.setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        signInClient.signOut()
                        val new4 = Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(new4)
                        finish()
                    }
                    alert.setNegativeButton("No") { _, _ ->
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

    private fun setUpToolbar() {
        supportActionBar?.height
        supportActionBar?.title = "Welcome"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openHome() {
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
            val latitude = mLastLocation.latitude
            val longitude = mLastLocation.longitude
            val editor = sharedPreferences.edit()
            editor.putString("last_lat",latitude.toString())
            editor.putString("last_long",longitude.toString())
            try {
                val geocoder = Geocoder(applicationContext,Locale.getDefault())
                val addresses: MutableList<Address> =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if(!addresses.isNullOrEmpty()){
                    val state = addresses[0].adminArea
                    editor.putString("locState", state.toString())
                }
            }catch (e:Exception){
                Toast.makeText(this@HomeActivity, "Current Address not found!", Toast.LENGTH_LONG).show()
            }
            editor.apply()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }


    @SuppressLint("MissingPermission")
     fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val editor = sharedPreferences.edit()
                        editor.putString("last_lat",latitude.toString())
                        editor.putString("last_long",longitude.toString())
                        try {
                            val geocoder = Geocoder(applicationContext,Locale.getDefault())
                            val addresses: MutableList<Address> =
                                geocoder.getFromLocation(latitude, longitude, 1)
                            if(!addresses.isNullOrEmpty()){
                                val state = addresses[0].adminArea
                                editor.putString("locState", state.toString())
                            }
                        }catch (e:Exception){
                            Toast.makeText(this, "Current Address not found!", Toast.LENGTH_LONG).show()
                        }
                        editor.apply()
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

}