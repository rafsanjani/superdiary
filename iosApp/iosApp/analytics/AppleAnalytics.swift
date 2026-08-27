import Foundation
import shared
import Sentry

class AppleAnalytics: AnalyticsTracker {
    func trackEvent(event: AnalyticsEvent) {
        let breadcrumb = Breadcrumb(level: .info, category: "analytics")
        breadcrumb.type = "user"
        breadcrumb.message = event.name
        breadcrumb.data = ["parameters": event.parameters.description]
        SentrySDK.addBreadcrumb(breadcrumb)
    }
}
