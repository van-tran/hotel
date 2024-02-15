package com.example.hotelsdatamerger.dto

sealed class AttributeContent(var name: String, val virtualNode: Boolean = false) {
    class PlainString<T>(name: String, val value: T, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
    class ListString(name: String, val value: List<String>, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
    class ListNestedObject(name: String, val value: List<InfoObject>, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
}

data class InfoObject(val attributes: List<AttributeContent>)