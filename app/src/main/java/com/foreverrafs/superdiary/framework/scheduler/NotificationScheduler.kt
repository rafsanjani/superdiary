package com.foreverrafs.superdiary.framework.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.framework.broadcastreceiver.AlarmReceiver
import com.foreverrafs.superdiary.util.DEBUG_TAG
import java.util.*

private const val TAG = DEBUG_TAG

class NotificationScheduler(private val context: Context, private val prefs: SharedPreferences) {

    private val alarmIntent: PendingIntent =
        Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm() {
        Log.d(TAG, "scheduleAlarm: Scheduling Alarm")
        val alarmHour =
            prefs.getString(context.getString(R.string.pref_key_notification_time), "0")?.toInt()
                ?: 10
        val alarmHour2 = when (alarmHour) {
            12 -> 12
            else -> alarmHour + 12
        }
        val alarmTime = Calendar.getInstance().also {
            it.set(Calendar.HOUR_OF_DAY, alarmHour2)
            it.set(Calendar.MINUTE, 0)
            it.set(Calendar.SECOND, 0)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    fun cancelAlarm() {
        Log.d(TAG, "cancelAlarm: Cancelling Alarm")
        alarmManager.cancel(alarmIntent)
    }
}