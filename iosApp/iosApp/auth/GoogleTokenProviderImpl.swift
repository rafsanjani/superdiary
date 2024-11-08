import shared
import SwiftUI
import GoogleSignIn

class GoogleTokenProviderImpl : GoogleTokenProvider{
    func getGoogleToken() async throws -> String {
        let rootViewController = await (UIApplication.shared.windows.first?.rootViewController)!
        
        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController)
        
        return result.user.idToken?.tokenString ?? "empty token"
    }
}
