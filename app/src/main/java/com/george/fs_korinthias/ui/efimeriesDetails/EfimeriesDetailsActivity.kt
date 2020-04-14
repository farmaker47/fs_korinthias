package com.george.fs_korinthias.ui.efimeriesDetails

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.InfoOnoma
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.PARCEL_TO_PASS
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.ActivityEfimeriesDetailsBinding
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterDetails
import com.google.android.material.snackbar.Snackbar

class EfimeriesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEfimeriesDetailsBinding
    private lateinit var viewModel: EfimeriesDetailsViewModel
    private lateinit var efimeriesPassed: MainEfimeries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEfimeriesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        setSupportActionBar(binding.toolbar)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        if (intent.hasExtra(PARCEL_TO_PASS)) {
            efimeriesPassed = intent.getParcelableExtra<MainEfimeries>(PARCEL_TO_PASS)
            Log.i("SOMETHING", efimeriesPassed.titlePerioxi)

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

            // Setting Header
            binding.textViewTitleEfimeriesDetails.text = efimeriesPassed.titlePerioxi
        }

        binding.efimeriesDetailsRecyclerView.adapter =
            EfimeriesAdapterDetails(
                EfimeriesAdapterDetails.OnClickListener { mainEfimeries ->

                    //(activity as MainActivity?)?.fragmentMethodTransitionEfimeries(mainEfimeries)
                    shareAction(mainEfimeries)
                })

        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        binding.imageArrowBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun shareAction(mainEfimeries: InfoOnoma) {
        val text =
            getString(R.string.efimeriesShare) + " " + efimeriesPassed.titlePerioxi +
                    "\n" + mainEfimeries.imerominia
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.putExtra(Intent.EXTRA_TEXT, text)
        share.type = "text/plain"
        startActivity(Intent.createChooser(share, getString(R.string.shareNews)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_efimeries_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_info -> {
                dialogEfimeriesDetails()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun dialogEfimeriesDetails() {
        val downloadDialog =
            AlertDialog.Builder(this)
        downloadDialog.setTitle(resources.getString(R.string.enimerosiFinal))
        downloadDialog.setMessage(resources.getString(R.string.action_info))
        downloadDialog.setPositiveButton(
            resources.getString(R.string.epilogiOK)
        ) { dialogInterface, i -> //Check for permissions
            /*finish();*/
        }
        /*downloadDialog.setNegativeButton(
                    resources.getString(R.string.epilogiDownload)
                ) { dialogInterface, i -> //Do some actions if user wants to manually insert addres
                    val myWebLink = Intent(Intent.ACTION_VIEW)
                    myWebLink.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=com.george.recipePro")
                    if (myWebLink.resolveActivity(packageManager) != null) {
                        startActivity(myWebLink)
                    }
                }*/
        downloadDialog.show()
    }

}
