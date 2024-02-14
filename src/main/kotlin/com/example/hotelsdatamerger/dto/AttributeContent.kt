package com.example.hotelsdatamerger.dto

sealed class AttributeContent(var name: String, val virtualNode: Boolean = false) {
    class PlainString(name: String, val value: String, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
    class ListString(name: String, val value: List<String>, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
    class ListNestedObject(name: String, val value: List<InfoObject>, virtualNode: Boolean = false) : AttributeContent(name, virtualNode)
}

data class InfoObject(val attributes: List<AttributeContent>)