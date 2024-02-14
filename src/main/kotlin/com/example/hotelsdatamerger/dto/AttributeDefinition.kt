package com.example.hotelsdatamerger.dto

import com.example.hotelsdatamerger.constants.ContentValueType

data class AttributeDefinition (
    val replacement: String,
    val regex: String,
    val virtualNode: Boolean = false
)