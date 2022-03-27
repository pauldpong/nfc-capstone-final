package com.capstone.nfc.ui.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.capstone.nfc.R
import com.capstone.nfc.databinding.ActivityWriterBinding
import com.capstone.nfc.services.NFCHCEService

class WriterActivity : AppCompatActivity() {
    private val args: WriterActivityArgs by navArgs()
    private lateinit var dataBinding: ActivityWriterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityWriterBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.sharePrompt.text = getString(R.string.share_prompt, args.file.name)

        val rc = Intent(this, NFCHCEService::class.java)
        rc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //important for Android 10
        rc.putExtra("NFCHCEService.fileUUID", args.file.uuid)
        startService(rc)
    }
}