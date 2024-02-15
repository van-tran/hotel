package com.example.hotelsdatamerger.utils

import com.example.hotelsdatamerger.dto.AttributeContent
import com.example.hotelsdatamerger.dto.AttributeContentResolver
import com.example.hotelsdatamerger.dto.AttributeInfo
import com.example.hotelsdatamerger.dto.InfoObject

sealed class AttributeProcessor() {
    abstract fun standardizeKey(attr: AttributeInfo): AttributeInfo
    abstract fun <T> standardizeValue(attr: AttributeInfo, contentValue: T): T

    class KeyTransformer(private val attributeKeyMapper: Map<String, AttributeInfo>) : AttributeProcessor() {

        override fun standardizeKey(attr: AttributeInfo): AttributeInfo = attributeKeyMapper.get(attr.name) ?: attr

        override fun <T> standardizeValue(attr: AttributeInfo, contentValue: T): T = contentValue
    }

    class ValueTransformer(private val contentMappers: List<AttributeContentResolver>) : AttributeProcessor() {
        override fun standardizeKey(attr: AttributeInfo): AttributeInfo = attr

        override fun <T> standardizeValue(attr: AttributeInfo, contentValue: T): T {
            return when (contentValue) {
                is String -> {
                    contentMappers
                        .fold(contentValue as String) { value, resolver ->
                            if (resolver.attrNameRegex.toRegex().matches(attr.name))
                                value.replace(Regex(resolver.regex), resolver.replacement)
                            else value
                        }
                        .let { it as T }
                }
                // TODO: implement the function for other data types
                else -> contentValue
            }

        }

    }
}