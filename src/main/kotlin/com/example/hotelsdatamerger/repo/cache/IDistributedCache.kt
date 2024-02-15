package com.example.hotelsdatamerger.repo.cache

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject

interface IDistributedCache {
    fun put(hashOfRawData: String, processedHotelInfo: FlatHotelInfo)
    fun get(hashOfRawData: String) : FlatHotelInfo?
    fun putDataBySource(source: String, jsonData: List<FlatHotelInfo>)
    fun getDataBySource(source: String) : List<FlatHotelInfo>?
}