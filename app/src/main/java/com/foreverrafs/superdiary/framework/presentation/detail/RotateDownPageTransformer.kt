package com.foreverrafs.superdiary.framework.presentation.detail

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class RotateDownPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val rotation = -15f * position

        page.pivotX = page.width * 0.5f
        page.pivotY = 0f
        page.translationX = 0f
        page.rotation = rotation;
    }
}