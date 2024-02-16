package com.example.hotelsdatamerger

import com.example.hotelsdatamerger.dto.AttributeContent
import com.example.hotelsdatamerger.service.IHotelInfoService
import com.example.hotelsdatamerger.utils.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HotelsDataMergerApplicationTests {


    // Initializing instance of Logger for Service
    val logger: Logger = LoggerFactory.getLogger(HotelsDataMergerApplicationTests::class.java)

    @Autowired
    lateinit var jsonUtils: JsonUtils

    @Autowired
    lateinit var hotelInfoService: IHotelInfoService

    @Test
    fun contextLoads() {
    }

    /*
    * Test 1
    * */
    @Test
    fun flattenJson() {
        jsonUtils.parseJsonString(TestSet1.rawData)
            .also {
                logger.info("result {}",jsonUtils.objectMapper.writeValueAsString(it))
                assert(it[0].attributes.size == 11)
                Assertions.assertNotNull(it[0].attributes.find { it.name == "hotel_id" })
                Assertions.assertNotNull(it[0].attributes.find { it.name == "destination_id" })
                Assertions.assertNotNull(it[0].attributes.find { it.name == "amenities.general" })
            }
    }


    /*
    * Test 2
    * */
    @Test
    fun standardizeAtributeName() {
        jsonUtils.parseJsonString(TestSet2.rawData)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }
            .also {
                logger.info("result {}", jsonUtils.objectMapper.writeValueAsString(it))
                assert(it[0].content.attributes.size == 10)
                Assertions.assertNotNull(it[0].content.attributes.find { it.name == "hotel_id" })
                Assertions.assertNotNull(it[0].content.attributes.find { it.name == "destination_id" })
                it[0].content.attributes.find { it.name == "amenities.other" }
                    .also {
                        Assertions.assertNotNull(it)
                        assert(it!!.virtualNode == true)
                    }

            }
    }

    /*
    * Test 3
    * */
    @Test
    fun mergeListStringsFromDiffSources() {
        val dataFromSource1 = jsonUtils.parseJsonString(TestSet3.rawData1)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 1 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }

        val dataFromSource2 = jsonUtils.parseJsonString(TestSet3.rawData2)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 2 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }
        assert(dataFromSource1.hotelID == dataFromSource2.hotelID)
        runBlocking(Dispatchers.Default) {
            hotelInfoService.mergeAttributesFromDifferentSource(
                dataFromSource1.hotelID,
                listOf(dataFromSource1, dataFromSource2)
            )
                .also {
                    logger.info("result {}", jsonUtils.objectMapper.writeValueAsString(it))
                    assert(it.hotelID == dataFromSource1.hotelID)
                    Assertions.assertNotNull(it.content.attributes.find { it.name == "hotel_id" })
                    Assertions.assertNotNull(it.content.attributes.find { it.name == "destination_id" })
                    it.content.attributes.find { it.name == "amenities.other" }
                        .also {
                            Assertions.assertNotNull(it)
                            assert(it!!.virtualNode == true)
                        }
                }
        }
    }

    /*
    * Test 4
    * */
    @Test
    fun mergeListObjectsFromDiffSources() {
        val dataFromSource1 = jsonUtils.parseJsonString(TestSet4.rawData1)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 1 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }

        val dataFromSource2 = jsonUtils.parseJsonString(TestSet4.rawData2)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 2 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }
        assert(dataFromSource1.hotelID == dataFromSource2.hotelID)
        runBlocking(Dispatchers.Default) {
            hotelInfoService.mergeAttributesFromDifferentSource(
                dataFromSource1.hotelID,
                listOf(dataFromSource1, dataFromSource2)
            )
                .also {
                    logger.info("result {}", jsonUtils.objectMapper.writeValueAsString(it))
                    assert(it.hotelID == dataFromSource1.hotelID)
                    Assertions.assertNotNull(it.content.attributes.find { it.name == "hotel_id" })
                    Assertions.assertNotNull(it.content.attributes.find { it.name == "destination_id" })
                    it.content.attributes.find { it.name == "images.rooms" }
                        .let { it as AttributeContent.ListNestedObject }
                        .also {
                            Assertions.assertNotNull(it)
                            assert(it.value.size == 3)
                        }
                }
        }
    }


    /*
    * Test 5
    * */
    @Test
    fun finalObjectFromDiffSources() {
        val dataFromSource1 = jsonUtils.parseJsonString(TestSet5.rawData1)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 1 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }

        val dataFromSource2 = jsonUtils.parseJsonString(TestSet5.rawData2)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }.first()
            .also {
                logger.info("data 2 {}", jsonUtils.objectMapper.writeValueAsString(it))
            }
        assert(dataFromSource1.hotelID == dataFromSource2.hotelID)
        runBlocking(Dispatchers.Default) {
            hotelInfoService.mergeAttributesFromDifferentSource(
                dataFromSource1.hotelID,
                listOf(dataFromSource1, dataFromSource2)
            )
                .let {
                    jsonUtils.attributeToJsonObject(it.content.attributes)
                }
                .also {
                    logger.info("result {}", jsonUtils.objectMapper.writeValueAsString(it))
                    assert(it.keys.size == 7)
                    Assertions.assertNotNull(it["amenities"])
                    assert(it["amenities"] is List<*> &&
                            (it["amenities"] as List<*>)
                                ?.map { it as String }
                                ?.none {
                                    it.startsWith(" ")
                                } ?: false
                    )

                }
        }
    }


    /*
    * Test 6
    * */
    @Test
    fun buildResponseFromFlattenMap() {
        jsonUtils.parseJsonString(TestSet6.rawData1)
            .let {
                runBlocking(Dispatchers.Default) {
                    hotelInfoService.standardizeAttributeKeys(it)
                }
            }
            .map {
                jsonUtils.attributeToJsonObject( it.content.attributes )
            }
            .also {
                logger.info("result {}", jsonUtils.objectMapper.writeValueAsString(it))
                assert(it[0].get("hotel_id") == "iJhz")
                assert(it[0].get("amenities") is List<*>)
            }
    }
}
