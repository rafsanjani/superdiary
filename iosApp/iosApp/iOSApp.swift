import SwiftUI
import Sentry

import shared
import CoreLocation
import GoogleMaps

@main
struct iOSApp: App  {
    init() {
        KoinApplication.shared.initialize(
            analytics: AppleAnalytics(),
            logger: AggregateLogger(loggers: [SentryLogger(), KermitLogger()])
        )
        
        GMSServices.provideAPIKey(Environment.googleMapsSdkKey)

        SentrySDK.start { options in
            options.dsn = Environment.sentryBaseUrl
            #if DEBUG
            options.debug = true
            #else
            options.debug = true
            #endif

            options.enableTracing = true
        }
    }


    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
