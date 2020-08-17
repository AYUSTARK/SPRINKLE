package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    private lateinit var etOTP: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnReset: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        etOTP = findViewById(R.id.etOTP)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnReset=findViewById(R.id.btnReset)
        val mobileNumber= intent.getStringExtra("mobile")
        btnReset.setOnClickListener {
            if (etPassword.text.toString().length>=6){
                if (etConfirmPassword.text.toString()==etPassword.text.toString()){
                    val queue = Volley.newRequestQueue(this@ResetActivity)
                    val url="http://13.235.250.119/v2/reset_password/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password",etPassword.text.toString())
                    jsonParams.put("otp",etOTP.text.toString())
                    if (ConnectionManager().checkConnectivity(this@ResetActivity)) {
                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST,
                            url, jsonParams,
                            Response.Listener {
                                try {
                                    //println("The Response is $it")
                                    val success =
                                        it.getJSONObject("data")
                                            .getBoolean("success")
                                    if (success) {
                                        Toast.makeText(this@ResetActivity,"Your password has successfully changed",Toast.LENGTH_SHORT).show()
                                        val intent= Intent(this@ResetActivity,LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        val error =
                                            it.getJSONObject("data")
                                                .getString("errorMessage")
                                        Toast.makeText(
                                            this@ResetActivity,
                                            error,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }catch (e:JSONException){
                                    //println("Error is $it")
                                    Toast.makeText(
                                        this@ResetActivity,
                                        "Some Unexpected error occurred2!!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ResetActivity,
                                "Volley error occurred!!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }){
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "7d090d7ff9230d"
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)
                    }else{
                        val dialog = AlertDialog.Builder(this@ResetActivity)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings") { _, _ ->
                            val settingsIntent =
                                Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { _, _ ->
                            ActivityCompat.finishAffinity(this@ResetActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }else{
                    Toast.makeText(
                        this@ResetActivity,
                        "Confirm Password doesn't match with the input password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                Toast.makeText(
                    this@ResetActivity,
                    "Length of password must be atleast 6",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}