package com.example.hotelsdatamerger.repo.cache

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject
import com.example.hotelsdatamerger.entity.HotelInfo
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class DistributedCacheImpl(
    val hotelRepo: HotelInfoRepo,
    val redisTemplate: RedisTemplate<String, Any>
) : IDistributedCache {


    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(DistributedCacheImpl::class.java)

    private lateinit var hashOperations: HashOperations<String, String, List<FlatHotelInfo>>

    @PostConstruct
    fun init() {
        this.hashOperations = redisTemplate.opsForHash()
    }

    override fun put(hashOfRawData: String, processedHotelInfo: FlatHotelInfo) {
        hotelRepo.save(HotelInfo(hashOfRawData, processedHotelInfo))
            .also {
                logger.info("saved {} with hash {}", processedHotelInfo, hashOfRawData)
            }
    }

    override fun get(hashOfRawData: String): FlatHotelInfo? {
        return hotelRepo.findById(hashOfRawData)
            .map { it.hotelInfo }
            .getOrNull()
            ?.also {
                logger.info("loaded from cache hotel info {} with hash {}", it, hashOfRawData)
            }

    }

    override fun putDataBySource(source: String, jsonData: List<FlatHotelInfo>) {
        hashOperations.put(RAW_DATA, source, jsonData)
    }

    override fun getDataBySource(source: String): List<FlatHotelInfo>? {
        return hashOperations.get(RAW_DATA, source)
    }

    companion object {
        const val RAW_DATA = "raw_data"
    }
}