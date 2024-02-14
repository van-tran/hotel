package com.example.hotelsdatamerger.model


/*
* This model keeps the data to detect & classify hotel by attributes.
* This data is temporarily defined in code, and should be store in other places for flexibility.
* Hence, we could update attributes once data changed from sources
* */
data class HotelAttributeTemplate(val name: String,
    // will be preprocessed (trim, lowercase, ...) before scanning
                                  val alternativeNamesRegex: List<String> = emptyList(),
                                  val includedAttributesRegex: List<String> = emptyList()
){
//    fun matchingKey(key : String)
}