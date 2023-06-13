package com.jaramgroupware.gateway.route.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

/**
 * Request Header에서 user_pk, role_pk를 제거하는 필터
 *
 */
@Component
class CleanRequestFilterFactory : GatewayFilterFactory<CleanRequestFilterFactory.Config> {

    data class Config(
        var isEnable: Boolean? = null
    )

    override fun newConfig(): Config {
        return Config()
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val modifiedRequest = exchange.request.mutate()
                .headers { headers ->
                    headers.remove("user_pk")
                    headers.remove("role_pk")
                }
                .build()
            chain.filter(exchange.mutate().request(modifiedRequest).build())
        }
    }
}