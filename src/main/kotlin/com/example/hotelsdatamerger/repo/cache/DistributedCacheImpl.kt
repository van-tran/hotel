package com.example.hotelsdatamerger.repo.cache

import com.example.hotelsdatamerger.dto.FlatHotelInfo
import com.example.hotelsdatamerger.dto.InfoObject
import com.example.hotelsdatamerger.dto.SerializableHotelInfo
import com.example.hotelsdatamerger.entity.HotelInfoEntity
import com.example.hotelsdatamerger.utils.JsonUtils
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull


@Service
class DistributedCacheImpl(
    val jsonUtils: JsonUtils,
    val hotelRepo: HotelInfoRepo,
    val redisTemplate: RedisTemplate<String, Any>
) : IDistributedCache {


    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(DistributedCacheImpl::class.java)

    private lateinit var hashOperations: HashOperations<String, String, Map<String,SerializableHotelInfo>>

    @PostConstruct
    fun init() {
        this.hashOperations = redisTemplate.opsForHash()
    }

    override fun put(hashOfRawData: String, processedHotelInfo: FlatHotelInfo) {
        hotelRepo.save(HotelInfoEntity(hashOfRawData, processedHotelInfo))
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

    override fun putMergedData(jsonData: Map<String,FlatHotelInfo>) {
        jsonData.mapValues {( hotelID, hotelInfo) ->
            SerializableHotelInfo(hotelID, jsonUtils.attributeToJsonObject(hotelInfo.content.attributes))
        }
            .also {
                hashOperations.put(RAW_DATA, MERGED_HOTEL_INFO, it)

            }
    }

    override fun getMergedData(): Map<String, FlatHotelInfo>? {
        return hashOperations.get(RAW_DATA, MERGED_HOTEL_INFO)
            ?.mapValues { ( hotelID, hotelInfo) ->
                FlatHotelInfo(hotelInfo.hotelID, InfoObject(jsonUtils.mapToInfoObject(hotelInfo.content)))
            }
    }

    companion object {
        const val RAW_DATA = "raw_data"
        const val MERGED_HOTEL_INFO = "merged_info"
    }

}