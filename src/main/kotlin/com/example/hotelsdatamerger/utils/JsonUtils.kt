package com.example.hotelsdatamerger.utils

import com.example.hotelsdatamerger.dto.AttributeContent
import com.example.hotelsdatamerger.dto.InfoObject
import com.example.hotelsdatamerger.facade.SearchingFacade
import com.example.hotelsdatamerger.repo.source.IConfigurationRepo
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class JsonUtils(
    val objectMapper: ObjectMapper,
    val configurationRepo: IConfigurationRepo
) {

    val logger: Logger = LoggerFactory.getLogger(SearchingFacade::class.java)

    fun objectToString(anyObj: Any): String = objectMapper.writeValueAsString(anyObj)
    fun parseJsonString(jsonString: String): List<InfoObject> {

        return objectMapper.readValue(jsonString, object : TypeReference<List<Map<String, Any?>>>() {})
            ?.map {
                flatten("",
                    it.filterNot { (key, value) -> value == null }
                        .map { (key, value) -> key to value!! })
            }
            ?.map { InfoObject(it) }
            ?.also {
                logger.info("flatten map : {}", it)
            }
            ?: emptyList()

    }

    fun mapToInfoObject(mapData: Map<String, Any?>) =
        flatten("",
            mapData.filterNot { (key, value) -> value == null }
                .map { (key, value) -> key to value!! })

    fun attributeToJsonObject(attributes: List<AttributeContent>): Map<String, Any> {
        return attributes
            .groupBy {
                it.name.split(".")[0]
            }
            .mapValues { (key, lstOfAttributes) ->
                if (lstOfAttributes.all { it.name.length > key.length }) {
                    lstOfAttributes.map {
                        it.name = it.name.substringAfter('.')
                        it
                    }.let {
                        attributeToJsonObject(it)
                    }
                } else {
                    lstOfAttributes
                        .first()
                        .let {
                            when (it) {
                                is AttributeContent.PlainString<*> -> it.value
                                is AttributeContent.ListString -> it.value
                                is AttributeContent.ListNestedObject -> it.value
                                    .map { attributeToJsonObject(it.attributes) }
                            }
                        } ?: ""
                }
            }
    }

    fun flatten(root: String, map: List<Pair<String, Any>>): List<AttributeContent> {
        val base = if (root.isNotBlank()) "${root}." else ""
        val directValues = map.filterNot { node ->
            node.second is List<*> ||
                    node.second is Map<*, *>
        }.map { (name, value) ->
            AttributeContent.PlainString("${base}${name.toLowerCase()}", value)
        }
        val directCollectionValues = map.filter {
            it.second is List<*>
        }.map { (name, value) ->
            (value as List<*>)
                ?.filterNotNull()
                ?.let {
                    val content: AttributeContent? = when {
                        it.all { it is String } -> AttributeContent.ListString(
                            "${base}${name.toLowerCase()}",
                            it.map { it.toString() })

                        it.all { it is Map<*, *> } -> it.map {
                            (it as Map<String, Any>)
                                .map {
                                    it.key to it.value
                                }
                                .let {
                                    flatten("${root}-", it)
                                }.let {
                                    InfoObject(it)
                                }
                        }.let {
                            AttributeContent.ListNestedObject("${base}${name.toLowerCase()}", it)
                        }

                        else -> {
                            logger.info("Invalid json format.")
                            null
                        }
                    }
                    return@let content
                }
        }.filterNotNull() ?: emptyList()

        val childrenValues = map.filter {
            it.second is Map<*, *>
        }.flatMap { (name, mapValues) ->
            flatten("${base}${name}",
                (mapValues as Map<String, *>)
                    .filter { it.value != null }
                    .map { it.key to it.value!! })
        }
        return directValues + directCollectionValues + childrenValues
    }


}