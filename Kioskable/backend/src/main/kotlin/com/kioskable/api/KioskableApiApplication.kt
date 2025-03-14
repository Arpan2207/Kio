package com.kioskable.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KioskableApiApplication

fun main(args: Array<String>) {
    runApplication<KioskableApiApplication>(*args)
} 