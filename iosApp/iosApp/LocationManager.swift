//
//  LocationManager.swift
//  iosApp
//
//  Created by Rafsanjani Abdul-Aziz on 06/11/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import CoreLocation
import shared

class AppleLocationManager : NSObject, CLLocationManagerDelegate{
    let locationManager = CLLocationManager()
    
    func startUpdatingLocation(){
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        
        locationManager.startUpdatingLocation()
    }

    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        locations.forEach { location in
            print("Rafsanjani \(location.coordinate.latitude), \(location.coordinate.longitude)")
        }
    }
    
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Rafsanjani \(error)")
    }
}
