package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.utlis.exception.application.ApplicationErrorCode
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import java.util.*


@Configuration
class GlobalCorsFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun postGlobalFilter(): GlobalFilter? {
        return GlobalFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val response = exchange.response
            response.headers.remove("access-control-allow-credentials")
            response.headers.remove("access-control-allow-origin")
            response.headers.remove("Access-Control-Request-Method")
            response.headers.remove("access-control-allow-credentials")
            if (exchange.request.headers.origin == null) {
//                throw ApplicationException(
//                    errorCode = ApplicationErrorCode.ORIGIN_MISSING,
//                    message = "사용자의 헤더에서 origin을 찾을 수 없습니다.")
                response.headers.accessControlAllowOrigin = "localhost"
            } else {
                response.headers.accessControlAllowOrigin = exchange.request.headers.origin
            }
            response.headers.accessControlAllowCredentials = true
            response.headers.accessControlAllowMethods = listOf(
                HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.DELETE,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.OPTIONS
            )
            response.headers.accessControlAllowHeaders = listOf(
                "x-requested-with",
                "authorization",
                "Content-Type",
                "Content-Length",
                "Authorization",
                "credential",
                "X-XSRF-TOKEN",
                "set-cookie",
                "access-control-expose-headers"
            )
            chain.filter(exchange.mutate().response(response).build())
        }
    }
}