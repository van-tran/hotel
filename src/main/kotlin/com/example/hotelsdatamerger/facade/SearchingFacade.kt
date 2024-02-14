package com.example.hotelsdatamerger.facade

import com.example.hotelsdatamerger.dto.HotelInfo
import com.example.hotelsdatamerger.repo.rest.HotelRetrofitClient
import com.example.hotelsdatamerger.repo.source.IConfigurationRepo
import com.example.hotelsdatamerger.service.IHotelInfoService
import com.example.hotelsdatamerger.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SearchingFacade(
	val jsonUtils: JsonUtils,
	val configurationRepo: IConfigurationRepo,
	val hotelRetrofitClient: HotelRetrofitClient,
	val hotelInfoService: IHotelInfoService) {

	// Initializing instance of Logger for Service
	val logger : Logger = LoggerFactory.getLogger(SearchingFacade::class.java)

	suspend fun fetchSources() = configurationRepo.getSources()
	suspend fun fetchTemplate() = configurationRepo.getHotelInfoTemplate()

	public suspend fun fetchHotels(searchCriteria: SearchCriteria) : List<HotelInfo>{
//		0. fetch hotels from sources
		val attributeTemplate = configurationRepo.getHotelInfoTemplate()
			.map {
				it.name to it
			}
			.toMap()

		configurationRepo.getSources()
			.flatMap { source ->
				hotelRetrofitClient.fetchHotelInfo(source)
					.let {
						// parsing response json into nested & flattened Attributes objects (e.g :  location.address, amenities.general, .. )
						jsonUtils.jsonFileToMap(it.string())
					}
					.let {
						// standardize attribute key name (e.g : facilities -> amenities.other, facilities.general -> amenities.general, ...)
						hotelInfoService.standardizeAttributeKeys(it)
					}
			}.groupBy {
				// group hotel info from different sources
				it.hotelID
			}.mapValues {
				// score & merge attributes with the same attribute name
				hotelInfoService.mergeAttributesFromDifferentSource(it.key, it.value)
			}.let {

			}


//		1. filter by search criteria
//		2. clean & score data
//		3. merge (pick the higher score information)
//		4. cache (store hotelID with hash of hotel info. Then we could quickly check if is there any change on particular hotel by comparing hash)

		return emptyList()
	}


}