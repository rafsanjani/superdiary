import shared
import SwiftUI
import GoogleSignIn

class GoogleTokenProviderImpl : GoogleTokenProvider{
    func getGoogleToken() async throws -> String {
        
        let keyWindow = performOnMainThread{
            var result: UIWindow? {
              let allScenes = UIApplication.shared.connectedScenes
              for scene in allScenes {
                guard let windowScene = scene as? UIWindowScene else { continue }
                for window in windowScene.windows where window.isKeyWindow {
                   return window
                 }
               }
                return nil
            }
            return result
        }
       
        
        guard let rootViewController = await keyWindow?.rootViewController else { throw GoogleTokenProviderException(message: "Could not find root view controller") }
        
        
        return try await signInWithGoogle(rootViewController: rootViewController)
    }
    
    @MainActor
    func signInWithGoogle(rootViewController: UIViewController) async throws -> String {
        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController)
        

        guard let token = result.user.idToken?.tokenString else {
            throw GoogleTokenProviderException(message: "Error getting token from Google Sign in result")
        }

        return token
    }
    
    func performOnMainThread<T>(_ action:() -> T)-> T{
        return DispatchQueue.main.sync{
            return action()
        }
    }
}

class GoogleTokenProviderException: LocalizedError, Error{
    let message: String
    
    init(message: String) {
        self.message = message
    }
    
   var errorDescription: String? {
        message
    }
}
