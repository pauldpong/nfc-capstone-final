package com.capstone.nfc.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.databinding.ActivitySignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import com.pradeep.form.simple_form.form_items.FormTypes
import com.pradeep.form.simple_form.form_items.SingleLineTextType
import com.pradeep.form.simple_form.model.Form
import com.pradeep.form.simple_form.presentation.FormSubmitCallback

@AndroidEntryPoint
class SignInActivity : AppCompatActivity(), FormSubmitCallback{
    @Named(MAIN_INTENT) @Inject lateinit var mainIntent: Intent
    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var dataBinding: ActivitySignInBinding
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivitySignInBinding.inflate(layoutInflater)
        dataBinding.simpleForm2.setData(getFormData(), callback = this)
        setContentView(dataBinding.root)

        dataBinding.signUpButton.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getFormData(): List<Form> {
        val forms = mutableListOf<Form>()
        forms.add(
            Form(
                formType = FormTypes.SINGLE_LINE_TEXT,
                question = "Email",
                hint = "please enter your Email address",
                singleLineTextType = SingleLineTextType.EMAIL_ADDRESS,
                errorMessage = "Please provide a valid email address",

                )
        )
        forms.add(
            Form(
                formType = FormTypes.SINGLE_LINE_TEXT,
                question = "Password",
                hint = "please enter a password",
                singleLineTextType = SingleLineTextType.TEXT,
                errorMessage = "Please provide a password"
            )
        )
        return forms
    }

    override fun onFormSubmitted(forms: List<Form>) {
        for (field in forms){
            val res = field.answer
            Log.i("TAG", res.toString())
        }

    }



//        val navController = findNavController(R.id.nav_host_fragment_content_sign_in)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }


//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_sign_in)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}