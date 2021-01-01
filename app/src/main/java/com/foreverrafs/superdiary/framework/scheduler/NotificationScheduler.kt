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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

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


        val time = LocalDateTime.of(LocalDate.now(), LocalTime.of(alarmHour + 12, 0, 0))

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time.toInstant(ZoneOffset.UTC).toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
    }

    fun cancelAlarm() {
        Log.d(TAG, "cancelAlarm: Cancelling Alarm")
        alarmManager.cancel(alarmIntent)
    }
}