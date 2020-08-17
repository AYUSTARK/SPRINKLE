package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    var logged = false
    private lateinit var toolbar: Toolbar
    private lateinit var btnRegister: Button
    private lateinit var etName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etConfirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_register)
        sharedPreferences =
            getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_register)
        if (isLoggedIn) {
            val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)
        etPassword.setOnFocusChangeListener { view: View, b: Boolean ->
            if (!(etPassword.text.toString().length >= 4)){
                etPassword.error="Length of Password should be atleast 4";
            }
        }

        btnRegister.setOnClickListener {
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result"
            if (etPassword.text.toString().length >= 4) {
                if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                    if (etPhoneNumber.text.toString().length == 10) {
                        if (etEmail.text.toString().contains('@', true) &&etEmail.text.toString().length>=8) {
                            if (etName.text.toString().length >= 3) {
                                if (etAddress.text.toString() != " ") {
                                    val jsonParams = JSONObject()
                                    jsonParams.put("name", etName.text.toString())
                                    jsonParams.put("mobile_number", etPhoneNumber.text.toString())
                                    jsonParams.put("password", etPassword.text.toString())
                                    jsonParams.put("address", etAddress.text.toString())
                                    jsonParams.put("email", etEmail.text.toString())
                                    if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                                        val jsonObjectRequest = object : JsonObjectRequest(
                                            Method.POST,
                                            url, jsonParams,
                                            Response.Listener {
                                                try {
                                                    println("The Response is $it")
                                                    val success =
                                                        it.getJSONObject("data")
                                                            .getBoolean("success")
                                                    if (success) {
                                                        val restaurantJSONObject =
                                                            it.getJSONObject("data")
                                                                .getJSONObject("data")
                                                        //add to shared preference
                                                        logged = true
                                                        sharedPreferences.edit()
                                                            .putBoolean("isLoggedIn", true)
                                                            .apply()
                                                        sharedPreferences.edit().putString(
                                                            "user_id",
                                                            restaurantJSONObject.getString("user_id")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "name",
                                                            restaurantJSONObject.getString("name")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "email",
                                                            restaurantJSONObject.getString("email")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "mobile_number",
                                                            restaurantJSONObject.getString("mobile_number")
                                                        ).apply()
                                                        sharedPreferences.edit().putString(
                                                            "address",
                                                            restaurantJSONObject.getString("address")
                                                        ).apply()
                                                        val intent = Intent(
                                                            this@RegisterActivity,
                                                            HomeActivity::class.java
                                                        )
                                                        startActivity(intent)
                                                        Toast.makeText(
                                                            this@RegisterActivity,
                                                            "You have successfully registered yourself",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    } else {
                                                        val error =
                                                            it.getJSONObject("data")
                                                                .getString("errorMessage")
                                                        Toast.makeText(
                                                            this@RegisterActivity,
                                                            error,
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    }
                                                } catch (e: JSONException) {
                                                    println("Error is $it")
                                                    Toast.makeText(
                                                        this@RegisterActivity,
                                                        "Some Unexpected error occurred2!!!!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }, Response.ErrorListener {
                                                Toast.makeText(
                                                    this@RegisterActivity,
                                                    "Volley error occurred!!!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                //Here we will handle errors
                                            }) {
                                            override fun getHeaders(): MutableMap<String, String> {
                                                val headers = HashMap<String, String>()
                                                headers["Content-type"] = "application/json"
                                                headers["token"] = "7d090d7ff9230d"
                                                return headers
                                            }
                                        }
                                        queue.add(jsonObjectRequest)
                                    } else {
                                        val dialog = AlertDialog.Builder(this@RegisterActivity)
                                        dialog.setTitle("Error")
                                        dialog.setMessage("Internet Connection is not Found")
                                        dialog.setPositiveButton("Open Settings") { _, _ ->
                                            val settingsIntent =
                                                Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                            startActivity(settingsIntent)
                                            finish()
                                        }
                                        dialog.setNegativeButton("Exit") { _, _ ->
                                            ActivityCompat.finishAffinity(this@RegisterActivity)
                                        }
                                        dialog.create()
                                        dialog.show()
                                        //Internet not available

                                    }
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "An Address is Required",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Length of name should be atleast 3 letters",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Incorrect E-mail address",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Length of phone number should be 10 digits",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Confirm Password doesn't match with the password field",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Length of Password should be atleast 4",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (logged) {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}