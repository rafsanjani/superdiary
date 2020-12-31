package com.foreverrafs.superdiary.framework.broadcastreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import com.foreverrafs.superdiary.util.DEBUG_TAG
import java.util.*

private const val TAG = DEBUG_TAG

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "onReceive: Device Booted")
            scheduleAlarm(context)
        }
    }


    private fun scheduleAlarm(context: Context) {
        Log.d(TAG, "scheduleAlarm: Scheduling Alarm")

        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        val now = Calendar.getInstance()
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            now.timeInMillis,
            1000 * 60 * 1,
            alarmIntent
        )
    }
}
