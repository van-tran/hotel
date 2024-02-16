package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.dto.AttributeContentResolver
import com.example.hotelsdatamerger.dto.AttributeDefinition

interface IConfigurationRepo {
    fun getHotelIDKeyName(): String
    fun getSources(): List<String>
    fun getAttributeSet(): List<AttributeDefinition>
    fun getContentMapper(): List<AttributeContentResolver>
}