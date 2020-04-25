package com.george.fs_korinthias.messages_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.george.fs_korinthias.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.regex.Matcher
import java.util.regex.Pattern

class MyFireBaseMessagesService : FirebaseMessagingService() {

    private lateinit var intent: Intent

    override fun onNewToken(p0: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Handle messages here
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification?.title,
                remoteMessage.notification?.body,
                (0..1000).random()
            )
        }
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i("MessagesReceived", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.i("MESSAGES_DATA", "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.

                //      scheduleJob()
            } else {
                // Handle message within 10 seconds

                //     handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.i("MESSAGES_PAYLOAD", "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onDeletedMessages() {


    }

    private fun showNotification(title: String?, body: String?, idNotification: Int) {
        //val intent = Intent(this, MainActivity::class.java)
        //var intent = Intent()

        /*val intentB = Intent()
        intentB.action = "notification_message"
        intentB.putExtra("messages", body)
        sendBroadcast(intentB)*/

        if (body!!.contains("http")){
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(extractUrls(body)[0])
        }else{
            intent = Intent()
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri =
            /*Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName +
                    "/" + R.raw.inflicted)*/RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            //.setColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            .setColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            //.setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        // To play sound you have to engage to channel
        val attributes: AudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.setSound(defaultSoundUri, attributes)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(idNotification, notificationBuilder.build())
        Log.i("NOTIF_NUMBER", idNotification.toString())

        // If no sound is heard
        //playNotificationSound()

        // wake up screen
        wakeScreen()






        //intent.putExtra(EXTRA_EPILOGI_TO_FARMAKO_FRAGMENT,String.valueOf(linearDummy.getId()));
        //sendBroadcast(intent);
    }

    private fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.inflicted
            )
            val r = RingtoneManager.getRingtone(this, alarmSound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun wakeScreen() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isInteractive // check if screen is on

        if (!isScreenOn) {
            val wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myApp:notificationLock"
            )
            wl.acquire(3000) //set your time in milliseconds
        }
    }

    private fun extractUrls(text: String): List<String> {
        val containedUrls: MutableList<String> =
            ArrayList()
        val urlRegex =
            "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)
        while (urlMatcher.find()) {
            containedUrls.add(
                text.substring(
                    urlMatcher.start(0),
                    urlMatcher.end(0)
                )
            )
        }
        return containedUrls
    }
}