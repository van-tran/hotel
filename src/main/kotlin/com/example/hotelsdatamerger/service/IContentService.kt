package com.example.hotelsdatamerger.service

import com.example.hotelsdatamerger.dto.AttributeContent
import com.example.hotelsdatamerger.dto.InfoObject

interface IContentService {
    fun scoreContent(attribute: InfoObject): Int
    fun normalize(contentValue: String): String
    fun scoreContent(attribute: String): Int
    fun normalize(contentValue: InfoObject): String
}