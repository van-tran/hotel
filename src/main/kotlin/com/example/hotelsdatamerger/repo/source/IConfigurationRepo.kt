package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.model.HotelAttributeTemplate

interface IConfigurationRepo {
    fun getSources(): List<String>
    fun getHotelInfoTemplate(): List<HotelAttributeTemplate>
}