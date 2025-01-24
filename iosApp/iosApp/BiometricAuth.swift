//
//  BiometricAuth.swift
//  superdiary
//
//  Created by Rafsanjani Abdul-Aziz on 24/01/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared
import LocalAuthentication

class AppleBiometricAuth : BiometricAuth{
    let context = LAContext()
    
    func canAuthenticate() -> Bool {
        context.localizedCancelTitle = "Cancel"
        
        var error: NSError?
        guard context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            print(error?.localizedDescription ?? "Can't evaluate policy")
            return false
        }
        
        return true
    }
    
   
    func startBiometricAuth() async throws -> any BiometricAuthAuthResult {
        do {
            try await context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: "Log in to your account")
            return BiometricAuthAuthResultSuccess()
        } catch let error {
            print(error.localizedDescription)
            return BiometricAuthAuthResultFailed()
        }
    }
    
}
