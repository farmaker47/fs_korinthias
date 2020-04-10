package com.george.fs_korinthias

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.util.Log
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.george.fs_korinthias.ui.DetailsActivity
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
                R.id.navigation_home, R.id.navigation_dashboard
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

    fun fragmentMethodTransition(mainInfo: MainInfo, imageView: ImageView) {
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
}

@Parcelize
data class MainInfo(
    val title: String?,
    val image: String?,
    val link: String?,
    val date: String?
) : Parcelable
