package com.kotato.multitimelineclient.Push

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.kotato.multitimelineclient.MainActivity
import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.*
import kotlin.concurrent.timer


/**
 * Created by kotato on 2017/07/14.
 */
val REQ_CODE = 9999
val INTENT_KEY_LOCAL_PUSH = "LOCAL_PUSH"
val ACTION_LOCAL_PUSH = "com.kotato.localpush"

class PushService: Service(){

    var timer: Timer = timer("check timeline",initialDelay = 1000 * 60 , period = 1000 * 60){
        val intent = Intent(this@PushService, NotificationReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this@PushService, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        async(CommonPool){
            val timeLine = TwitterService.getMentions {  }
            intent.putExtra("count", timeLine.await().size)
            sender.send()
        }
    }



    override fun onCreate() {
        Log.d("Push Service", "On Create")
        val context = applicationContext

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Push Service", "On Start Command")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("Push Service", "On Destroy")
        timer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Receiver!")
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.tw__ic_logo_white)
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(INTENT_KEY_LOCAL_PUSH, true)
        val builder = NotificationCompat.Builder(context)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.tw__ic_logo_white)//ないと通知が届かない
        builder.setLargeIcon(largeIcon)
        builder.setTicker("ticker")
        builder.setContentTitle("通知が来ました！")
        val count = intent.getIntExtra("count", 0)
        builder.setContentText(" $count 件の通知がきました！")
        val contentIntent = PendingIntent.getActivity(context, REQ_CODE, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        builder.setContentIntent(contentIntent)
        // タップで通知領域から削除する
        builder.setAutoCancel(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

}