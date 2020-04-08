package com.george.fs_korinthias

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.george.fs_korinthias.ui.DetailsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.parcel.Parcelize

const val PARCEL_TO_PASS = "parcel_to_pass"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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




    }

    fun FragmentMethodTransition(mainInfo: MainInfo,imageView: ImageView){
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(
            PARCEL_TO_PASS,
            mainInfo
        )
        // bundle for the transition effect
        Log.i("transition", imageView.transitionName)
        val bundle: Bundle? = ActivityOptionsCompat
            .makeSceneTransitionAnimation(
                this,
                imageView,
                imageView.transitionName
            ).toBundle()
        startActivity(intent, bundle)

    }
}

@Parcelize
data class MainInfo(
    val title: String?,
    val image: String?,
    val link: String?,
    val date: String?
) : Parcelable
