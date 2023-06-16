package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.dto.tokenCache.TokenCacheAddRequestDto
import com.jaramgroupware.gateway.dto.tokenCache.TokenCacheResponseDto
import com.jaramgroupware.gateway.security.firebase.FirebaseClient
import com.jaramgroupware.gateway.service.MemberService
import com.jaramgroupware.gateway.service.TokenCacheService
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationErrorCode
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationErrorCode
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import com.jaramgroupware.gateway.utlis.logging.TemplateLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Component
class AuthenticationFilterFactory(
    @Autowired val firebaseClient: FirebaseClient,
    @Autowired val memberService: MemberService,
    @Autowired val tokenCacheService: TokenCacheService

) : GatewayFilterFactory<AuthenticationFilterFactory.Config> {

    private val logger = LoggerFactory.getLogger(RequestLoggingFilterFactory::class.java)

    data class Config(
        var mode: AuthFilterMode? = null,
    )

    override fun newConfig(): Config {
        return Config()
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            when (config.mode) {
                AuthFilterMode.ONLY_TOKEN -> {
                    return@GatewayFilter authenticationOnlyToken(exchange = exchange, chain = chain)
                }

                AuthFilterMode.OPTIONAL -> {
                    return@GatewayFilter authenticationTokenOptional(exchange = exchange, chain = chain)
                }

                AuthFilterMode.FULLY -> {
                    return@GatewayFilter authenticationFully(exchange = exchange, chain = chain)
                }

                else -> {
                    throw ApplicationException(
                        message = "게이트웨이 설정에 오류가 발생했습니다.",
                        errorCode = ApplicationErrorCode.UNKNOWN_ERROR
                    )
                }
            }
        }
    }

    private fun checkContainsAndExtractToken(request: ServerHttpRequest): String {

        val token = extractToken(request)

        if (token.isNotEmpty()) {
            return token
        }

        throw AuthenticationException(
            message = "토큰을 헤더에서 찾을 수 없거나, 올바른 형식이 아닙니다.",
            errorCode = AuthenticationErrorCode.TOKEN_NOT_FOUND
        )
    }

    private fun extractToken(request: ServerHttpRequest): String {

        val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authorizationHeader?.startsWith("Bearer ") == true) {
            return authorizationHeader.substringAfter("Bearer ").trim()
        }

        return ""
    }

    /**
     * 오직 토큰만을 검증합니다.
     *
     * @param chain
     * @param exchange
     * @return
     */
    private fun authenticationOnlyToken(
        chain: GatewayFilterChain,
        exchange: ServerWebExchange
    ): Mono<Void> {
        val request = exchange.request
        val token = checkContainsAndExtractToken(request)

        lateinit var uid: String

        //check cache
        val cache = tokenCacheService.getCache(token)

        when (cache.isNull()) {
            true -> {
                logger.debug("AuthenticationFilter Token Cache Not Found. - token : $token")
                val tokenInfo = firebaseClient.verifyAndDecodeToken(token, true)
                val tokenCacheAddRequestDto = TokenCacheAddRequestDto(
                    token = token,
                    uid = tokenInfo.uid!!,
                    ttl = Duration.between(LocalDateTime.now(), tokenInfo.exp).toMillis(),
                    roleId = null
                )
                tokenCacheService.addCache(tokenCacheAddRequestDto)

                uid = tokenInfo.uid
            }

            false -> {
                uid = cache.uid
            }
        }
        val newRequest = createNewRequest(request = exchange.request, uid = uid, roleId = null, mode = "isOnlyToken")
        return chain.filter(exchange.mutate().request(newRequest).build())
    }

    /**
     * 토큰 유효성 검증 및 유저 정보까지 체크합니다.
     *
     * @param chain
     * @param exchange
     * @return
     */
    private fun authenticationFully(chain: GatewayFilterChain, exchange: ServerWebExchange): Mono<Void> {
        val request = exchange.request
        val token = checkContainsAndExtractToken(request)
        //check cache
        val cache = tokenCacheService.getCache(token)

        when (cache.getCacheType()) {
            TokenCacheResponseDto.CacheType.NOT_VALID -> {
                logger.debug("AuthenticationFilter Token Cache Not Found. - token : $token")
                val tokenInfo = firebaseClient.verifyAndDecodeToken(token, true)

                val role = memberService.findMemberById(tokenInfo.uid!!).flatMap {
                    if(it.isNull()){
                        throw AuthenticationException(
                            message = "존재하지 않는 유저입니다.",
                            errorCode = AuthenticationErrorCode.USER_NOT_FOUND
                        )
                    }
                    return@flatMap Mono.just(it.roleId!!)
                }.onErrorMap { handleException(it) }

                val newRequest = role.flatMap {
                    val cacheAddRequestDto = TokenCacheAddRequestDto(
                        token = token,
                        uid = tokenInfo.uid,
                        ttl = Duration.between(LocalDateTime.now(), tokenInfo.exp).toMillis(),
                        roleId = it
                    )
                    tokenCacheService.addCache(cacheAddRequestDto)

                    return@flatMap Mono.just(
                        createNewRequest(
                            request = exchange.request,
                            uid = tokenInfo.uid,
                            roleId = it,
                            mode = "Fully"
                        )
                    )
                }.onErrorMap { handleException(it) }

                return newRequest.flatMap { chain.filter(exchange.mutate().request(it).build()) }

            }

            TokenCacheResponseDto.CacheType.FULLY -> {
                val newRequest =
                    createNewRequest(request = exchange.request, uid = cache.uid, roleId = cache.roleId, mode = "Fully")
                return chain.filter(exchange.mutate().request(newRequest).build())
            }

            // 토큰 상태가 INVALID 일 경우와 유사한 동작을 하지만, 다른 동작이 있음.
            // 해당 상태에서는 기존 캐시를 제거하고, 새로운 캐시로 수정함.
            TokenCacheResponseDto.CacheType.ONLY_TOKEN -> {
                logger.debug("AuthenticationFilter Update Token. - token : $token")

                return memberService.findMemberById(cache.uid).flatMap { it ->
                    assert(!it.isNull())

                    val cacheAddRequestDto = TokenCacheAddRequestDto(
                        token = token,
                        uid = cache.uid,
                        ttl = cache.ttl!!,
                        roleId = it.roleId
                    )

                    tokenCacheService.deleteCache(token)
                    tokenCacheService.addCache(cacheAddRequestDto)

                    val newRequest =
                        createNewRequest(request = exchange.request, uid = it.id, roleId = it.roleId, mode = "Fully")
                    return@flatMap chain.filter(exchange.mutate().request(newRequest).build())
                }.onErrorMap { handleException(it) }
            }
        }
    }

    /**
     * 해당 토큰이 유효하고, 유저 정보 체크도 성공하면, 해당 정보를 리턴하지만 실패하면 null 정보를 보냅니다.
     *
     * @param chain
     * @param exchange
     * @return
     */
    private fun authenticationTokenOptional(chain: GatewayFilterChain, exchange: ServerWebExchange): Mono<Void> {

        val request = exchange.request
        val token = extractToken(request)

        //토큰이 아에 없을 경우
        if (token.isEmpty()) {
            val newRequest = createNewRequest(request = request, uid = null, roleId = null, mode = "isOptional")
            return chain.filter(exchange.mutate().request(newRequest).build())
        }

        //check cache
        val cache = tokenCacheService.getCache(token)

        when (cache.getCacheType()) {
            TokenCacheResponseDto.CacheType.NOT_VALID -> {
                val tokenInfo = firebaseClient.verifyAndDecodeToken(token, false)

                return if (tokenInfo.isNull()) {
                    val newRequest = createNewRequest(request = request, uid = null, roleId = null, mode = "isOptional")
                    chain.filter(exchange.mutate().request(newRequest).build())
                } else {
                    val tokenCacheAddRequestDto = TokenCacheAddRequestDto(
                        token = token,
                        uid = tokenInfo.uid!!,
                        ttl = Duration.between(LocalDateTime.now(), tokenInfo.exp).toMillis(),
                        roleId = null
                    )
                    tokenCacheService.addCache(tokenCacheAddRequestDto)
                    findMemberAndCreateOptionalRequest(
                        request = request,
                        uid = cache.uid,
                        chain = chain,
                        exchange = exchange
                    )
                }
            }

            TokenCacheResponseDto.CacheType.FULLY -> {
                val newRequest =
                    createNewRequest(request = request, uid = cache.uid, roleId = cache.roleId, mode = "isOptional")
                return chain.filter(exchange.mutate().request(newRequest).build())
            }

            TokenCacheResponseDto.CacheType.ONLY_TOKEN -> {
                return findMemberAndCreateOptionalRequest(
                    request = request,
                    uid = cache.uid,
                    chain = chain,
                    exchange = exchange
                )
            }
        }
    }

    private fun handleException(exception: Throwable):Throwable {
        logger.info("AuthenticationFilter Exception. - ${exception.javaClass} / ${exception.message}")
        when (exception) {
            is AuthenticationException -> {
                throw exception
            }
            is ApplicationException -> {
                throw exception
            }
            else -> {
                throw ApplicationException(
                    message = "알 수 없는 에러가 발생했습니다.",
                    errorCode = ApplicationErrorCode.UNKNOWN_ERROR
                )
            }
        }
    }

    private fun createNewRequest(
        request: ServerHttpRequest,
        uid: String?,
        roleId: Int?,
        mode: String
    ): ServerHttpRequest {
        val newRequest = request
            .mutate()
            .header("user_pk", uid ?: "null")
            .header("role_pk", roleId.toString())
            .build()

        val requestLog = TemplateLogger.createRequestLog(request)
        logger.info("AuthenticationFilter Pass. ( $mode ) -$requestLog")
        return newRequest
    }

    private fun findMemberAndCreateOptionalRequest(
        request: ServerHttpRequest,
        uid: String,
        chain: GatewayFilterChain,
        exchange: ServerWebExchange
    ): Mono<Void> {
        return memberService.findMemberById(uid).flatMap {
            val roleId = it.roleId
            val newRequest = createNewRequest(request = request, uid = uid, roleId = roleId, mode = "isOptional")
            return@flatMap chain.filter(exchange.mutate().request(newRequest).build())
        }.onErrorMap { handleException(it) }
    }

    enum class AuthFilterMode {
        FULLY, ONLY_TOKEN, OPTIONAL
    }
}


