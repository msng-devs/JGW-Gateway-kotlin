package com.jaramgroupware.gateway.config.filter

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
class CorsConfig {
    private val ALLOWED_HEADERS =
        "x-requested-with, authorization, Content-Type, Content-Length, Authorization, credential, X-XSRF-TOKEN,set-cookie,access-control-expose-headers"
    private val ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS, PATCH"

    private val MAX_AGE = "7200" //2 hours (2 * 60 * 60)


    @Bean
    fun corsFilter(): WebFilter? {
        return WebFilter { ctx: ServerWebExchange, chain: WebFilterChain ->
            val request = ctx.request
            if (CorsUtils.isCorsRequest(request)) {
                val response = ctx.response
                val headers = response.headers
                if (request.headers.origin == null) {
                    response.statusCode = HttpStatus.BAD_REQUEST
                    return@WebFilter Mono.empty<Void?>()
                }
                headers.add("Access-Control-Allow-Origin", request.headers.origin)
                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS)
                headers.add("Access-Control-Allow-Credentials", "true")
                headers.add("Access-Control-Max-Age", MAX_AGE)
                headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS)
                if (request.method === HttpMethod.OPTIONS) {
                    response.statusCode = HttpStatus.OK
                    return@WebFilter Mono.empty<Void?>()
                }
            }
            chain.filter(ctx)
        }
    }
}