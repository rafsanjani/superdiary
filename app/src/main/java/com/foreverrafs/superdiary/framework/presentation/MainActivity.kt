package com.foreverrafs.superdiary.framework.presentation

import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.framework.broadcastreceiver.BootReceiver
import com.foreverrafs.superdiary.framework.presentation.diarylist.DiaryListFragmentDirections
import com.foreverrafs.superdiary.util.INTENT_ACTION_DAILY_ENTRY
import com.foreverrafs.superdiary.util.INTENT_ACTION_DIARY_NOTIFICATION
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
// TODO: 28/12/20 Switch from AppCompat
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setUpBroadcastReceiver()
        lifecycleScope.launchWhenStarted { }


        //If we are making an entry from the notification, we open the add dialog straight away
        intent?.let {
            if (it.action == INTENT_ACTION_DAILY_ENTRY) {
                findNavController(R.id.navHostFragment).navigate(
                    DiaryListFragmentDirections.actionDiaryListFragmentToAddDiaryDialogFragment()
                )
            }
        }
    }

    private fun setUpBroadcastReceiver() {
        val filter = IntentFilter(INTENT_ACTION_DIARY_NOTIFICATION)
        this.registerReceiver(BootReceiver(), filter)
    }
}