package com.capstone.nfc.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capstone.nfc.Constants.AUTH_INTENT
import com.capstone.nfc.R
import com.capstone.nfc.databinding.ActivityMainBinding
import com.capstone.nfc.ui.ModalFABBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Named(AUTH_INTENT) @Inject
    lateinit var authIntent: Intent
    private lateinit var dataBinding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        setupBottomNavController()
        setupFabOnClick()
        observeAuthState()
    }

    private fun setupBottomNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController

        dataBinding.bottomNav.setupWithNavController(navController)
    }

    private fun setupFabOnClick() {
        dataBinding.fab.setOnClickListener {
            val modal = ModalFABBottomSheet()
            modal.show(supportFragmentManager, null)
        }
    }

    private fun observeAuthState() {
        viewModel.getAuthState().observe(this) { isUserSignedOut ->
            if (isUserSignedOut) {
                startActivity(authIntent)
                finish()
            }
        }
    }
}