package com.example.myapitest.model

import androidx.room.Embedded
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    var id: String? = null,
    val imageUrl: String,
    val year: String,
    val name: String,
    val licence: String,
    val place: Place
) :Parcelable

@Parcelize
data class Place(
    val lat: Float,
    val long: Float
) :Parcelable