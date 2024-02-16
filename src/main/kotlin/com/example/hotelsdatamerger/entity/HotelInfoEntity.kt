package com.example.hotelsdatamerger.entity

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("HotelInfo")
data class HotelInfoEntity(
	@Id
	val hashId : String,
	val hotelInfo: FlatHotelInfo

)
