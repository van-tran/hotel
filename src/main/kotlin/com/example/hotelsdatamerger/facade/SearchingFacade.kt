package com.example.hotelsdatamerger.facade

import com.example.hotelsdatamerger.dto.HotelInfo
import com.example.hotelsdatamerger.repo.rest.HotelRetrofitClient
import com.example.hotelsdatamerger.repo.source.ConfigurationRepoImpl
import com.example.hotelsdatamerger.repo.source.IConfigurationRepo
import com.example.hotelsdatamerger.service.IHotelInfoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SearchingFacade(
	val configurationRepo: IConfigurationRepo,
	val hotelRetrofitClient: HotelRetrofitClient,
	val hotelInfoService: IHotelInfoService) {

	// Initializing instance of Logger for Service
	val logger : Logger = LoggerFactory.getLogger(SearchingFacade::class.java)

	suspend fun fetchSources() = configurationRepo.getSources()
	suspend fun fetchTemplate() = configurationRepo.getHotelInfoTemplate()

	public suspend fun fetchHotels(searchCriteria: SearchCriteria) : List<HotelInfo>{
//		0. fetch hotels from sources

//		1. filter by search criteria
//		2. clean & score data
//		3. merge (pick the higher score information)
//		4. cache (store hotelID with hash of hotel info. Then we could quickly check if is there any change on particular hotel by comparing hash)

		return emptyList()
	}


}