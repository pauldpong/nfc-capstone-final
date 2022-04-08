package com.capstone.nfc.auth

import android.content.Intent
import android.util.Log
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.ActivityAuthBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import com.pradeep.form.simple_form.form_items.FormTypes
import com.pradeep.form.simple_form.form_items.SingleLineTextType
import com.pradeep.form.simple_form.model.Form
import com.pradeep.form.simple_form.presentation.FormSubmitCallback


@AndroidEntryPoint
class AuthActivity : AppCompatActivity(), FormSubmitCallback {
    @Named(MAIN_INTENT) @Inject lateinit var mainIntent: Intent
    private lateinit var dataBinding: ActivityAuthBinding
    private val viewModel by viewModels<AuthViewModel>()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityAuthBinding.inflate(layoutInflater)
        dataBinding.simpleForm.setData(getFormData(), callback = this)
        setContentView(dataBinding.root)
        dataBinding.signInAnon.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(true)
                .build(),
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
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
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
    private fun getFormData(): List<Form> {
        val forms = mutableListOf<Form>()
        forms.add(
            Form(
                formType = FormTypes.SINGLE_LINE_TEXT,
                question = "Name",
                hint = "Please enter your name",
                singleLineTextType = SingleLineTextType.TEXT,
                errorMessage = "Please provide an answer"
            )
        )
        forms.add(
            Form(
                formType = FormTypes.SINGLE_LINE_TEXT,
                question = "Email",
                hint = "please enter your email address",
                singleLineTextType = SingleLineTextType.EMAIL_ADDRESS,
                errorMessage = "Please provide a valid email address",

            )
        )
        forms.add(
            Form(
                formType = FormTypes.SINGLE_LINE_TEXT,
                question = "Password",
                hint = "Please enter a password",
                singleLineTextType = SingleLineTextType.TEXT,
                errorMessage = "Please provide a password"
            )
        )
        return forms
    }

    override fun onFormSubmitted(forms: List<Form>) {
        this.signInAnonAndCreateUser()
        for (field in forms){
            val res = field.answer
            Log.i("TAG", res.toString())
        }

    }
}