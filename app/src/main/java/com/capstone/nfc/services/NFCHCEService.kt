package com.capstone.nfc.services

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.capstone.nfc.data.FileRepository
import com.capstone.nfc.data.UserRepository
import com.capstone.nfc.utilities.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class NFCHCEService : HostApduService() {
    companion object {
        const val TAG = "NFCHCEService"
        private const val AID = "F0162534435261"

        val STATUS_SUCCESS = Utils.hexStringToByteArray("9000")
        val STATUS_FAILED = Utils.hexStringToByteArray("6F00")

        // ISO-DEP SELECT AID Command APDU
        val SELECT_APDU_HEADER = Utils.hexStringToByteArray("00A40400" + String.format("%02X", AID.length / 2) + AID)
        const val HEADER_LENGTH_BYTES = 4 // 8 HEXs = 4 bytes
    }

    @Inject
    lateinit var fileRepository: FileRepository
    var filePath : String? = null

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: " + reason)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (intent.hasExtra("NFCHCEService.filePath")) {
                intent.extras?.getString("NFCHCEService.filePath")?.let {
                    filePath = it
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return STATUS_FAILED
        }
        if (commandApdu.size < HEADER_LENGTH_BYTES) {
            return STATUS_FAILED
        }

        // Get Command APDU header
        return if (commandApdu.contentEquals(SELECT_APDU_HEADER)) {
            STATUS_SUCCESS
        } else { // Own own protocol
            // Extract requester UID
            val requesterUid = String(commandApdu)

            // Return STATUS_FAILED if invalid UID

            // Store uid in accessors of the shared data (file, contact info, etc.)
            fileRepository.addAccessor(filePath!!.split('/')[2], requesterUid)

            // Send access token for the shared data to requester
            filePath?.toByteArray() ?: STATUS_FAILED
        }
    }
}