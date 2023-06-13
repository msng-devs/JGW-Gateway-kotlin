package com.jaramgroupware.gateway.route.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.awt.PageAttributes

//TODO : 에러 핸들링
@Component
@Order(-1)
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val response = exchange.response
        response.headers.contentType = PageAttributes.MediaType.APPLICATION_JSON

        val errorResponse = when (ex) {
            // Spring Web Server 관련 오류의 경우 Spring 오류 메시지를 사용
            is ResponseStatusException -> ErrorResponse(code = ErrorCode.FRAME_WORK_INTERNAL_ERROR)
            is BusinessException -> {
                response.statusCode = HttpStatus.valueOf(ex.errorCode.status)
                ErrorResponse(code = ex.errorCode)
            }
            // 그외 오류는 ErrorCode.UNDEFINED_ERROR 기반으로 메시지를 사용
            else -> {
                response.statusCode = HttpStatus.valueOf(ErrorCode.UNDEFINED_ERROR.status)
                ErrorResponse(code = ErrorCode.UNDEFINED_ERROR)
            }
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