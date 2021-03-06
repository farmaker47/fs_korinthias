package com.george.fs_korinthias.ui.detailsNews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainActivity.Companion.PARCEL_TO_PASS
import com.george.fs_korinthias.MainActivityViewModel
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.ActivityDetailsBinding
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    //private lateinit var viewModel: DetailsViewModel

    // Now with DI
    private val viewModel: DetailsViewModel by viewModel()

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
            val newsPassed = intent.getParcelableExtra<MainInfo>(PARCEL_TO_PASS)
            Log.i("SOMETHING", newsPassed.title)
            Log.i("SOMETHING", newsPassed.image)
            Log.i("SOMETHING", newsPassed.link)
            Log.i("SOMETHING", newsPassed.date)
            shareString = newsPassed.link

            // DI setProperty
            getKoin().setProperty("detailsInfo", newsPassed)

            //Picasso.get().load(somethingPassed?.image).into(binding.detailActivityImage)
            /*val viewModelFactory =
                DetailsActivityViewModelFactory(
                    newsPassed,
                    application
                )
            viewModel = ViewModelProvider(
                this, viewModelFactory
            ).get(DetailsViewModel::class.java)*/
            binding.detailsViewModel = viewModel
        }

        binding.imageArrowBack.setOnClickListener {
            onBackPressed()
        }

        binding.fab.setOnClickListener { view ->
            val text = getString(R.string.mainShareNews) + " " + shareString
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.putExtra(Intent.EXTRA_TEXT, text)
            share.type = "text/plain"
            startActivity(Intent.createChooser(share, getString(R.string.shareNews)))
        }

        // Set clickable links to textView
        binding.allNewsBlockTextView.movementMethod = LinkMovementMethod.getInstance()
        //t2.setMovementMethod(LinkMovementMethod.getInstance());


        // Observe if there are images inside page
        /*viewModel.selectedImages.observe(this, Observer { listImages ->

            //Log.i("DETAILS", listImages[1])

            for (image in listImages){
                val ingredient = ImageView(this)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                //params.setMargins(8, 4, 8, 4)
                ingredient.layoutParams = params
                //ripple effect
                val outValue = TypedValue()
                theme
                    .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                ingredient.setBackgroundResource(outValue.resourceId)

                ingredient
                    .setOnClickListener { *//*Toast.makeText(IngredientActivity.this,  urlText, Toast.LENGTH_LONG).show();*//*
                        val myWebLink = Intent(Intent.ACTION_VIEW)
                        myWebLink.data =
                            Uri.parse(image)
                        if (myWebLink.resolveActivity(packageManager) != null) {
                            startActivity(myWebLink)
                        }

                    }
                binding.linearForImages.addView(ingredient)

                // Load image with Picasso
                Picasso.get().load(image).into(ingredient)
            }



        })*/


    }

}