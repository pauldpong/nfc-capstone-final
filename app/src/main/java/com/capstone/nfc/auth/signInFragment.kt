package com.capstone.nfc.auth

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.nfc.R
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.Constants
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response
import com.capstone.nfc.databinding.SignInFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject
import javax.inject.Named
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.nfc.Constants.MAIN_INTENT
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.ActivityAuthBinding
import com.pradeep.form.simple_form.form_items.FormTypes
import com.pradeep.form.simple_form.form_items.SingleLineTextType
import com.pradeep.form.simple_form.model.Form
import com.pradeep.form.simple_form.presentation.FormSubmitCallback


@AndroidEntryPoint
class SignInFragment : BaseFragment<SignInFragmentBinding>(SignInFragmentBinding::inflate) {
    private val model by viewModels<SignInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//      dataBinding.signInAnon.setOnClickListener {
//        //signInAnonAndCreateUser()
//       }
    }

//    private fun signInAnonAndCreateUser() {
//        model.signInAnon().observe(this) { response ->
//            if (response is Response.Success) {
//                model.createUser().observe(this) {
//                    if (it is Response.Success) {
//                        startActivity(mainIntent)
//                        finish()
//                    }
//                }
//            }
//        }
//    }
}