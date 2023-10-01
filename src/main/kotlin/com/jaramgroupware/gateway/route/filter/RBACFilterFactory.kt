package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.utlis.exception.application.ApplicationErrorCode
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationErrorCode
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import com.jaramgroupware.gateway.utlis.logging.TemplateLogger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.*

@Component
class RBACFilterFactory :
    GatewayFilterFactory<RBACFilterFactory.Config> {

    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config(
        var role: Int? = null
    )

    override fun newConfig(): Config {
        return RBACFilterFactory.Config()
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request


            val uid = request.headers["user_pk"]?.firstOrNull()?.toString()
            val role = request.headers["role_pk"]?.firstOrNull()?.toIntOrNull()

            if(role!! < config.role!!){
                throw AuthenticationException(errorCode = AuthenticationErrorCode.INVALID_ROLE, message = "해당 API를 사용할 권한이 없습니다.")
            }

            val requestLog = TemplateLogger.createRequestLog(request)
            logger.info("RBACFilter Pass.(uid:$uid)-$requestLog")
            chain.filter(exchange.mutate().request(request).build())
        }
    }

}