package com.example.hotelsdatamerger.dto

@kotlinx.serialization.Serializable
class FlatHotelInfo(
    val hotelID : String,
    val content: InfoObject
)