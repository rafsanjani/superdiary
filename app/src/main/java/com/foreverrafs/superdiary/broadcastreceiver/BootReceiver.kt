package com.foreverrafs.superdiary.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.foreverrafs.superdiary.scheduler.NotificationScheduler
import com.foreverrafs.superdiary.util.DEBUG_TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = DEBUG_TAG

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "onReceive: Device Booted")
            notificationScheduler.scheduleAlarm()
        }
    }
}
