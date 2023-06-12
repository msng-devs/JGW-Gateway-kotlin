package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.security.firebase.FirebaseClient
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationErrorCode
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import jakarta.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthenticationFilterFactory(
    @Autowired val firebaseClient: FirebaseClient

) : GatewayFilterFactory<AuthenticationFilterFactory.Config>{

    class Config {
        @NotEmpty
        val isOnlyToken = false

        @NotEmpty
        val isOptional = false
    }

    override fun newConfig(): Config {
        return newConfig();
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            val request = exchange.request
            val token = checkContainsAndExtractToken(request);

            when(config.isOnlyToken) {
                true -> {
                    processOnlyTokenValid(token);
                }
                false -> {
                    if (token.isNotEmpty()) {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        return@GatewayFilter exchange.response.setComplete()
                    }
                }
            }

            chain.filter(exchange)
        }
    }

    //토큰 존재 여부 검증 및 추출
    fun checkContainsAndExtractToken(request: ServerHttpRequest): String {

        val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader?.startsWith("Bearer ") == true) {
            return authorizationHeader.substringAfter("Bearer ").trim();
        }

        throw AuthenticationException(message = "토큰을 헤더에서 찾을 수 없거나, 올바른 형식이 아닙니다.", errorCode = AuthenticationErrorCode.TOKEN_NOT_FOUND);
    }

    //옵션이 only token 일 경우, 토큰 유효성만 검증함.
    fun processOnlyTokenValid(token:String){

    }
}