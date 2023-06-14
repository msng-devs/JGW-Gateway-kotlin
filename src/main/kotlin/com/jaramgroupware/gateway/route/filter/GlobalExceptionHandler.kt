package com.jaramgroupware.gateway.route.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaramgroupware.gateway.dto.error.ErrorResponseDto
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.ResolvableType
import org.springframework.core.annotation.Order
import org.springframework.core.codec.Hints
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Component
import org.springframework.web.ErrorResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.awt.PageAttributes
import java.time.LocalDateTime

@Component
@Order(-1)
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response
        val request = exchange.request

        response.headers.contentType = MediaType.APPLICATION_JSON

        val path = request.path.value()
        val errorResponse = when (ex) {

            is ApplicationException -> ErrorResponseDto(ex, path)
            is AuthenticationException -> ErrorResponseDto(ex, path)
            // 그외 오류는 ErrorCode.UNDEFINED_ERROR 기반으로 메시지를 사용
            else -> ErrorResponseDto(
                timestamp = LocalDateTime.now(),
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                errorCode = "GW-0000",
                error = "UNDEFINED_ERROR",
                message = "서버에 알 수 없는 에러가 발생했습니다.",
                path = path
            )
        }

        return response.writeWith(
            Jackson2JsonEncoder(objectMapper).encode(
                Mono.just(errorResponse),
                response.bufferFactory(),
                ResolvableType.forInstance(errorResponse),
                MediaType.APPLICATION_JSON,
                Hints.from(Hints.LOG_PREFIX_HINT, exchange.logPrefix)
            )
        )
    }

}