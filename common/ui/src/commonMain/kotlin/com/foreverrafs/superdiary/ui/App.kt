package com.foreverrafs.superdiary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.foreverrafs.superdiary.resources.MR
import dev.icerock.moko.resources.compose.fontFamilyResource

@Composable
fun App() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Cyan) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text("Styled font", fontFamily = fontFamilyResource(MR.fonts.Sofia.regular))
                Text("Regular font")
            }
        }
    }
}
