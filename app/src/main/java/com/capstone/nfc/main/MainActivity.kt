package com.capstone.nfc.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.nfc.Constants.AUTH_INTENT
import com.capstone.nfc.R
import com.capstone.nfc.data.Response
import com.capstone.nfc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Named(AUTH_INTENT) @Inject
    lateinit var authIntent: Intent
    private lateinit var dataBinding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var fileChooserActivity: ActivityResultLauncher<Intent>

    // Animations
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var fabExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        fileChooserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data

                uri?.let {
                    this.contentResolver?.query(uri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val fullFileName: String = cursor.getString(nameIndex)

                    viewModel.uploadPdf(uri, fullFileName).observe(this) { response ->
                        if (response is Response.Success) {
                            Log.e("A", "Success")
                            //viewModel.loadMyFiles()
                        } else if (response is Response.Loading) {
                            Log.e("A", "loading")
//                            dataBinding.pdfUri.text = "loading..."
                        } else {
                            Log.e("A", "error")
//                            dataBinding.pdfUri.text = "error"
                        }
                    }
                }
            }
        }

        setupBottomNavController()
        setupFabOnClick()
        observeAuthState()
    }

    private fun setupBottomNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController

        dataBinding.bottomNav.setupWithNavController(navController)
        Navigation.setViewNavController(dataBinding.readFab, navController)
    }

    private fun setupFabOnClick() {
        dataBinding.fab.setOnClickListener {
            toggleFab()
        }

        dataBinding.uploadFab.setOnClickListener {
            val intent = Intent().apply {
                type = "*/*"
                action = Intent.ACTION_GET_CONTENT
            }

            fileChooserActivity.launch(intent)

            toggleFab()
        }

        dataBinding.readFab.setOnClickListener {
            it.findNavController().navigate(R.id.ReaderFragment)

            toggleFab()
        }
    }

    private fun toggleFab() {
        setVisibility(fabExpanded)
        setAnimation(fabExpanded)
        setClickable(fabExpanded)
        fabExpanded = !fabExpanded
    }

    private fun setAnimation(expanded: Boolean) {
        if (!expanded) {
            dataBinding.readFab.visibility = View.VISIBLE
            dataBinding.uploadFab.visibility = View.VISIBLE
        } else {
            dataBinding.readFab.visibility = View.INVISIBLE
            dataBinding.uploadFab.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(expanded: Boolean) {
        if (!expanded) {
            dataBinding.readFab.startAnimation(fromBottom)
            dataBinding.uploadFab.startAnimation(fromBottom)
            dataBinding.fab.startAnimation(rotateOpen)
        } else {
            dataBinding.readFab.startAnimation(toBottom)
            dataBinding.uploadFab.startAnimation(toBottom)
            dataBinding.fab.startAnimation(rotateClose)
        }
    }

    private fun setClickable(expanded: Boolean) {
        if (!expanded) {
            dataBinding.readFab.isClickable = true
            dataBinding.uploadFab.isClickable = true
        } else {
            dataBinding.readFab.isClickable = false
            dataBinding.uploadFab.isClickable = false
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