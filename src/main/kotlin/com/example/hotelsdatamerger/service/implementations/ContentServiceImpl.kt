package com.example.hotelsdatamerger.service.implementations

import com.example.hotelsdatamerger.dto.AttributeContent
import com.example.hotelsdatamerger.dto.InfoObject
import com.example.hotelsdatamerger.service.IContentService
import com.example.hotelsdatamerger.utils.JsonUtils
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class ContentServiceImpl(val jsonUtils: JsonUtils) : IContentService {

//    this function will score content based on some predefined logic, such as : language, length, spelling, terms, ...
    override fun scoreContent(attribute: InfoObject) : Int {
        return Random(100).nextInt(0, 99)
    }

    override fun scoreContent(attribute: String) : Int {
        return Random(100).nextInt(0, 99)
    }
    override fun <T> scoreContent(attribute: T) : Int {
        return Random(100).nextInt(0, 99)
    }
    override fun normalize(contentValue: String) : String {
        return contentValue.trim()
            .lowercase(Locale.getDefault())
            .replace(Regex("(?<=\\w)( |\\t|\\n|\\r){2,}(?=\\w)")," ")
    }
    override fun normalize(contentValue: InfoObject) : String {
        return jsonUtils.objectToString(contentValue)
    }
}