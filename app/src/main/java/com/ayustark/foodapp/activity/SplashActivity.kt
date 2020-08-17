package com.ayustark.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ayustark.foodapp.R

class SplashActivity : AppCompatActivity() {

    /*Life-cycle method of the com.ayustark.foodapp.activity*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*Setting up the view for the com.ayustark.foodapp.activity*/
        setContentView(R.layout.activity_splash)

        /*This is how we change the com.ayustark.foodapp.activity with a delay of 2000 milliseconds.
        * The delay time can be changed by changing the value of the time in milliseconds
        * Here the com.ayustark.foodapp.activity is displayed for 2 seconds and then the Handler starts the new process after 2 seconds
        * the new task is the one which we write inside the handler.
        * startActivity() is used to start the new com.ayustark.foodapp.activity, whereas finish() is used to destroy the current com.ayustark.foodapp.activity.
        * We use finish() here so the when the user presses back button, she is not redirected to this com.ayustark.foodapp.activity*/
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, 2000)
    }
}
