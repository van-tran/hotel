package com.example.hotelsdatamerger.controller

import com.example.hotelsdatamerger.dto.HotelInfo
import com.example.hotelsdatamerger.facade.SearchCriteria
import com.example.hotelsdatamerger.facade.SearchingFacade
import com.example.hotelsdatamerger.model.HotelAttributeTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("hotels")
class HotelController(val hotelSearchingFacade: SearchingFacade) {

	@GetMapping("/v1/hotels")
	suspend fun searchHotelByDestination(@RequestParam destinationID: String): List<HotelInfo> {
		return hotelSearchingFacade.fetchHotels(SearchCriteria.ByDestinationID(destinationID))
	}
	@GetMapping("/v1/configurations/sources")
	suspend fun fetchConfigurationSources(): List<String> {
		return hotelSearchingFacade.fetchSources()
	}
	@GetMapping("/v1/configurations/templates")
	suspend fun fetchConfigurationAttributeTemplates(): List<HotelAttributeTemplate> {
		return hotelSearchingFacade.fetchTemplate()
	}
}