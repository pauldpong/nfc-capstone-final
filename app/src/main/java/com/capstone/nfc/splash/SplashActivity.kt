package com.capstone.nfc.splash

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.AUTH_INTENT
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.data.Response
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.api.AuthProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Named(MAIN_INTENT) @Inject lateinit var mainIntent: Intent
    private val viewModel by viewModels<SplashViewModel>()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserAuthStatus()
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val providerType = response?.providerType

            if (providerType == EmailAuthProvider.PROVIDER_ID) {
                viewModel.createUser(response.email).observe(this) {
                    if (it is Response.Success) {
                        startActivity(mainIntent)
                        finish()
                    }
                }
            } else {
                viewModel.createUser().observe(this) {
                    if (it is Response.Success) {
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun checkUserAuthStatus() {
        if (viewModel.userAuthenticated) {
            startActivity(mainIntent)
            finish()
        } else {
            createSignInIntent()
        }
    }
}