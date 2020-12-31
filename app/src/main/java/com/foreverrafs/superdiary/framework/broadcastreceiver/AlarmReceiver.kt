package com.foreverrafs.superdiary.framework.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.framework.presentation.MainActivity
import com.foreverrafs.superdiary.util.DEBUG_TAG
import com.foreverrafs.superdiary.util.INTENT_ACTION_DAILY_ENTRY

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "com.foreverrafs.superdiary"
        private const val NOTIFICATION_CHANNEL_NAME = "superdiary"
        private const val NOTIFICATION_ID = 16
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(DEBUG_TAG, "onReceive: Alarm Received. Dispatching Notification")
        createAndShowNotification(context)
    }

    private fun createAndShowNotification(context: Context) {
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager?.createNotificationChannel(notificationChannel)
        }

        val addDiaryIntent = Intent(context, MainActivity::class.java).let { intent ->
            intent.action = INTENT_ACTION_DAILY_ENTRY
            PendingIntent.getActivity(context, 20, intent, 0)
        }


        val notification = NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle("SuperDiary Reminder")
            .setContentText("How was your day today?")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(
                addDiaryIntent
            )
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)

    }
}