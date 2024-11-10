import SwiftUI
import Sentry

import shared
import GoogleMaps


class AppDelegate : NSObject, UIApplicationDelegate {
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        
        print("opening a resource url")
        return true
    }
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        print("finished launching with options")
        return true
    }
    
}

@main
struct iOSApp: App  {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        KoinApplication.shared.initialize(
            analytics: AppleAnalytics(),
            logger: AggregateLogger(loggers: [SentryLogger(), KermitLogger()]),
            googleTokenProvider: GoogleTokenProviderImpl()
        )
        
        GMSServices.provideAPIKey(Environment.googleMapsSdkKey)
        
        SentrySDK.start { options in
            
            options.dsn = Environment.sentryBaseUrl
#if DEBUG
            options.debug = false
#else
            options.debug = false
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
