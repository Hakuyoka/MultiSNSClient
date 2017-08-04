package com.kotato.multitimelineclient.Push

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.kotato.multitimelineclient.MainActivity
import com.kotato.multitimelineclient.OrmaHolder
import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.kotato.multitimelineclient.TimeLine.TIME_LINE_TYPE
import com.kotato.multitimelineclient.TimeLine.getTimeList
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.concurrent.timer


/**
 * Created by kotato on 2017/07/14.
 */
val REQ_CODE = 9999
val INTENT_KEY_LOCAL_PUSH = "LOCAL_PUSH"
val ACTION_LOCAL_PUSH = "com.kotato.localpush"

object MaxIdHolder {
    var maxId: Long? = null
}

class PushService: Service(){

    companion object {
        var maxId: Long? = null
    }

    lateinit var timer: Timer


    override fun onCreate() {
        Log.d("Push Service", "On Create")
        //親が死んだ時にリセットされるためサービス独自のものを持っておく
        //init Twitter
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .debug(true)
                .build()
        Twitter.initialize(config)

        //init Orma
        OrmaHolder.initialize(this)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Push Service", "On Start Command")

        val mentions = getTimeList(TwitterCore.getInstance().sessionManager.activeSession.userId, TIME_LINE_TYPE.MENTION.id)
        maxId = mentions.maxBy { it.id }?.id

        Log.i("Push Service", "Timer Start")
        timer = timer("check timeline", initialDelay = 1000 * 10, period = 1000 * 60 * 15) {
            val context = this@PushService
            val intent = Intent(context, NotificationReceiver::class.java)
            val sender = PendingIntent.getBroadcast(context, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            //CommonPoolのなから@付きのthisは見れない？
            val timeLine = TwitterService.getMentions(maxId)
            launch(CommonPool) {
                if (timeLine.await().size > 0) {
                    intent.putExtra("count", timeLine.await().size)
                    sender.send(context, REQ_CODE, intent)
                    maxId = timeLine.await().maxBy { it.id }?.id
                }
            }

        }

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