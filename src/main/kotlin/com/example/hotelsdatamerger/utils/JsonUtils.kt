package com.example.hotelsdatamerger.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service


@Service
class JsonUtils(val objectMapper: ObjectMapper) {

    fun jsonFileToMapGson( jsonString: String): List<Map<String, Any>> {

        return objectMapper.readValue(jsonString, object : TypeReference<List<Map<String, Any>>>() {})
    }


}