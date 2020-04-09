package com.george.fs_korinthias.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.PARCEL_TO_PASS
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: DetailsViewModel
    private var shareString: String? = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            enterTransition = Fade()
        }*/

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        val intent = intent
        if (intent.hasExtra(PARCEL_TO_PASS)) {
            val somethingPassed = intent.getParcelableExtra<MainInfo>(PARCEL_TO_PASS)
            Log.i("SOMETHING", somethingPassed.title)
            Log.i("SOMETHING", somethingPassed.image)
            Log.i("SOMETHING", somethingPassed.link)
            Log.i("SOMETHING", somethingPassed.date)
            shareString = somethingPassed.link

            //Picasso.get().load(somethingPassed?.image).into(binding.detailActivityImage)
            val viewModelFactory = DetailsActivityViewModelFactory(somethingPassed, application)
            viewModel = ViewModelProvider(
                this, viewModelFactory
            ).get(DetailsViewModel::class.java)
            binding.detailsViewModel = viewModel
        }

        binding.imageArrowBack.setOnClickListener {
            onBackPressed()
        }

        binding.fab.setOnClickListener { view ->
            val text = getString(R.string.mainShareNews) + " " + shareString
            val shire = Intent()
            shire.action = Intent.ACTION_SEND
            shire.putExtra(Intent.EXTRA_TEXT, text)
            shire.type = "text/plain"
            startActivity(Intent.createChooser(shire, getString(R.string.shareNews)))
        }


    }

}