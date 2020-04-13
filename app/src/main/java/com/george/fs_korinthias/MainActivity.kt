package com.george.fs_korinthias

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.transition.Explode
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.george.fs_korinthias.ui.detailsNews.DetailsActivity
import com.george.fs_korinthias.ui.efimeriesDetails.EfimeriesDetailsActivity
import com.george.fs_korinthias.ui.worker.NotificationWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

const val PARCEL_TO_PASS = "parcel_to_pass"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            exitTransition = Explode()
        }

        setContentView(R.layout.activity_main)

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

        // Init worker
        initWorker()

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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresBatteryNotLow(true)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.MINUTES)
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

            else -> super.onOptionsItemSelected(item)
        }
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