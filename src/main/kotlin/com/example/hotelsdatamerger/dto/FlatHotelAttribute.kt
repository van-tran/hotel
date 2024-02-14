package com.example.hotelsdatamerger.dto

import com.example.hotelsdatamerger.constants.ContentValueType

class FlatHotelAttribute (
    val refinedName : String,
    val orgName : String,
    val contentValue : AttributeContent,
    val type : ContentValueType
)

