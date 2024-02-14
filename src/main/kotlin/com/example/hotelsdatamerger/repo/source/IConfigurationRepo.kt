package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.dto.AttributeDefinition
import com.example.hotelsdatamerger.model.HotelAttributeTemplate

interface IConfigurationRepo {
    fun getHotelIDKeyName(): String
    fun getSources(): List<String>
    fun getHotelInfoTemplate(): List<HotelAttributeTemplate>
    fun getAttributeSet(): List<AttributeDefinition>
}