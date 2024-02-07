package com.example.hotelsdatamerger.facade

import com.example.hotelsdatamerger.repo.rest.HotelRetrofitClient
import okhttp3.internal.filterList
import org.springframework.stereotype.Service

@Service
class HotelSearchingFacade(val hotelRetrofitClient: HotelRetrofitClient) {



	public suspend fun findHotelByID(id : String){
		hotelRetrofitClient.fetchHotelInfo("")
	}
}