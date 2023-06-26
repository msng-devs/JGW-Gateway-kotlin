package com.jaramgroupware.gateway.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/ping")
    fun ping():String {
        return "PONG"
    }

}