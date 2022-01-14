package com.foreverrafs.superdiary

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.broadcastreceiver.BootReceiver
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryNavHost
import com.foreverrafs.superdiary.util.INTENT_ACTION_DIARY_NOTIFICATION
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpBroadcastReceiver()

        setContent {
            SuperDiaryNavHost(
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }

    private fun setUpBroadcastReceiver() {
        val filter = IntentFilter(INTENT_ACTION_DIARY_NOTIFICATION)
        this.registerReceiver(BootReceiver(), filter)
    }
}