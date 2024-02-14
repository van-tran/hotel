package com.example.hotelsdatamerger.service

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject

interface IHotelInfoService {
    suspend fun standardizeAttributeKeys(hotelInfo: List<InfoObject>): List<FlatHotelInfo>
    suspend fun mergeAttributesFromDifferentSource(hotelID: String, lsOfHotelInfo: List<FlatHotelInfo>): FlatHotelInfo
}