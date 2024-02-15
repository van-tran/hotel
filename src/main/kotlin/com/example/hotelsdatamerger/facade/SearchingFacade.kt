package com.example.hotelsdatamerger.facade

import com.example.hotelsdatamerger.dto.AttributeContent
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
    val hotelInfoService: IHotelInfoService
) {

    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(SearchingFacade::class.java)

    suspend fun fetchSources() = configurationRepo.getSources()
    suspend fun fetchTemplate() = configurationRepo.getHotelInfoTemplate()

    public suspend fun fetchHotels(searchCriteria: SearchCriteria): List<Map<String, Any>> {
//		0. fetch hotels from sources
        return configurationRepo.getSources()
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
            }.mapValues { (hotelID, lstOfData) ->
                // score & merge attributes with the same attribute name
                hotelInfoService.mergeAttributesFromDifferentSource(hotelID, lstOfData)
            }.let { mapOfHotels ->
                // filter by hotel id
                when (searchCriteria) {
                    is SearchCriteria.ByHotelID -> mapOfHotels
                        .filterValues { hotelInfo ->
                            searchCriteria.hoteIDs.contains(hotelInfo.hotelID)
                        }
                        .values.toList()

                    is SearchCriteria.ByDestinationID -> mapOfHotels.values.filter {
                        it.content.attributes.any {
                            it.name == "destination"
                                    && it is AttributeContent.PlainString<*>
                                    && searchCriteria.destIDs.contains(it.value)
                        }
                    }
                }
            }.map {
                jsonUtils.attributeToJsonObject(it.content.attributes)
            }

    }


}