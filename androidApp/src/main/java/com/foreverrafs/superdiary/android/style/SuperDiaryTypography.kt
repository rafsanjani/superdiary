package com.foreverrafs.superdiary.android.style

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.foreverrafs.superdiary.android.R

val PrincesSofia = FontFamily(
    fonts = listOf(Font(resId = R.font.princess_sofia)),
)

val sourceSansPro = FontFamily(
    fonts = listOf(
        Font(resId = R.font.source_sans_pro, weight = FontWeight.Normal),
        Font(resId = R.font.source_sans_pro_bold, weight = FontWeight.Bold),
    )
)
