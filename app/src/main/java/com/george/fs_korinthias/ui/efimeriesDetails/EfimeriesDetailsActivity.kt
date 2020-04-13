package com.george.fs_korinthias.ui.efimeriesDetails

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.*
import com.george.fs_korinthias.databinding.ActivityEfimeriesDetailsBinding
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterDetails
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterMain
import com.george.fs_korinthias.ui.detailsNews.DetailsActivityViewModelFactory
import com.george.fs_korinthias.ui.detailsNews.DetailsViewModel

class EfimeriesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEfimeriesDetailsBinding
    private lateinit var viewModel: EfimeriesDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEfimeriesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent.hasExtra(PARCEL_TO_PASS)) {
            val efimeriesPassed = intent.getParcelableExtra<MainEfimeries>(PARCEL_TO_PASS)
            Log.e("SOMETHING", efimeriesPassed.titlePerioxi)

            //Picasso.get().load(somethingPassed?.image).into(binding.detailActivityImage)
            val viewModelFactory =
                EfimeriesDetailsViewModelFactory(
                    efimeriesPassed,
                    application
                )
            viewModel = ViewModelProvider(
                this, viewModelFactory
            ).get(EfimeriesDetailsViewModel::class.java)
            binding.efimeriesViewModel = viewModel
        }

        binding.efimeriesDetailsRecyclerView.adapter =
            EfimeriesAdapterDetails(
                EfimeriesAdapterDetails.OnClickListener { mainEfimeries ->

                    //(activity as MainActivity?)?.fragmentMethodTransitionEfimeries(mainEfimeries)
                })

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.home -> {

                onBackPressed()

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
