package com.example.hotelsdatamerger.dto

data class SerializableHotelInfo @JvmOverloads constructor(
    var hotelID : String,
    var content: Map<String, Any?>
)