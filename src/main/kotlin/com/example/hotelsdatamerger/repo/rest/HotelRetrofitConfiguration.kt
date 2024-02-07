package com.example.hotelsdatamerger.repo.rest

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Configuration
class HotelRetrofitConfiguration {


	@Value("\${source.host}")
	lateinit var url: String


	@Bean
	fun okHttpClient(): OkHttpClient {
		return OkHttpClient.Builder()
			.addInterceptor(
				HttpLoggingInterceptor()
					.setLevel(HttpLoggingInterceptor.Level.BASIC)
			)
			.build()
	}

	@Bean
	@Qualifier("HotelRetrofitClient")
	fun hotelRetrofitClient(client: OkHttpClient, gson: Gson): HotelRetrofitClient {
		return Retrofit.Builder()
			.baseUrl(url)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create(gson))
			.build()
			.create(HotelRetrofitClient::class.java)
	}

	@Bean
	fun gson(): Gson {
		return GsonBuilder().setLenient().create()
	}
}