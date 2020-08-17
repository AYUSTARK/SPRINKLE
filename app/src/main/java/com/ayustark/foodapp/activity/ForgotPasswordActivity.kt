package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etForgotMobile: EditText
    private lateinit var etForgotEmail: EditText
    private lateinit var btnForgotNext: Button
    private lateinit var rlContentMain:RelativeLayout
    lateinit var mobileNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        etForgotMobile = findViewById(R.id.etForgotMobile)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnForgotNext)
        rlContentMain=findViewById(R.id.rlContentMain)
        rlContentMain.visibility=View.VISIBLE

        btnForgotNext.setOnClickListener {
            mobileNumber = etForgotMobile.text.toString()
            if (mobileNumber.length == 10) {
                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("email", etForgotEmail.text.toString())
                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
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
                                    val first = it.getJSONObject("data")
                                        .getBoolean("first_try")
                                    val intent=Intent(this@ForgotPasswordActivity,ResetActivity::class.java)
                                    intent.putExtra("mobile",mobileNumber)
                                    if (first) {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "The OTP has been mailed to you",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Please refer to the previous mail for OTP",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    val error =
                                        it.getJSONObject("data")
                                            .getString("errorMessage")
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        error,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } catch (e: JSONException) {
                                //println("Error is $it")
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Some Unexpected error occurred2!!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Volley error occurred!!!!",
                                Toast.LENGTH_SHORT
                            ).show()
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
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent =
                            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Length of mobile number should be 10 digits",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
