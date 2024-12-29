import Foundation
import GoogleMaps
import SwiftUI
import shared

struct GoogleMap : UIViewRepresentable {
    let location: Location
    func makeUIView(context: Context) -> GMSMapView {
        let options = GMSMapViewOptions()
        
        options.camera = GMSCameraPosition.camera(
            withLatitude: location.latitude,
            longitude: location.longitude,
            zoom: 11.0
        )
        

        let mapView = GMSMapView(options: options)
        
        mapView.settings.allowScrollGesturesDuringRotateOrZoom = false
        mapView.settings.setAllGesturesEnabled(false)
        
        
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(
            latitude: location.latitude,
            longitude: location.longitude
        )
        
        marker.map = mapView
        
        
        return mapView
    }
    
    func updateUIView(_ uiView: GMSMapView, context: Context) {
        // required but no-op
    }
}
