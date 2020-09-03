package com.pictionary.pictio.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pictionary.pictio.R
import com.pictionary.pictio.view.Fragment.HomeFragment
import com.pictionary.pictio.view.Fragment.NotificationFragment
import com.pictionary.pictio.view.Fragment.ProfileFragment
import com.pictionary.pictio.view.Fragment.SearchFragment


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
    }
    lateinit var bottomNavigationView: BottomNavigationView
    var selectedfragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate")

        bindViews()
        onNavigationItemSelectedListener()

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }

    private fun bindViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }


    private fun onNavigationItemSelectedListener() {
        val navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.nav_home -> selectedfragment = HomeFragment()
                        R.id.nav_search -> selectedfragment = SearchFragment()
                        R.id.nav_add -> {
                            selectedfragment = null
                            startActivity(Intent(this@MainActivity, PostActivity::class.java))
                        }
                        R.id.nav_heart -> selectedfragment = NotificationFragment()
                        R.id.nav_profile -> {
                            val editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                            editor.putString("profileid", FirebaseAuth.getInstance().currentUser!!.uid)
                            editor.apply()
                            selectedfragment = ProfileFragment()
                        }
                    }
                    if (selectedfragment != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedfragment!!).commit()
                    }
                    return true
                }
            }
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }
}