//
//  Environment.swift
//  iosApp
//
//  Created by Rafsanjani Abdul-Aziz on 23/09/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

public enum Environment{
    enum Keys{
        static let SENTRY_BASE_URL = "SENTRY_BASE_URL"
    }
    
    private static let infoDictionary: [String: Any] = {
        guard let dictionary = Bundle.main.infoDictionary else {
            fatalError("Info.plist file not found")
        }
        
        return dictionary
    }()
    
    
    static let sentryBaseUrl: String = {
        guard let baseUrl = Environment.infoDictionary[Keys.SENTRY_BASE_URL] as? String else {
            fatalError("SENTRY_BASE_URL not set in Info.plist")
        }
        
        return baseUrl
    }()
}
