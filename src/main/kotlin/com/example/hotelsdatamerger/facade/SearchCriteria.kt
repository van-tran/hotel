package com.example.hotelsdatamerger.facade

sealed class SearchCriteria() {
    class ByHotelID(val hoteIDs : List<String>) : SearchCriteria()
    class ByDestinationID(val destIDs : List<String>) : SearchCriteria()
}