package com.example.hotelsdatamerger.repo.cache

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject

interface IDistributedCache {
    fun put(hashOfRawData: String, processedHotelInfo: FlatHotelInfo)
    fun get(hashOfRawData: String) : FlatHotelInfo?
    fun putMergedData(jsonData: Map<String, FlatHotelInfo>)
    fun getMergedData(): Map<String, FlatHotelInfo>?
}