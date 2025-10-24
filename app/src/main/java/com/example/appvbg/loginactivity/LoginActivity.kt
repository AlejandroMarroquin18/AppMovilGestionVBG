package com.example.appvbg.loginactivity


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.example.appvbg.R


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }


}
