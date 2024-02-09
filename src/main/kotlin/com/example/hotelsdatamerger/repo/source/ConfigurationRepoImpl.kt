package com.example.hotelsdatamerger.repo.source

import com.example.hotelsdatamerger.model.HotelAttributeTemplate
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.File

@Service
class ConfigurationRepoImpl(val objectMapper: ObjectMapper) : IConfigurationRepo{

    override fun getSources(): List<String> = objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/sources.json").path),
        object : TypeReference<List<String>>() {})

    override fun getHotelInfoTemplate() : List<HotelAttributeTemplate> =
        objectMapper.readValue(File(ResourceUtils.getFile("classpath:data/template.json").path),
            object : TypeReference<List<HotelAttributeTemplate>>() {})

}