package com.capstone.nfc.ui.reader

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.databinding.FragmentReaderBinding
import com.capstone.nfc.services.NFCHCEService
import com.capstone.nfc.utilities.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReaderFragment: BaseFragment<FragmentReaderBinding>(FragmentReaderBinding::inflate), NfcAdapter.ReaderCallback {
    private val TAG = "ReaderFragment"
    private val viewModel by viewModels<ReaderViewModel>()
    private var nfcAdapter : NfcAdapter? = null
    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        uid = viewModel.uid
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
//            activity?.runOnUiThread {
//                dataBinding.nameField.text = accessToken.toString()
//            }

            // Store access token in user's shared field
            viewModel.addShared(String(accessToken))
        } else {
//            activity?.runOnUiThread {
//                dataBinding.nameField.text = "Brew"
//            }
        }

        isoDep.close()
    }

    // Networking layer - use certificate (lkjS0BaYmE9y92HnKLLx) request data from the backend
    // Backend - Firebase (POC)
    // Firebase.connect ()
    // Firebase.getDocument(lkjS0BaYmE9y92HnKLLx).resume
    // Hey firebase give me lkjS0BaYmE9y92HnKLLx's resume
    // Firebase response with resume
    // Display resume (UI)

    override fun onResume() {
        super.onResume()

        nfcAdapter?.enableReaderMode(activity, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity)
    }
}