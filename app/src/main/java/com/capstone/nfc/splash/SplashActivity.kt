package com.capstone.nfc.splash

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.AUTH_INTENT
import com.capstone.nfc.Constants.MAIN_INTENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Named(AUTH_INTENT) @Inject lateinit var authIntent: Intent
    @Named(MAIN_INTENT) @Inject lateinit var mainIntent: Intent
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserAuthStatus()
    }

    private fun checkUserAuthStatus() {
        if (viewModel.userAuthenticated) {
            startActivity(mainIntent)
        } else {
            startActivity(authIntent)
        }
        finish()
    }
}