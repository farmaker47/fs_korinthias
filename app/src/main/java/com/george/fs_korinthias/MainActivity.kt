package com.george.fs_korinthias

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.transition.Explode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.IdpResponse
import com.george.fs_korinthias.databinding.ActivityMainBinding
import com.george.fs_korinthias.ui.adapters.MainActivityFirebaseMessagesAdapter
import com.george.fs_korinthias.ui.detailsNews.DetailsActivity
import com.george.fs_korinthias.ui.efimeriesDetails.EfimeriesDetailsActivity
import com.george.fs_korinthias.ui.worker.NotificationWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var slidingOpen: Boolean = false
    private lateinit var database: FirebaseDatabase
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var messagesList: ArrayList<FirebaseMainActivityMessages?>

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener: AuthStateListener
    private val providers = arrayListOf(
        EmailBuilder().build(),
        GoogleBuilder().build()
    )
    private lateinit var referenceMainActivityMessages: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
    private var name: String = ""
    private var email = ""
    private var photoUrl = ""
    private val emailVerified: Array<String> = arrayOf(
        "soloupis@gmail.com",
        "farmaker47@gmail.com",
        "dtpharm@gmail.com"
    )
    private lateinit var currentDate: String
    private lateinit var intentFilter: IntentFilter
    private lateinit var receiver: OnNotificationReceived

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            // set an exit transition
            exitTransition = Explode()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        // Init FireBase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()

        // Save first value to SharedPrefs
        //saveTitle()

        // Get intent from notifications
        val intent = intent
        if (intent.action != null) {
            //Log.e("INTENT_ACTION", intent.action)
        }

        if (intent.hasExtra("messages")) {

            //Log.e("MESSAGE_NOTIFICATION", intent.getStringExtra("messages"))


        }

        receiver = OnNotificationReceived()
        intentFilter = IntentFilter()
        intentFilter.addAction("notification_message")

        val viewModelFactory =
            MainActivityViewModelFactory(
                application
            )
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(MainActivityViewModel::class.java)
        binding.viewmodelXml = viewModel

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
            // For authenticated users to read data
            attachDatabaseReadListener()

            openSliding()

            val handler = Handler()
            handler.postDelayed(
                {
                    binding.recyclerMainFireBaseMessages.scrollToPosition(messagesList.size - 1)
                },
                400
            )


            /*val intentB = Intent()
            intentB.action = "notification_message"
            intentB.putExtra("messages", "FAB")
            sendBroadcast(intentB)*/

        }

        binding.imageButtonClose.setOnClickListener {
            closeSliding()
            referenceMainActivityMessages.removeEventListener(childEventListener)
            viewModel.setArratListMainActivityMessages(ArrayList())
        }

        database = Firebase.database
        referenceMainActivityMessages =
            database.reference.child("MainActivity_messages")

        // Listener for button to send messages
        binding.buttonSendMessage.setOnClickListener {

            //.child((1..100).random().toString())
            currentDate =
                SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
            //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            // Write a message to the database
            referenceMainActivityMessages.push().setValue(
                FirebaseMainActivityMessages(
                    name,
                    binding.editTextSlidingMainActivity.text?.trim().toString(),
                    photoUrl,
                    currentDate
                )
            )
            binding.editTextSlidingMainActivity.setText("")
        }

        // First button is disabled
        binding.buttonSendMessage.isEnabled = false
        binding.editTextSlidingMainActivity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (charSequence.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.buttonSendMessage.setImageResource(R.drawable.ic_send_green)
                    binding.buttonSendMessage.isEnabled = true
                } else {
                    binding.buttonSendMessage.setImageResource(R.drawable.ic_send_gray)
                    binding.buttonSendMessage.isEnabled = false
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.editTextSlidingMainActivity.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                DEFAULT_MSG_LENGTH_LIMIT
            )
        )

        binding.recyclerMainFireBaseMessages.adapter =
            MainActivityFirebaseMessagesAdapter(
                MainActivityFirebaseMessagesAdapter.OnClickListener { mainMessages ->

                    //(activity as MainActivity?)?.fragmentMethodTransitionEfimeries(mainEfimeries)
                })

        // Init worker
        initWorker()

        // Auth state listener
        //checkForAuth()
        mAuthStateListener = AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            Log.i("CURRENT_USER", user?.photoUrl.toString())
            if (user != null) {
                //user is signed in
                if (user.email.toString() in emailVerified) {
                    name = user.displayName.toString()
                    email = user.email.toString()
                    photoUrl = user.photoUrl.toString()
                    if (!slidingOpen) {
                        binding.fabMessage.visibility = View.VISIBLE
                    }

                    // If they are registered users then subscribe to topic
                    //subscribeToTopic()
                }

            } else {
                // user is signed out
                // Create and launch sign-in intent
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    RC_SIGN_IN
                )
            }
        }


    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("syllogos")
            .addOnCompleteListener { task ->
                var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed)
                }
                Log.i("Subscription", msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun attachDatabaseReadListener() {
        messagesList = ArrayList()
        childEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // A new comment has been added, add it to the displayed list
                val comment = dataSnapshot.getValue<FirebaseMainActivityMessages>()

                messagesList.add(comment)

                viewModel.setArratListMainActivityMessages(messagesList)

                binding.recyclerMainFireBaseMessages.adapter?.notifyDataSetChanged()
                binding.recyclerMainFireBaseMessages.scrollToPosition(messagesList.size - 1)

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        referenceMainActivityMessages.addChildEventListener(childEventListener)
    }

    /*public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mFirebaseAuth.currentUser
        //updateUI(currentUser)
        Log.e("CURRENT_USER", currentUser?.email.toString())
        currentUser?.let {
            // Name, email address, and profile photo Url
            val name = currentUser.displayName
            val email = currentUser.email
            val photoUrl = currentUser.photoUrl

            // Check if user's email is verified
            val emailVerified = currentUser.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = currentUser.uid
            //val token = FirebaseUser.getToken()
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user?.email.toString() in emailVerified) {
                    name = user?.displayName.toString()
                    email = user?.email.toString()
                    photoUrl = user?.photoUrl.toString()
                    if (!slidingOpen) {
                        binding.fabMessage.visibility = View.VISIBLE
                    }

                    // If they are registered users then subscribe to topic
                    subscribeToTopic()
                }

                // ...
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.error_firebase_auth),
                    Toast.LENGTH_SHORT
                ).show()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i("ERROR_SIGN_IN", response?.getError()?.getErrorCode().toString())
                // ...
                finish()
            }
        }
    }

    private fun checkForAuth() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
        if (slidingOpen) {
            binding.fabMessage.visibility = View.INVISIBLE
            binding.recyclerMainFireBaseMessages.scrollToPosition(messagesList.size - 1)
        }

        //registerReceiver(receiver, filter)
        /*registerReceiver(
            receiver,
            intentFilter
        )*/
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
        //unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(receiver)
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
            referenceMainActivityMessages.removeEventListener(childEventListener)
            viewModel.setArratListMainActivityMessages(ArrayList())
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
        const val DEFAULT_MSG_LENGTH_LIMIT = 1000
        const val RC_SIGN_IN = 14
        const val NOTIFICATION_MESSAGES = "messages"
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

            R.id.action_log_out -> {

                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // ...
                    }

                finish()

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


    /*private val onNotificationReceived: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == "notification_message"){
                Log.e("MESSAGE_Broadcast", intent.getStringExtra("message"))
            }
        }
    }*/


}

class OnNotificationReceived : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        /*Toast.makeText(
            context, "Broadcast Intent Detected.",
            Toast.LENGTH_LONG
        ).show()*/
        // an Intent broadcast.
        if (intent.action == "notification_message") {
            //Log.e("MESSAGE_Broadcast", intent.getStringExtra("messages"))
        }
        //throw UnsupportedOperationException("Not yet implemented")
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


data class FirebaseMainActivityMessages(
    val name: String? = "",
    val message: String? = "",
    val photo: String? = "",
    val date: String? = ""
)
