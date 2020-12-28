package com.foreverrafs.superdiary.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foreverrafs.superdiary.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
// TODO: 28/12/20 Switch from AppCompat
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}