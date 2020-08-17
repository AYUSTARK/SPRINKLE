package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.ayustark.foodapp.R
import com.ayustark.foodapp.database.RestaurantDatabase
import com.ayustark.foodapp.fragment.*
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtUserName:TextView
    private lateinit var txtUserEmail:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        navigationView = findViewById(R.id.NavigationView)
        val header=navigationView.getHeaderView(0)
        txtUserName=header.findViewById(R.id.txtUserName)
        txtUserEmail=header.findViewById(R.id.txtUserEmail)
        sharedPreferences =
            getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.DrawerLayout)
        txtUserName.text= sharedPreferences.getString("name","ABC")
        txtUserEmail.text= sharedPreferences.getString("email","@")
        toolbar = findViewById(R.id.Toolbar)
        setUpToolbar()
        navigationView = findViewById(R.id.NavigationView)
        openFragment(HomeFragment(), "Home", R.id.home)
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.Open_Drawer,
            R.string.Close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    openFragment(HomeFragment(), "Home", R.id.home)
                }
                R.id.Favourites -> {
                    openFragment(FavouriteFragment(), "HomeRecyclerAdapter", R.id.Favourites)
                }
                R.id.Profile -> {
                    openFragment(ProfileFragment(), "Profile", R.id.Profile)
                }
                R.id.History->{
                    openFragment(HistoryFragment(),"My Order History",R.id.History)
                }
                R.id.Logout -> {
                    val dialog = AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Log Out Confirmation")
                    dialog.setMessage("Are you sure to log out?")
                    dialog.setPositiveButton("Log Out") { _, _ ->
                        sharedPreferences.edit().clear().apply()
                        RemoveEverything(this@HomeActivity).execute()
                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            this@HomeActivity,
                            "You have been successfully Logged Out",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    dialog.setNegativeButton("Cancel") { _, _ ->
                        drawerLayout.closeDrawers()
                        navigationView.menu.findItem(R.id.home).isChecked = true
                    }
                    dialog.create()
                    dialog.show()
                }
                R.id.faq -> {
                    openFragment(FAQsFragment(), "About App",R.id.faq)
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFragment(fragment: Fragment, title: String, item: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FrameLayout, fragment).commit()
        navigationView.menu.findItem(item).isChecked = true
        supportActionBar?.title = title
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.FrameLayout)) {
            !is HomeFragment -> openFragment(
                HomeFragment(), "Home", R.id.home
            )
            else -> super.onBackPressed()
        }
    }

    class RemoveEverything(val context: Context) :
        AsyncTask<Void, Void, Void>() {
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "Restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Void? {
            db.clearAllTables()
            db.close()
            return null
        }
    }
}
