package com.example.hotelsdatamerger.dto

data class AttributeContentResolver(
    val attrNameRegex: String,
    val regex: String,
    val replacement: String
)