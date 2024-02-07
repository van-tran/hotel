package com.example.hotelsdatamerger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class HotelsDataMergerApplication

fun main(args: Array<String>) {
	runApplication<HotelsDataMergerApplication>(*args)
}


@RestController
class MessageController {
	@GetMapping("/")
	fun index(@RequestParam(value = "name") name: String) = "Hello, $name!"
}