package com.example.hotelsdatamerger.controller

import com.example.hotelsdatamerger.facade.SearchCriteria
import com.example.hotelsdatamerger.facade.SearchingFacade
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1")
class HotelController(val hotelSearchingFacade: SearchingFacade) {

	@GetMapping("/hotels")
	suspend fun findHotelsByID(@RequestParam hotelIDs: List<String>): List<Map<String,Any>> {
		return hotelSearchingFacade.fetchHotels(SearchCriteria.ByHotelID(hotelIDs))
	}
	@GetMapping("/hotels/destinations")
	suspend fun searchHotelByDestination(@RequestParam destinationIDs: List<String>): List<Map<String,Any>> {
		return hotelSearchingFacade.fetchHotels(SearchCriteria.ByDestinationID(destinationIDs))
	}
	@GetMapping("/v1/configurations/sources")
	suspend fun fetchConfigurationSources(): List<String> {
		return hotelSearchingFacade.fetchSources()
	}


	@Scheduled(fixedRate = 3*60*1000) // every 3 minutes
	suspend fun invalidateCache() {
		hotelSearchingFacade.updateCacheData()
	}

}