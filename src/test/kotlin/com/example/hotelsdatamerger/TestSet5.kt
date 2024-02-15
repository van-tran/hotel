package com.example.hotelsdatamerger

object TestSet5 {
    val rawData1 =  """[
  {
  "Id": "f8c9",
  "DestinationId": 1122,
  "Name": "Hilton Shinjuku Tokyo",
  "Latitude": "",
  "Longitude": "",
  "Address": "160-0023, SHINJUKU-KU, 6-6-2 NISHI-SHINJUKU, JAPAN",
  "City": "Tokyo",
  "Country": "JP",
  "PostalCode": "160-0023",
  "Description": "Hilton Tokyo is located in Shinjuku, the heart of Tokyo's business, shopping and entertainment district, and is an ideal place to experience modern Japan. A complimentary shuttle operates between the hotel and Shinjuku station and the Tokyo Metro subway is connected to the hotel. Relax in one of the modern Japanese-style rooms and admire stunning city views. The hotel offers WiFi and internet access throughout all rooms and public space.",
  "Facilities": [
  "tv",
    "Pool",
    "WiFi ",
    "BusinessCenter",
    "DryCleaning",
    " Breakfast",
    "Bar",
    "BathTub"
  ]
}
]"""
    val rawData2 = """[
        {
    "id": "f8c9",
    "destination": 1122,
    "name": "Hilton Tokyo Shinjuku",
    "lat": 35.6926,
    "lng": 139.690965,
    "address": null,
    "info": null,
    "amenities": null,
    "images": {
      "rooms": [
        {
          "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i10_m.jpg",
          "description": "Suite"
        },
        {
          "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i11_m.jpg",
          "description": "Suite - Living room"
        }
      ],
      "amenities": [
        {
          "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i57_m.jpg",
          "description": "Bar"
        }
      ]
    }
  }]""".trimMargin()
}