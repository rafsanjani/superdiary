import Foundation
import shared
import Sentry

// Use sentry to log errors and exceptions
class SentryLogger: Logger {
    func d(tag: String, message: @escaping () -> String) {
        // no-op
    }

    func i(tag: String, message: @escaping () -> String) {
        // no-op
    }

    func v(tag: String, throwable: KotlinThrowable?, message: @escaping () -> String) {
        // no-op
    }

    func w(tag: String, throwable: KotlinThrowable?, message: @escaping () -> String) {
        // no-op
    }

    func e(tag: String, throwable: KotlinThrowable?, message: @escaping () -> String) {
        if let throwable = throwable {
            SentrySDK.capture(error: throwable.asError())
        } else {
            SentrySDK.capture(message: message())
        }
    }
}
