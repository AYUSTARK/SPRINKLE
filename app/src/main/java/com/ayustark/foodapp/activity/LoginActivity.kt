package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    var logged = false
    private lateinit var txtRegisterYourself: TextView
    private lateinit var login: Button
    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgotPassword: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)
        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)
        login = findViewById(R.id.btnLogin)

        /*Handling the clicks using the setOnClickListener method*/
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        txtRegisterYourself.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }


        login.setOnClickListener {
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url ="http://13.235.250.119/v2/login/fetch_result"
            if (etPassword.text.toString().length >= 4) {
                if (etMobileNumber.text.toString().length == 10) {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etMobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())
                    if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST,
                            url, jsonParams,
                            Response.Listener {
                                try{
                                //println("The Response is $it")
                                val success = it.getJSONObject("data").getBoolean("success")
                                if (success) {
                                    val restaurantJSONObject =
                                        it.getJSONObject("data").getJSONObject("data")
                                    //add to shared preference
                                    logged = true
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true)
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
                                        this@LoginActivity,
                                        HomeActivity::class.java
                                    )
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "You have successfully Logged In",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    val error=it.getJSONObject("data").getString("errorMessage")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        error,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                } catch (e: JSONException) {
                                    println("Error is $it")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Some Unexpected error occurred2!!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
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
                        val dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings") { _, _ ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { _, _ ->
                            ActivityCompat.finishAffinity(this@LoginActivity)
                        }
                        dialog.create()
                        dialog.show()
                        //Internet not available

                    }

                }else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Length of phone number should be 10 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else {
                Toast.makeText(
                    this@LoginActivity,
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
}
