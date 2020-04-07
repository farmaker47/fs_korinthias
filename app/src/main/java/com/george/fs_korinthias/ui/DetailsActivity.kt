package com.george.fs_korinthias.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import com.george.fs_korinthias.ui.important.PARCEL_TO_PASS
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val intent = intent
        if (intent.hasExtra(PARCEL_TO_PASS)) {
            val somethingPassed = intent.getParcelableExtra<MainInfo>(PARCEL_TO_PASS)
            Log.e("SOMETHING",somethingPassed.title)
            Log.e("SOMETHING",somethingPassed.image)
            Log.e("SOMETHING",somethingPassed.link)
            Log.e("SOMETHING",somethingPassed.date)

        }
    }

}
