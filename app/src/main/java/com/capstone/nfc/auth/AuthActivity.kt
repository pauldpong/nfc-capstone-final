package com.capstone.nfc.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    @Named(MAIN_INTENT) @Inject lateinit var mainIntent: Intent
    private lateinit var dataBinding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.signInButton.setOnClickListener {
            signInAnonAndCreateUser()
        }
    }

    private fun signInAnonAndCreateUser() {
        viewModel.signInAnon().observe(this) { response ->
            if (response is Success) {
                viewModel.createUser().observe(this) {
                    if (it is Success) {
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
        }
    }
}