package com.jaramgroupware.gateway.route.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaramgroupware.gateway.dto.error.ErrorResponseDto
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import com.jaramgroupware.gateway.utlis.logging.TemplateLogger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.Order
import org.springframework.core.codec.Hints
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponse
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.awt.PageAttributes
import java.time.LocalDateTime

@Configuration
class GlobalRequestLoggingFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun postGlobalFilter(): GlobalFilter? {
        return GlobalFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val requestLog = TemplateLogger.createRequestLog(request)
            logger.info(requestLog)

            chain.filter(exchange)
        }
    }
}