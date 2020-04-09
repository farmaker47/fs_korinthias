package com.george.fs_korinthias.ui.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.george.fs_korinthias.MainActivity
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import com.george.fs_korinthias.ui.important.ImportantViewModel
import com.george.fs_korinthias.ui.important.WeatherApiStatus
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.HttpRetryException
import java.util.HashMap

class NotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        //Log.d(javaClass.simpleName, "Worker Started!")

        fetchImportantNews()




        return Result.success()

        /*return try {

        }catch (exception:HttpException){

        }*/


    }

    private fun showNotification(news: String?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(news)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        const val BASE_URL_IMPORTANT = "http://www.fsk.gr/wordpress/?cat=204&paged=1"
    }

    private fun fetchImportantNews() {

        // Create a Coroutine scope using a job to be able to cancel when needed
        var viewModelJob = Job()

        // the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

        coroutineScope.launch {
            val cookies = HashMap<String, String>()
            try {

                // status loading
                //_status.postValue(WeatherApiStatus.LOADING)
                var toUseArrayList: ArrayList<MainInfo> = ArrayList()

                // 2 pages of important news

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
                        //Log.e("FIRST_LINK", toUseArrayList[0].link)
                        showNotification(toUseArrayList[0].link)

                    }
                }

                //_status.postValue(WeatherApiStatus.DONE)


                withContext(Dispatchers.Main) {
                    // call to UI thread
                    //_status.value = WeatherApiStatus.DONE
                    //_titleList.value = _toUseArrayList
                }


            } catch (e: IOException) {
                Log.e("EXCEPTION", e.toString())
                //_status.postValue(WeatherApiStatus.ERROR)
            }
        }
    }

    private fun checkElement(elem: Element?): Boolean {
        return elem != null
    }


}