import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinApplication.shared.initialize(analytics: AppleAnalytics())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
