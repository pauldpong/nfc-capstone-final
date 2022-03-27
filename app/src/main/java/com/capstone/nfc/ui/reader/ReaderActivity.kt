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
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        // Send SELECT AID
        val response = isoDep.transceive(Utils.hexStringToByteArray("00A4040007F0162534435261"))
        if (response.contentEquals(NFCHCEService.STATUS_SUCCESS)) { // Successfully connected with writer NFC card
            Log.e("tag","success connection")
            // Send writer the requester/reader ID to get access token
            val requesterID = uid?.toByteArray()
            val accessToken = isoDep.transceive(requesterID)
//            activity?.runOnUiThread {
//                dataBinding.nameField.text = accessToken.toString()
//            }

            // Store access token in user's shared field
            viewModel.addShared(String(accessToken))
            Log.e("tag","success overall")
        } else {
//            val fromColor = resources.getColor(R.color.black, null)
//            val toColor = resources.getColor(R.color.error, null)
//
//            val animator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
//            animator.duration = 250
//            animator.addUpdateListener {
//                dataBinding.layout.setBackgroundColor(it.animatedValue as Int)
//            }
//
//            animator.start()

            Log.e("tag","Error")
        }

        isoDep.close()
    }

    override fun onResume() {
        super.onResume()

        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }
}