//
//  SwiftToonEncoder.swift
//  superdiary
//
//  Created by Rafsanjani Abdul-Aziz on 16/11/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import Foundation
import shared
import TOONEncoder

class SwiftToonEncoder : ToonEncoder{
    let encoder = TOONEncoder()
    
    func encode(json: String) -> String? {
        do {
            return String(data: try encoder.encode(json), encoding: .utf8)
        } catch {
            return nil
        }
    }
}
