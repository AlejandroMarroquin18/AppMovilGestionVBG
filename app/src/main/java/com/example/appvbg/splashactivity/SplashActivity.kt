package com.example.appvbg.splashactivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.appvbg.MainActivity
import com.example.appvbg.R
import com.example.appvbg.loginactivity.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 1000 // 1 segundo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // Ocultar la action bar
        supportActionBar?.hide()

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("authToken", null)

        Handler(Looper.getMainLooper()).postDelayed({

            if (token != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()

        }, SPLASH_DELAY)


    }
}


