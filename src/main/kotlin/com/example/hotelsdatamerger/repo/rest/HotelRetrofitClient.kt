package com.example.hotelsdatamerger.repo.rest

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path


interface HotelRetrofitClient {
	@GET("/suppliers/{source}")
	suspend fun fetchHotelInfo(@Path("source") source: String) : ResponseBody
}