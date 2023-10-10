import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinApplication.shared.initialize()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
