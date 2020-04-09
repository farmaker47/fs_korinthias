package com.george.fs_korinthias.ui

import android.os.Bundle
import android.transition.Explode
import android.transition.Fade
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.PARCEL_TO_PASS
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.ActivityDetailsBinding
import com.george.fs_korinthias.databinding.ContentDetailsBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: DetailsViewModel

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

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val intent = intent
        if (intent.hasExtra(PARCEL_TO_PASS)) {
            val somethingPassed = intent.getParcelableExtra<MainInfo>(PARCEL_TO_PASS)
            Log.i("SOMETHING", somethingPassed.title)
            Log.i("SOMETHING", somethingPassed.image)
            Log.i("SOMETHING", somethingPassed.link)
            Log.i("SOMETHING", somethingPassed.date)

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


    }

}