//
//  SwiftNativeViewFactory.swift
//  superdiary
//
//  Created by Rafsanjani Abdul-Aziz on 02/08/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

class SwiftNativeViewFactory : NativeViewFactory{
    static var shared = SwiftNativeViewFactory()
    
    func createGoogleMap(latitude: Double, longitude: Double) -> UIViewController {
        let view = GoogleMap(latitude: latitude, longitude: longitude)
        return UIHostingController(rootView: view)
    }
}
