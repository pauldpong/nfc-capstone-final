package com.capstone.nfc.ui.search

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.nfc.databinding.ActivitySearchableBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "SearchActivity"

@AndroidEntryPoint
class SearchableActivity: AppCompatActivity() {

//    private val model by viewModels<SearchViewModel>()
//    private lateinit var myFilesAdapter: FileViewAdsapter
//    private lateinit var mySharedFilesAdapter: SharedFileViewAdapter
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySearchableBinding
    public lateinit var searchQuery : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchableBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //verify action and get query
        if(Intent.ACTION_SEARCH == intent.action ){
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchQuery = query
                //once on create ends the fragment will use this query to do the search
            }
        }
    }

}