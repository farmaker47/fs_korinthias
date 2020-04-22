package com.george.fs_korinthias.ui.worker

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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.george.fs_korinthias.MainActivity
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException


class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val sharedPref = context.getSharedPreferences(
        context.getString(R.string.save_first_news_article),
        Context.MODE_PRIVATE
    )

    private var idChannelForNotifications: Int = 0

    override suspend fun doWork(): Result {
        //Log.d(javaClass.simpleName, "Worker Started!")

        //fetchImportantNews()

        return try {
            fetchImportantNews()
            Log.i("WORKER_SUCCESS", "Success")
            Result.success()
        } catch (exception: Exception) {
            Log.i("WORKER_RETRY", "Retry")
            Result.retry()
        }

        /*return try {

        }catch (exception:HttpException){

        }*/


    }

    private fun showNotification(news: String?, idNotification: Int) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ applicationContext.packageName + "/" + R.raw.inflicted)//RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(news)
            .setStyle(NotificationCompat.BigTextStyle().bigText(news))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        playNotificationSound()
    }

    companion object {
        const val BASE_URL_IMPORTANT = "http://www.fsk.gr/wordpress/?cat=40&paged=1"
    }

    private fun fetchImportantNews() {

        // Create a Coroutine scope using a job to be able to cancel when needed
        val viewModelJob = Job()

        // the Coroutine runs using the dispatcher
        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

        coroutineScope.launch {
            val cookies = HashMap<String, String>()
            try {

                val toUseArrayList: ArrayList<MainInfo> = ArrayList()

                val importantResponse =
                    Jsoup.connect(BASE_URL_IMPORTANT)
                        .method(Connection.Method.GET)
                        .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        .execute()
                cookies.putAll(importantResponse.cookies())
                val doc = importantResponse.parse()
                //check if element exists
                if (checkElement(doc.select(".main-content-column-1").first())) {
                    val image = doc.select(".main-content-column-1").select(".blog-block-2")
                        .select(".items").select(".image")
                    for ((index, element) in image.withIndex()) {
                        val datePlus =
                            doc.select(".blog-block-2").select(".items").select(".title")
                                .select(".updated").text() + " "
                        val date = datePlus.substring(index * 16, index * 16 + 15)
                        val generalElement = MainInfo(
                            element.select(".image").select("img[alt]").attr("alt"),
                            element.select(".image").select("img[src]").attr("src"),
                            element.select(".image").select("a[href]").attr("href"),
                            date
                        )
                        toUseArrayList.add(generalElement)
                    }

                    //Fetch first news link from SharedPrefs
                    val firstNews =
                        sharedPref.getString(
                            context.getString(R.string.save_first_news_article),
                            context.getString(R.string.notification_message)
                        )
                    Log.i("WORKER_BEFORE", firstNews)
                    //Toast.makeText(context, "YEAP", Toast.LENGTH_LONG).show()
                    Log.i("NOTIFIIII", (0..100).random().toString())

                    // Check if news are not the same
                    if (firstNews != toUseArrayList[0].link) {
                        // Show notification
                        idChannelForNotifications = (0..100).random()
                        showNotification(
                            toUseArrayList[0].title,
                            idChannelForNotifications

                        )

                        // And save the new link
                        with(sharedPref.edit()) {
                            putString(
                                context.getString(R.string.save_first_news_article),
                                toUseArrayList[0].link
                            )
                            commit()
                        }

                        Log.i("WORKER_AFTER", toUseArrayList[0].link)
                    }
                }


                withContext(Dispatchers.Main) {
                    // call to UI thread
                    //Toast.makeText(context, "YEAP THIS WORKER SHOWS LINKS", Toast.LENGTH_LONG).show()
                    //_status.value = WeatherApiStatus.DONE
                    //_titleList.value = _toUseArrayList
                }


            } catch (e: IOException) {
                Log.e("EXCEPTION_WORKER", e.toString())
            }
        }
    }

    private fun checkElement(elem: Element?): Boolean {
        return elem != null
    }

    private fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ applicationContext.packageName + "/" + R.raw.inflicted
            )
            val r = RingtoneManager.getRingtone(context, alarmSound)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}