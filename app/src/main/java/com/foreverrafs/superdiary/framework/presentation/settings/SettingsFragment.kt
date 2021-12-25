package com.foreverrafs.superdiary.framework.presentation.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.framework.scheduler.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.background = ContextCompat.getDrawable(requireContext(), R.color.bg_light)

        return view
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.pref_key_notification_time)) {
            notificationScheduler.cancelAlarm()
            notificationScheduler.scheduleAlarm()
        } else if (key == getString(R.string.pref_key_enable_notifications)) {
            val shouldEnableNotifications = sharedPreferences.getBoolean(key, true)

            if (shouldEnableNotifications)
                notificationScheduler.scheduleAlarm()
            else
                notificationScheduler.cancelAlarm()
        }
    }
}