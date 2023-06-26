package com.jaramgroupware.gateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaramgroupware.gateway.route.filter.GlobalExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExceptionHandlerConfig(@Autowired val objectMapper: ObjectMapper
) {
    @Bean
    fun globalExceptionHandler(): ErrorWebExceptionHandler? {
        return GlobalExceptionHandler(objectMapper)
    }
}