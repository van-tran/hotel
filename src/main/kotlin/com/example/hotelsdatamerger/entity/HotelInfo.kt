package com.example.hotelsdatamerger.entity

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("HotelInfo")
data class HotelInfo(
	@Id
	val hashId : String,
	val hotelInfo: FlatHotelInfo

)
