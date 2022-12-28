package com.foreverrafs.superdiary

import db.KmpSuperDiaryDB


expect class LocalDatabaseFactory {
    companion object {
        fun getSuperDiaryDB(): KmpSuperDiaryDB
    }
}