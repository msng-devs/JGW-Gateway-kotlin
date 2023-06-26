package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.utlis.GatewayEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class GatewayRefreshFilterFactory(
    @Autowired val gatewayEventPublisher: GatewayEventPublisher
)  : GatewayFilterFactory<GatewayRefreshFilterFactory.Config> {

    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config(
        var isEnable: Boolean? = true,
    )

    override fun newConfig(): Config {
        return Config()
    }

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val uid = request.headers["user_pk"]?.firstOrNull()?.toString()
            val role = request.headers["role_pk"]?.firstOrNull()?.toIntOrNull()

            logger.info("GatewayRefresh! uid:$uid, role:$role")
            gatewayEventPublisher.refreshRoute()

            chain.filter(exchange)
        }

    }
}