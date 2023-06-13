package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.dto.member.MemberResponseDto
import com.jaramgroupware.gateway.security.firebase.FirebaseClient
import com.jaramgroupware.gateway.service.MemberService
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationErrorCode
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import com.jaramgroupware.gateway.utlis.logging.TemplateLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.Function

@Component
class AuthenticationFilterFactory(
    @Autowired val firebaseClient: FirebaseClient,
    @Autowired val memberService: MemberService,

) : GatewayFilterFactory<AuthenticationFilterFactory.Config>{

    private val logger = LoggerFactory.getLogger(RequestLoggingFilterFactory::class.java)

    data class Config(
        var isOnlyToken: Boolean = false,
        var isOptional: Boolean = false
    )

    override fun newConfig(): Config {
        return newConfig();
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            val request = exchange.request
            val token = checkContainsAndExtractToken(request);

            val uid = firebaseClient.verifyAndDecodeToken(token)

            when(config.isOnlyToken) {
                true -> {
                    val newRequest = exchange.request
                        .mutate()
                        .header("user_pk", uid)
                        .build()
                    val requestLog = TemplateLogger.createRequestLog(request)
                    logger.info("AuthenticationFilter Pass.(uid:$uid isOnlyToken - enable / isOptional - disable ) -$requestLog")
                    return@GatewayFilter chain.filter(exchange.mutate().request(newRequest).build())
                }

                false -> {
                    return@GatewayFilter memberService.findMemberById(uid).flatMap { it ->

                        if (!config.isOptional) { checkUidAndRoleIsNotNull(it) }

                        val newRequest = exchange.request
                            .mutate()
                            .header("user_pk", it.id?:"null")
                            .header("role_pk", it.roleId.toString()?:"null")
                            .build()

                        val requestLog = TemplateLogger.createRequestLog(request)
                        logger.info("AuthenticationFilter Pass.(uid:$uid isOnlyToken - disable / isOptional - {} ) -$requestLog", if(config.isOptional) "enable" else "disable")
                        return@flatMap chain.filter(exchange.mutate().request(newRequest).build())
                    }

                }
            }
        }
    }


    fun checkContainsAndExtractToken(request: ServerHttpRequest): String {

        val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader?.startsWith("Bearer ") == true) {
            return authorizationHeader.substringAfter("Bearer ").trim();
        }

        throw AuthenticationException(message = "토큰을 헤더에서 찾을 수 없거나, 올바른 형식이 아닙니다.", errorCode = AuthenticationErrorCode.TOKEN_NOT_FOUND);
    }


    fun checkUidAndRoleIsNotNull(dto:MemberResponseDto){

        if (dto.isNull()) {
            throw AuthenticationException(
            message = "토큰에 해당하는 유저가 없습니다.",
            errorCode = AuthenticationErrorCode.USER_NOT_FOUND)
        }

    }
}