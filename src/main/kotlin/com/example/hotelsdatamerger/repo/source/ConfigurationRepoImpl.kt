package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.dto.AttributeContentResolver
import com.example.hotelsdatamerger.dto.AttributeDefinition
import com.example.hotelsdatamerger.model.HotelAttributeTemplate
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.File

@Service
class ConfigurationRepoImpl(val objectMapper: ObjectMapper) : IConfigurationRepo{
    override fun getHotelIDKeyName() = "hotel_id"

    override fun getSources(): List<String> = objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/sources.json").path),
        object : TypeReference<List<String>>() {})

    override fun getHotelInfoTemplate() : List<HotelAttributeTemplate> =
        objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/template.json").path),
            object : TypeReference<List<HotelAttributeTemplate>>() {})
    override fun getAttributeSet(): List<AttributeDefinition> =
        objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/attributes.json").path),
            object : TypeReference<List<AttributeDefinition>>() {})
    override fun getContentMapper(): List<AttributeContentResolver> =
        objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/content_filter.json").path),
            object : TypeReference<List<AttributeContentResolver>>() {})

}