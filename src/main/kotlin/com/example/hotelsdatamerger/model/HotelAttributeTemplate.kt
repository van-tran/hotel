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
)
//{
//
//    class Identifier() : HotelAttribute(
//        name = "id",
//        alternativeNamesRegex = listOf(
//            "id",
//            "hotel(-|_|){0,2}id")
//    )
//    class Location() : HotelAttribute(
//        name = "location",
//        alternativeNamesRegex = listOf("place"),
//        includedAttributesRegex = listOf(
//            "dest(ination){0,1}(_){0,1}(id){0,1}",
//            "lat(itude){0,1}",
//            "long(itude){0,1}",
//            "address",
//            "postalcode",
//            "country",
//            "city"
//        )
//    )
//
//
//    class Description() : HotelAttribute(
//        name = "description",
//        alternativeNamesRegex = listOf(
//            "description(s){0,1}",
//            "detail(s){0,1}",
//            "info(rmation){0,1}"
//        )
//    )
//    class Amenity() : HotelAttribute(
//        name = "amenities",
//        alternativeNamesRegex = listOf("facilities"),
//        includedAttributesRegex = listOf(
//            "general",
//            "room"
//        )
//    )
//    class Image() : HotelAttribute(
//        name = "images",
//        alternativeNamesRegex = listOf(
//            "image(s){0,1}",
//            "photo(s){0,1}"
//        ),
//        includedAttributesRegex = listOf(
//            "room(s){0,1}",
//            "site(s){0,1}"
//        )
//    )
////    class Other() : HotelAttribute(
////        name = "amenities",
////        alternativeNamesRegex = listOf("facilities")
////    )
//}