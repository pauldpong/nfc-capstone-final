package com.capstone.nfc.ui.reader

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.capstone.nfc.R
import com.capstone.nfc.databinding.ActivityReaderBinding
import com.capstone.nfc.services.NFCHCEService
import com.capstone.nfc.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private val viewModel by viewModels<ReaderViewModel>()
    private lateinit var dataBinding: ActivityReaderBinding
    private var nfcAdapter : NfcAdapter? = null
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        uid = viewModel.uid

        dataBinding.layout.setOnClickListener {
            onRestartTap()
        }
        dataBinding.layout.isClickable = false
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        // Send SELECT AID
        val response = isoDep.transceive(Utils.hexStringToByteArray("00A4040007F0162534435261"))
        if (response.contentEquals(NFCHCEService.STATUS_SUCCESS)) { // Successfully connected with writer NFC card
            // Send writer the requester/reader ID to get access token
            val requesterID = uid?.toByteArray()
            val accessToken = isoDep.transceive(requesterID)

            // Store access token in user's shared field
            if (accessToken.contentEquals(NFCHCEService.STATUS_FAILED)) {
                Log.e("tag","Error")
            } else {
                viewModel.addShared(String(accessToken))
            }

            onSuccess()
        } else {
            Log.e("tag","Error")
        }

        isoDep.close()
    }

    override fun onResume() {
        super.onResume()
        enableReader()
    }

    override fun onPause() {
        super.onPause()
        disableReader()
    }

    private fun onSuccess() {
        runOnUiThread {
            val fromColor = resources.getColor(R.color.black, null)
            val toColor = resources.getColor(R.color.success, null)

            val animator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
            animator.duration = 250
            animator.addUpdateListener {
                dataBinding.layout.setBackgroundColor(it.animatedValue as Int)
            }

            animator.start()

            dataBinding.lottieAnimationView.setAnimationFromUrl("https://assets4.lottiefiles.com/datafiles/8UjWgBkqvEF5jNoFcXV4sdJ6PXpS6DwF7cK4tzpi/Check Mark Success/Check Mark Success Data.json")
            dataBinding.lottieAnimationView.repeatCount = 0
            dataBinding.lottieAnimationView.playAnimation()

            dataBinding.textPrompt.text = "Success!\nTap to read again"
            dataBinding.layout.isClickable = true
        }

        disableReader()
    }

    private fun onRestartTap() {
        runOnUiThread {
            val fromColor = resources.getColor(R.color.success, null)
            val toColor = resources.getColor(R.color.black, null)

            val animator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
            animator.duration = 250
            animator.addUpdateListener {
                dataBinding.layout.setBackgroundColor(it.animatedValue as Int)
            }

            animator.start()

            dataBinding.lottieAnimationView.setAnimationFromUrl("https://assets10.lottiefiles.com/packages/lf20_wi7HGR.json")
            dataBinding.lottieAnimationView.repeatCount = LottieDrawable.INFINITE
            dataBinding.lottieAnimationView.playAnimation()

            dataBinding.textPrompt.text = "Hold to sharer"
            dataBinding.layout.isClickable = false
        }

        enableReader()
    }

    private fun enableReader() {
        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    private fun disableReader() {
        nfcAdapter?.disableReaderMode(this)
    }
}