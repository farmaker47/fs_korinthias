package com.george.fs_korinthias

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.transition.Explode
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.george.fs_korinthias.databinding.ActivityMainBinding
import com.george.fs_korinthias.ui.detailsNews.DetailsActivity
import com.george.fs_korinthias.ui.efimeriesDetails.EfimeriesDetailsActivity
import com.george.fs_korinthias.ui.worker.NotificationWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var slidingOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            exitTransition = Explode()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save first value to SharedPrefs
        //saveTitle()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.fabMessage.setOnClickListener {
            //Toast.makeText(this,"LIKE",Toast.LENGTH_LONG).show()
            openSliding()

        }

        binding.imageButtonClose.setOnClickListener {
            closeSliding()
        }

        binding.someDummyText?.setOnClickListener{
            //Toast.makeText(this,"SOME",Toast.LENGTH_LONG).show()
        }

        // Init worker
        initWorker()

    }

    private fun openSliding() {
        val bottomUp: Animation = AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_up
        )
        binding.slidingLayout.startAnimation(bottomUp)
        binding.slidingLayout.visibility = View.VISIBLE

        binding.fabMessage.visibility = View.INVISIBLE
        slidingOpen = true
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (slidingOpen) {
            closeSliding()
        } else {
            finish()
        }
    }

    private fun closeSliding() {
        val bottomDown: Animation = AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_down
        )
        binding.slidingLayout.startAnimation(bottomDown)
        binding.slidingLayout.visibility = View.GONE

        slidingOpen = false

        val handler = Handler()
        handler.postDelayed(
            {
                binding.fabMessage.visibility = View.VISIBLE
            },
            400
        )
    }

    private fun saveTitle() {
        val sharedPref =
            getSharedPreferences(getString(R.string.save_first_news_article), Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString(
                getString(R.string.save_first_news_article),
                getString(R.string.notification_message)
            )
            commit()
        }
    }

    fun fragmentMethodTransitionNews(mainInfo: MainInfo, imageView: ImageView) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(
            PARCEL_TO_PASS,
            mainInfo
        )
        // bundle for the transition effect
        /*Log.i("transition", imageView.transitionName)
        val bundle: Bundle? = ActivityOptionsCompat
            .makeSceneTransitionAnimation(
                this,
                imageView,
                imageView.transitionName
            ).toBundle()
        startActivity(intent, bundle)*/

        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            // Swap without transition
            startActivity(intent)
        }
    }

    fun fragmentMethodTransitionEfimeries(mainEfimeries: MainEfimeries) {
        val intent = Intent(this, EfimeriesDetailsActivity::class.java)
        intent.putExtra(
            PARCEL_TO_PASS,
            mainEfimeries
        )
        // bundle for the transition effect
        /*Log.i("transition", imageView.transitionName)
        val bundle: Bundle? = ActivityOptionsCompat
            .makeSceneTransitionAnimation(
                this,
                imageView,
                imageView.transitionName
            ).toBundle()
        startActivity(intent, bundle)*/

        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Apply activity transition
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            // Swap without transition
            startActivity(intent)
        }
    }

    private fun initWorker() {

        val sharedPref = getSharedPreferences(
            getString(R.string.save_first_news_article),
            Context.MODE_PRIVATE
        )

        val firstNews =
            sharedPref.getString(
                getString(R.string.save_first_news_article),
                getString(R.string.notification_message)
            )
        Log.i("WORKER_INIT", firstNews)
        val constraints = Constraints.Builder()
            //.setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresBatteryNotLow(true)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            JOB_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }

    companion object {
        const val JOB_TAG = "notificationWorkTag"
        const val PARCEL_TO_PASS = "parcel_to_pass"
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
            R.id.action_policy -> {

                val myWebLink = Intent(Intent.ACTION_VIEW)
                myWebLink.data =
                    Uri.parse("https://docs.google.com/document/d/12DoY1JXV7-omRIhPPlk-cXEVyDa9Ctpwc46g6KVV_Ek/edit?usp=sharing")
                if (myWebLink.resolveActivity(packageManager) != null) {
                    startActivity(myWebLink)
                }

                return true
            }

            R.id.action_epikoinonia -> {

                epikoinoniaDialog()

                return true
            }

            R.id.action_apostoli -> {

                shareApp()

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareApp() {
        val text = getString(R.string.mainShareApp)
        val shire = Intent()
        shire.action = Intent.ACTION_SEND
        shire.putExtra(Intent.EXTRA_TEXT, text)
        shire.type = "text/plain"
        startActivity(Intent.createChooser(shire, getString(R.string.shareApplication)))
    }

    private fun epikoinoniaDialog() {
        val downloadDialog =
            AlertDialog.Builder(this)
        downloadDialog.setTitle(resources.getString(R.string.epikoinoniaMeSyllogo))
        downloadDialog.setMessage(resources.getString(R.string.epikoinoniaTilefona))
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

@Parcelize
data class MainInfo(
    val title: String?,
    val image: String?,
    val link: String?,
    val date: String?
) : Parcelable

@Parcelize
data class MainEfimeries(
    val titlePerioxi: String?,
    val titleInfo: ArrayList<InfoOnoma>?
) : Parcelable

@Parcelize
data class InfoOnoma(
    val imerominia: String?,
    val onoma: String?,
    val tilefono: String?
) : Parcelable
