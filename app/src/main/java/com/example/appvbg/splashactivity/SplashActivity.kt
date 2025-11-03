package com.example.appvbg.splashactivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.appvbg.APIConstant
import com.example.appvbg.MainActivity
import com.example.appvbg.R
import com.example.appvbg.api.makeRequest
import com.example.appvbg.loginactivity.LoginActivity
import com.example.appvbg.api.PrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 1000 // 1 segundo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("authToken", null)

        Handler(Looper.getMainLooper()).postDelayed({
            if (token != null) {

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val checkSession = makeRequest(
                            """${APIConstant.BACKEND_URL}api/auth/checkSession/""",
                            "GET",
                            PrefsHelper.getDRFToken(applicationContext).toString()
                        )

                        if (checkSession != "error") {
                            withContext(Dispatchers.Main) {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                finish()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }

            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }, SPLASH_DELAY)
    }
}



