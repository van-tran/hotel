package com.example.hotelsdatamerger.controller

import com.example.hotelsdatamerger.data.HotelInfo
import com.example.hotelsdatamerger.facade.HotelSearchingFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("hotels")
class HotelController(val hotelSearchingFacade: HotelSearchingFacade) {

	@GetMapping("/v1/{id}")
	suspend fun getHotelByID(@PathVariable id: String): HotelInfo {
		hotelSearchingFacade.findHotelByID("")
		return HotelInfo(id)
	}
}