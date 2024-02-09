package com.example.hotelsdatamerger.service

import com.example.hotelsdatamerger.facade.SearchingFacade
import com.example.hotelsdatamerger.repo.rest.HotelRetrofitClient
import com.example.hotelsdatamerger.repo.source.IConfigurationRepo
import com.example.hotelsdatamerger.utils.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HotelInfoServiceImpl(
    val jsonUtils: JsonUtils,
    val hotelHotelInfoSourceRepo: IConfigurationRepo,
    val hotelRetrofitClient: HotelRetrofitClient
) : IHotelInfoService {

    // Initializing instance of Logger for Service
    val logger : Logger = LoggerFactory.getLogger(SearchingFacade::class.java)


    public suspend fun findHotelByID(id : String){
        hotelRetrofitClient.fetchHotelInfo("paperflies")
            .also {
                logger.info("response : " + it.string())
            }
    }
    public suspend fun fetchHotelsFromSource(sourceId : String){
        hotelRetrofitClient.fetchHotelInfo(sourceId)
            .let {  jsonUtils.jsonFileToMapGson(it.string()) }
            .also {
                logger.info("response : $it")
            }
    }
    public suspend fun fetchHotels(){
        hotelHotelInfoSourceRepo.getSources()
            .flatMap { sourceId ->
                hotelRetrofitClient.fetchHotelInfo(sourceId)
                    .let {  jsonUtils.jsonFileToMapGson(it.string()) }
                    .also {
                        logger.info("response : $it")
                    }
            }

    }
}