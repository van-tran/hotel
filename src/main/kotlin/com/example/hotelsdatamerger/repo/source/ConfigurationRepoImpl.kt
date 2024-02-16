package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.dto.AttributeContentResolver
import com.example.hotelsdatamerger.dto.AttributeDefinition
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.util.ResourceUtils
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets


@Service
class ConfigurationRepoImpl(val objectMapper: ObjectMapper) : IConfigurationRepo{
    override fun getHotelIDKeyName() = "hotel_id"

    fun readJsonString(fileName: String) =
        ClassPathResource(fileName)
            .let { classPathResource ->
                try {
                    val binaryData = FileCopyUtils.copyToByteArray(classPathResource.inputStream)
                    String(binaryData, StandardCharsets.UTF_8)
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw e
                }
            }
    override fun getSources(): List<String> {
        return readJsonString("data/sources.json")
            .let { objectMapper.readValue(it, object : TypeReference<List<String>>() {}) }
    }
//    override fun getSources(): List<String> = objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/sources.json").path),
//        object : TypeReference<List<String>>() {})

    override fun getAttributeSet(): List<AttributeDefinition> =
        objectMapper.readValue(readJsonString("data/attributes.json"),
            object : TypeReference<List<AttributeDefinition>>() {})
    override fun getContentMapper(): List<AttributeContentResolver> =
        objectMapper.readValue(readJsonString("data/content_filter.json"),
            object : TypeReference<List<AttributeContentResolver>>() {})

}