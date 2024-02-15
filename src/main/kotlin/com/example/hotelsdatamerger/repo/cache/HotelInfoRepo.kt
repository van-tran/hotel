package com.example.hotelsdatamerger.repo.cache

import com.example.hotelsdatamerger.entity.HotelInfo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HotelInfoRepo : CrudRepository<HotelInfo, String>