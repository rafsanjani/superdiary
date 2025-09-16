package com.foreverrafs.superdiary.design

import platform.UIKit.UIViewController

interface NativeViewFactory {
    fun createGoogleMap(latitude: Double, longitude: Double): UIViewController
}
