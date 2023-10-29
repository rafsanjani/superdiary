package com.foreverrafs.superdiary.diary.parcel

import android.os.Parcel
import kotlinx.datetime.Instant

actual object InstantParceler : CommonParceler<Instant> {
    override fun create(parcel: Parcel): Instant {
        val instant = parcel.readString() ?: throw IllegalArgumentException("Error parsing instant")
        return Instant.parse(instant)
    }

    override fun Instant.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}
